/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.casc.CascClientSubscribeCallback;
import org.serviceconnector.casc.CascSCSubscribeCallback;
import org.serviceconnector.casc.CascSCUnsubscribeCallback;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.casc.ClnSubscribeCascSubscribedCallback;
import org.serviceconnector.casc.ClnUnsubscribeCascSubscribedCallback;
import org.serviceconnector.cmd.sc.ClnSubscribeCommandCallback;
import org.serviceconnector.cmd.sc.ClnUnsubscribeCommandCallback;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

public class CascadedSC extends Server implements IStatefulServer {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CascadedSC.class);

	/** The subscriptions, list of subscriptions allocated on cascaded SC. */
	private List<AbstractSession> subscriptions;
	private static SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();

	public CascadedSC(InetSocketAddress socketAddress, String serverName, int portNr, int maxConnections, String connectionType,
			int keepAliveInterval) {
		// TODO JOT keep alive
		super(ServerType.CASCADED_SC, socketAddress, serverName, portNr, maxConnections, connectionType, 0);
		this.serverKey = serverName;
		this.subscriptions = Collections.synchronizedList(new ArrayList<AbstractSession>());
	}

	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	public void deleteSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, msgToForward);
		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	public void execute(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnExecuteCall executeCall = new SCMPClnExecuteCall(this.requester, msgToForward);
		try {
			executeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// send data failed
			callback.receive(e);
		}
	}

	public void echo(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPEchoCall echoCall = new SCMPEchoCall(this.requester, msgToForward);
		try {
			echoCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// echo failed
			callback.receive(e);
		}
	}

	public boolean tryAcquirePermitOnCascClientSemaphore(CascadedClient cascClient, int oti, ISCMPMessageCallback callback) {
		boolean permit = false;
		Semaphore cascClientSemaphore = cascClient.getCascClientSemaphore();
		try {
			logger.trace("acquire permit callback=" + callback.getClass());
			permit = cascClientSemaphore.tryAcquire(oti, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			// thread interrupted during acquire a permit on semaphore
			callback.receive(ex);
			logger.warn("thread interrupted during acquire a permit on semaphore service=" + cascClient.getServiceName(), ex);
			return false;
		}
		if (permit == false) {
			// thread didn't get a permit in time
			callback.receive(new IdleTimeoutException("oti expired. operation - could not be completed."));
			logger.warn("thread didn't get a permit in time service=" + cascClient.getServiceName());
			return false;
		}
		if (cascClient.isDestroyed() == true) {
			// cascaded client got destroyed in the meantime, stop operation
			callback.receive(new IdleTimeoutException("oti expired. operation - could not be completed."));
			// release permit
			cascClientSemaphore.release();
			logger.warn("cascaded client got destroyed in the meantime, stop operation service=" + cascClient.getServiceName());
			return false;
		}
		return permit;
	}

	public void clientSubscribe(CascadedClient cascClient, SCMPMessage msgToForward, ClnSubscribeCommandCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);

		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// thread got permit to continue
			if (cascClient.isSubscribed() == false) {
				// cascaded client not subscribed - subscribe
				CascadedPublishService cascPublishService = cascClient.getPublishService();
				// adapt NO_DATA_INTERVAL for cascaded client
				msgToForward.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, cascPublishService.getNoDataIntervalInSeconds());
				SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, msgToForward);
				// store client mask in subscription
				String mask = msgToForward.getHeader(SCMPHeaderAttributeKey.MASK);
				cascClient.setSubscriptionMask(new SubscriptionMask(mask));
				CascClientSubscribeCallback cascCallback = new CascClientSubscribeCallback(callback, cascClient);
				// TODO JOT/JAN what OTI to continue??
				subscribeCall.invoke(cascCallback, oti);
				return;
			}
			// TODO JOT/JAN what OTI to continue??
			// cascaded client already subscribed - special subscribe
			ClnSubscribeCascSubscribedCallback cascCallback = new ClnSubscribeCascSubscribedCallback(callback.getRequest(),
					callback);
			cascCallback.setCascClient(cascClient);
			this.subscribeWithActiveCascadedClient(cascClient, msgToForward, cascCallback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	public void cascadedSCSubscribe(CascadedClient cascClient, SCMPMessage msgToForward, CascSCSubscribeCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// thread got permit to continue
			if (cascClient.isSubscribed() == false) {
				// cascaded client not subscribed - subscribe
				CascadedPublishService cascPublishService = cascClient.getPublishService();
				// adapt NO_DATA_INTERVAL for cascaded client
				msgToForward.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, cascPublishService.getNoDataIntervalInSeconds());
				SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, msgToForward);
				// store cascadedSC mask in subscription
				String mask = msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
				cascClient.setSubscriptionMask(new SubscriptionMask(mask));
				CascClientSubscribeCallback cascCallback = new CascClientSubscribeCallback(callback, cascClient);
				// TODO JOT/JAN what OTI to continue??
				subscribeCall.invoke(cascCallback, oti);
				return;
			}
			// TODO JOT/JAN what OTI to continue??
			// cascaded client already subscribed
			callback.setCascClient(cascClient);
			this.subscribeWithActiveCascadedClient(cascClient, msgToForward, callback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	private void subscribeWithActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISCMPMessageCallback callback, int oti) throws Exception {
		// set cascaded subscriptonId and cascadedMask
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		String clientMaskString = msgToForward.getHeader(SCMPHeaderAttributeKey.MASK);
		SubscriptionMask cascClientMask = cascClient.getSubscriptionMask();
		String cascadedMask = cascClientMask.evalNewMask(clientMaskString);
		// TODO JOT/JAN calculate new mask and put into cascadedMask
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, msgToForward);
		subscribeCall.invoke(callback, oti);
	}

	public void clientUnsubscribe(CascadedClient cascClient, SCMPMessage msgToForward, ClnUnsubscribeCommandCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// thread got permit to continue
			if (cascClient.getClientSubscriptionIds().size() <= 1) {
				// only this client subscription left cascaded client can unsubscribe himself
				SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(this.requester, msgToForward);
				// set cascaded client subscriptonId
				msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
				// TODO JOT/JAN what OTI to continue??
				try {
					unsubscribeCall.invoke(callback, oti);
				} finally {
					// destroy cascaded client in any case
					cascClient.destroy();
				}
				return;
			}
			// more than one client subscription left - unsubscribe only client, change subscription for cascaded client
			ClnUnsubscribeCascSubscribedCallback cascCallback = new ClnUnsubscribeCascSubscribedCallback(callback.getRequest(),
					callback);
			cascCallback.setCascClient(cascClient);
			// TODO JOT/JAN what OTI to continue??
			this.unsubscribeWithActiveCascadedClient(cascClient, msgToForward, cascCallback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	private void unsubscribeWithActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISCMPMessageCallback callback, int oti) throws Exception {
		// change mask of cascaded client in callback or keep it in case of an error
		// remove client subscription id in any case from cascaded client list
		cascClient.removeClientSubscriptionId(msgToForward.getSessionId());
		// set cascaded subscriptonId
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		// TODO JOT/JAN calculate new mask and put into cascadedMask
		String cascadedMask = cascClient.evalSubscriptionMaskFromClientSubscriptions();
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
		SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(this.requester, msgToForward);
		unsubscribeCall.invoke(callback, oti);
	}

	public void cascadedSCUnsubscribe(CascadedClient cascClient, SCMPMessage msgToForward, CascSCUnsubscribeCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			if (cascClient.getClientSubscriptionIds().size() <= 1) {
				// only this client subscription left cascaded client can unsubscribe himself
				SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(this.requester, msgToForward);
				// set cascaded client subscriptonId
				msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
				// TODO JOT/JAN what OTI to continue??
				try {
					unsubscribeCall.invoke(callback, oti);
				} finally {
					// destroy cascaded client in any case
					cascClient.destroy();
				}
				return;
			}
			// TODO JOT/JAN what OTI to continue??
			// more than one client subscription left - unsubscribe only cascaded SC, change subscription for cascaded client
			callback.setCascClient(cascClient);
			this.unsubscribeWithActiveCascadedClient(cascClient, msgToForward, callback, oti);
		} catch (Exception e) {
			callback.receive(e);
		} finally {
			// destroy cascaded client in any case, permission get released inside method
			cascClient.destroy();
		}
	}

	public void changeSubscription(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = new SCMPClnChangeSubscriptionCall(this.requester, msgToForward);

		try {
			changeSubscriptionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// changeSubscription failed
			callback.receive(e);
		}
	}

	public void receivePublication(String serviceName, String subscriptionId, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester, serviceName,
				subscriptionId);
		// TODO JOT/JAN what msg number for cascaded client RCP its only between SC's
		receivePublicationCall.getRequest().setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, "400");
		try {
			receivePublicationCall.invoke(callback, timeoutMillis);
		} catch (Exception e) {
			// receive publication failed
			callback.receive(e);
			return;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		// TODO JOT
		// delete subscription on casc, change subscription on cascadeSC
	}

	/** {@inheritDoc} */
	@Override
	public void addSession(AbstractSession subscription) {
		this.subscriptions.add(subscription);
	}

	/** {@inheritDoc} */
	@Override
	public void removeSession(AbstractSession subscription) {
		if (this.subscriptions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.subscriptions.remove(subscription);
	}

	public void removeSession(String subscriptionId) {
		Subscription subscription = CascadedSC.subscriptionRegistry.getSubscription(subscriptionId);
		this.removeSession(subscription);
	}

	/** {@inheritDoc} */
	@Override
	public List<AbstractSession> getSessions() {
		return this.subscriptions;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasFreeSession() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions() {
		return Integer.MAX_VALUE;
	}
}