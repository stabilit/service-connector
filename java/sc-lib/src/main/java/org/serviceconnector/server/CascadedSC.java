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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCscAbortSubscriptionCall;
import org.serviceconnector.call.SCMPCscChangeSubscriptionCall;
import org.serviceconnector.call.SCMPCscCreateSessionCall;
import org.serviceconnector.call.SCMPCscDeleteSessionCall;
import org.serviceconnector.call.SCMPCscExecuteCall;
import org.serviceconnector.call.SCMPCscSubscribeCall;
import org.serviceconnector.call.SCMPCscUnsubscribeCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.call.SCMPFileDownloadCall;
import org.serviceconnector.call.SCMPFileListCall;
import org.serviceconnector.call.SCMPFileUploadCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.cmd.casc.CommandCascCallback;
import org.serviceconnector.cmd.casc.CscAbortSubscriptionCallbackForCasc;
import org.serviceconnector.cmd.casc.CscChangeSubscriptionActiveCascClientCallback;
import org.serviceconnector.cmd.casc.CscSubscribeActiveCascClientCallback;
import org.serviceconnector.cmd.casc.CscSubscribeInactiveCascClientCallback;
import org.serviceconnector.cmd.casc.CscUnsubscribeCallbackActiveCascClient;
import org.serviceconnector.cmd.sc.CommandCallback;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.netty.NettyHttpRequest;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class CascadedSC.
 */
public class CascadedSC extends Server implements IStatefulServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CascadedSC.class);

	/** The subscriptions, list of subscriptions allocated on cascaded SC. */
	private List<AbstractSession> subscriptions;

	/**
	 * Instantiates a new cascaded sc.
	 * 
	 * @param remoteNodeConfiguration
	 *            the remote node configuration
	 * @param socketAddress
	 *            the socket address
	 */
	public CascadedSC(RemoteNodeConfiguration remoteNodeConfiguration, InetSocketAddress socketAddress) {
		super(remoteNodeConfiguration, socketAddress);
		this.serverKey = remoteNodeConfiguration.getName();
		this.subscriptions = Collections.synchronizedList(new ArrayList<AbstractSession>());
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPCscCreateSessionCall createSessionCall = new SCMPCscCreateSessionCall(this.requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	/**
	 * Delete session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void deleteSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPCscDeleteSessionCall deleteSessionCall = new SCMPCscDeleteSessionCall(this.requester, msgToForward);
		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	/**
	 * Execute.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void execute(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPCscExecuteCall executeCall = new SCMPCscExecuteCall(this.requester, msgToForward);
		try {
			executeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// send data failed
			callback.receive(e);
		}
	}

	/**
	 * Echo.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void echo(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPEchoCall echoCall = new SCMPEchoCall(this.requester, msgToForward);
		try {
			echoCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// echo failed
			callback.receive(e);
		}
	}

	/**
	 * Server download file.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void serverDownloadFile(SCMPMessage msgToForward, CommandCascCallback callback, int timeoutMillis) {
		SCMPFileDownloadCall fileDownloadCall = new SCMPFileDownloadCall(this.requester, msgToForward);

		try {
			fileDownloadCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// echo failed
			callback.receive(e);
		}
	}

	/**
	 * Server get file list.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void serverGetFileList(SCMPMessage msgToForward, CommandCascCallback callback, int timeoutMillis) {
		SCMPFileListCall fileListCall = new SCMPFileListCall(this.requester, msgToForward);

		try {
			fileListCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// echo failed
			callback.receive(e);
		}
	}

	/**
	 * Server upload file.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void serverUploadFile(SCMPMessage msgToForward, CommandCascCallback callback, int timeoutMillis) {
		SCMPFileUploadCall fileUploadCall = new SCMPFileUploadCall(this.requester, msgToForward);

		try {
			fileUploadCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// echo failed
			callback.receive(e);
		}
	}

	/**
	 * Try acquire permit on cascaded client semaphore. This method is used to get permit to continue. Only one permit is available
	 * per cascaded client. If no permit is available thread waits inside the semaphore. Operation breaks if OTI times out. After
	 * receiving a valid permit cascaded client will be checked to be active. Operation stops if client got destroyed in the
	 * meantime. Important: Pay attention an acquired permit must be released as fast as possible!!! Other threads are blocked.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param oti
	 *            the operation timeout
	 * @param callback
	 *            the callback
	 * @return true, if successful
	 */
	public boolean tryAcquirePermitOnCascClientSemaphore(CascadedClient cascClient, int oti, ISCMPMessageCallback callback) {
		boolean permit = false;
		Semaphore cascClientSemaphore = cascClient.getCascClientSemaphore();
		try {
			LOGGER.trace("acquire permit callback=" + callback.getClass());
			permit = cascClientSemaphore.tryAcquire(oti, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			// thread interrupted during acquire a permit on semaphore
			callback.receive(ex);
			LOGGER.warn("thread interrupted during acquire a permit on semaphore service=" + cascClient.getServiceName(), ex);
			return false;
		}
		if (permit == false) {
			// thread didn't get a permit in time
			callback.receive(new IdleTimeoutException("oti(ms)=" + oti + " expired. operation - could not be completed."));
			LOGGER.warn("thread didn't get a permit in time service=" + cascClient.getServiceName() + " time(ms)=" + oti);
			cascClient.incrementAndCheckSemaphorePermitDenialCounter();
			return false;
		}
		// reset the denial counter - we got a permit in time!
		cascClient.resetSemaphorePermitDenialCounter();
		if (cascClient.isDestroyed() == true) {
			// cascaded client got destroyed in the meantime, stop operation
			callback.receive(new InvalidActivityException("cascaded client got destroyed in the meantime, stop operation service="
					+ cascClient.getServiceName()));
			// release permit
			cascClientSemaphore.release();
			LOGGER.warn("cascaded client got destroyed in the meantime, stop operation service=" + cascClient.getServiceName());
			return false;
		}
		return permit;
	}

	/**
	 * Cascaded SC subscribe.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void cascadedSCSubscribe(CascadedClient cascClient, SCMPMessage msgToForward, ISubscriptionCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		long boforeAcquireTime = System.currentTimeMillis();
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// calculate new OTI, reduce original by wait time in acquire
		oti = oti - (int) (System.currentTimeMillis() - boforeAcquireTime);
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// thread got permit to continue
			if (cascClient.isSubscribed() == false) {
				// cascaded client is not subscribed yet
				this.subscribeCascadedSCWithInActiveCascadedClient(cascClient, msgToForward, callback, oti);
				return;
			}
			CscSubscribeActiveCascClientCallback cascCallback = new CscSubscribeActiveCascClientCallback(cascClient,
					callback.getRequest(), callback);
			this.subscribeCascadedSCWithActiveCascadedClient(cascClient, msgToForward, cascCallback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	/**
	 * Cascaded SC change subscription.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void cascadedSCChangeSubscription(CascadedClient cascClient, SCMPMessage msgToForward, ISubscriptionCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		long boforeAcquireTime = System.currentTimeMillis();
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// calculate new OTI, reduce original by wait time in acquire
		oti = oti - (int) (System.currentTimeMillis() - boforeAcquireTime);
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// thread got permit to continue
			CscChangeSubscriptionActiveCascClientCallback cascCallback = new CscChangeSubscriptionActiveCascClientCallback(
					cascClient, callback.getRequest(), callback);
			this.changeSubscriptionCascadedSCWithActiveCascadedClient(cascClient, msgToForward, cascCallback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	/**
	 * Cascaded SC unsubscribe.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void cascadedSCUnsubscribe(CascadedClient cascClient, SCMPMessage msgToForward, ISubscriptionCallback callback,
			int timeoutMillis) {
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		long boforeAcquireTime = System.currentTimeMillis();
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// calculate new OTI, reduce original by wait time in acquire
		oti = oti - (int) (System.currentTimeMillis() - boforeAcquireTime);
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// remove (cascaded SC / client) from cascaded client list
			if (msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK) == null) {
				// remove cascaded client, he unsubscribes himself
				cascClient.removeClientSubscriptionId(msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID));
			}
			cascClient.removeClientSubscriptionId(msgToForward.getSessionId());

			Map<String, SubscriptionMask> clnSubscriptions = cascClient.getClientSubscriptionIds();
			if (clnSubscriptions.size() == 0) {
				// cascaded client can unsubscribe himself
				SCMPCscUnsubscribeCall cscUnsubscribeCall = new SCMPCscUnsubscribeCall(this.requester, msgToForward);
				// set cascaded client subscriptonId
				msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
				try {
					cscUnsubscribeCall.invoke(callback, oti);
				} finally {
					cascClient.setSubscribed(false);
					cascClient.destroy();
				}
				return;
			}
			// more than one client subscription left
			CscUnsubscribeCallbackActiveCascClient cascCallback = new CscUnsubscribeCallbackActiveCascClient(cascClient, callback);
			this.unsubscribeCascadedSCWithActiveCascadedClient(cascClient, msgToForward, cascCallback, oti);
		} catch (Exception e) {
			// release permit in case of an error
			cascClient.getCascClientSemaphore().release();
			callback.receive(e);
		}
	}

	/**
	 * Cascaded SC abort subscription.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void cascadedSCAbortSubscription(CascadedClient cascClient, SCMPMessage msgToForward, ISubscriptionCallback callback,
			int timeoutMillis) {

		if (cascClient.getSubscriptionId() == null) {
			// it happens when cascaded client got destroyed in meantime, no need to continue XAB message
			return;
		}

		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		long boforeAcquireTime = System.currentTimeMillis();
		if (this.tryAcquirePermitOnCascClientSemaphore(cascClient, oti, callback) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// calculate new OTI, reduce original by wait time in acquire
		oti = oti - (int) (System.currentTimeMillis() - boforeAcquireTime);
		// try catch block to assure releasing permit in case of any error - very important!
		try {
			// remove (cascaded SC / client) from cascaded client list
			if (msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK) == null) {
				// remove cascaded client, he unsubscribes himself
				cascClient.removeClientSubscriptionId(msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID));
			}
			cascClient.removeClientSubscriptionId(msgToForward.getSessionId());

			msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());

			Map<String, SubscriptionMask> clnSubscriptions = cascClient.getClientSubscriptionIds();
			if (clnSubscriptions.size() != 0) {
				// cascaded client still has client subscriptions set cascaded mask
				String cascadedMask = cascClient.evalSubscriptionMaskFromClientSubscriptions();
				msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
				cascClient.setSubscriptionMask(new SubscriptionMask(cascadedMask));
				SCMPCscAbortSubscriptionCall cscAbortCall = new SCMPCscAbortSubscriptionCall(this.requester, msgToForward);
				cscAbortCall.invoke(callback, oti);
				return;
			}
			// no client subscription left - destroy client after XAB
			SCMPCscAbortSubscriptionCall cscAbortCall = new SCMPCscAbortSubscriptionCall(this.requester, msgToForward);
			cscAbortCall.invoke(callback, oti);
		} catch (Exception e) {
			callback.receive(e);
		} finally {
			// release permit in case any case
			cascClient.getCascClientSemaphore().release();
		}
	}

	/**
	 * Unsubscribe cascaded client in error cases.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 */
	public void unsubscribeCascadedClientInErrorCases(CascadedClient cascClient) {
		if (cascClient.isSubscribed() == false) {
			return;
		}
		try {
			SCMPMessage message = new SCMPMessage();
			message.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
			message.setServiceName(cascClient.getServiceName());
			long msgSeqNr = cascClient.getMsgSequenceNr().incrementAndGetMsgSequenceNr();
			message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSeqNr);
			SCMPCscUnsubscribeCall unsubscribeCall = new SCMPCscUnsubscribeCall(this.requester, message);
			unsubscribeCall.invoke(new CommandCallback(false), AppContext.getBasicConfiguration().getSrvAbortOTIMillis());
		} catch (Exception e) {
			LOGGER.warn("unsubscribing cascaded client failed service=" + cascClient.getServiceName(), e);
		}
	}

	/**
	 * Receive publication.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 */
	public void receivePublication(CascadedClient cascClient, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester,
				cascClient.getServiceName(), cascClient.getSubscriptionId());
		long msgSeqNr = cascClient.getMsgSequenceNr().incrementAndGetMsgSequenceNr();
		receivePublicationCall.getRequest().setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSeqNr);
		try {
			receivePublicationCall.invoke(callback, timeoutMillis);
		} catch (Exception e) {
			// receive publication failed
			callback.receive(e);
			return;
		}
	}

	/**
	 * Subscribe cascaded SC with in active cascaded client.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param oti
	 *            the oti
	 * @throws Exception
	 *             the exception
	 */
	private void subscribeCascadedSCWithInActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISubscriptionCallback callback, int oti) throws Exception {
		// cascaded client not subscribed - subscribe
		CascadedPublishService cascPublishService = cascClient.getPublishService();
		// adapt NO_DATA_INTERVAL for cascaded client
		msgToForward.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, cascPublishService.getNoDataIntervalSeconds());
		SCMPCscSubscribeCall cscSubscribeCall = new SCMPCscSubscribeCall(this.requester, msgToForward);

		// set cascaded mask in message
		String tmpCscMask = msgToForward.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		if (tmpCscMask == null) {
			// its the case if a client is subscribing directly
			tmpCscMask = msgToForward.getHeader(SCMPHeaderAttributeKey.MASK);
		}
		SubscriptionMask currentSubscriptionMask = cascClient.getSubscriptionMask();
		if (currentSubscriptionMask != null) {
			// cascaded client already has subscribed clients, figure out combined mask
			tmpCscMask = SubscriptionMask.masking(currentSubscriptionMask, tmpCscMask);
		}
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, tmpCscMask);
		CscSubscribeInactiveCascClientCallback cscCallback = new CscSubscribeInactiveCascClientCallback(callback, cascClient,
				tmpCscMask);
		cscSubscribeCall.invoke(cscCallback, oti);
	}

	/**
	 * Subscribe cascaded SC with active cascaded client.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param oti
	 *            the oti
	 * @throws Exception
	 *             the exception
	 */
	private void subscribeCascadedSCWithActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISCMPMessageCallback callback, int oti) throws Exception {
		// set cascaded subscriptonId and cascadedMask
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		String clientMaskString = msgToForward.getHeader(SCMPHeaderAttributeKey.MASK);
		SubscriptionMask cascClientMask = cascClient.getSubscriptionMask();
		String cascadedMask = SubscriptionMask.masking(cascClientMask, clientMaskString);
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
		SCMPCscSubscribeCall subscribeCall = new SCMPCscSubscribeCall(this.requester, msgToForward);
		subscribeCall.invoke(callback, oti);
	}

	/**
	 * Change subscription cascaded SC with active cascaded client.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param oti
	 *            the oti
	 * @throws Exception
	 *             the exception
	 */
	private void changeSubscriptionCascadedSCWithActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISCMPMessageCallback callback, int oti) throws Exception {
		// set cascaded subscriptonId and cascadedMask
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		String clientMaskString = msgToForward.getHeader(SCMPHeaderAttributeKey.MASK);
		SubscriptionMask cascClientMask = cascClient.getSubscriptionMask();
		String cascadedMask = SubscriptionMask.masking(cascClientMask, clientMaskString);
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
		SCMPCscChangeSubscriptionCall cscChangeSubscriptionCall = new SCMPCscChangeSubscriptionCall(this.requester, msgToForward);
		cscChangeSubscriptionCall.invoke(callback, oti);
	}

	/**
	 * Unsubscribe cascaded SC with active cascaded client.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param oti
	 *            the oti
	 * @throws Exception
	 *             the exception
	 */
	private void unsubscribeCascadedSCWithActiveCascadedClient(CascadedClient cascClient, SCMPMessage msgToForward,
			ISCMPMessageCallback callback, int oti) throws Exception {
		// change mask of cascaded client in callback or keep it in case of an error
		// set cascaded subscriptonId
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		String cascadedMask = cascClient.evalSubscriptionMaskFromClientSubscriptions();
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, cascadedMask);
		SCMPCscUnsubscribeCall cscUnsubscribeCall = new SCMPCscUnsubscribeCall(this.requester, msgToForward);
		cscUnsubscribeCall.invoke(callback, oti);
	}

	/**
	 * Abort session.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason {@inheritDoc}
	 */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		if (session instanceof Subscription) {
			Subscription subscription = (Subscription) session;
			CascadedPublishService casService = (CascadedPublishService) subscription.getService();

			PublishMessageQueue<SCMPMessage> publishMessageQueue = casService.getMessageQueue();
			// unsubscribe subscription
			publishMessageQueue.unsubscribe(subscription.getId());
			publishMessageQueue.removeNonreferencedNodes();

			CascadedClient cascClient = casService.getCascClient();
			// remove timed out subscription id from client subscription list
			cascClient.removeClientSubscriptionId(subscription.getId());

			if (cascClient.isDestroyed() == true) {
				// cascaded client got destroyed - do not continue
				return;
			}

			long msgSeqNr = cascClient.getMsgSequenceNr().incrementAndGetMsgSequenceNr();
			IRequest request = new NettyHttpRequest(null, null, null);
			SCMPMessage abortMessage = new SCMPMessage();
			abortMessage.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSeqNr);
			abortMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, casService.getName());
			// cascaded id will be set in following (cascadedSCAbortSubscription) method
			request.setMessage(abortMessage);

			if (subscription.isCascaded() == true) {
				// XAB procedure for casc subscriptions
				Set<String> subscriptionIds = subscription.getCscSubscriptionIds().keySet();

				for (String id : subscriptionIds) {
					abortMessage.setSessionId(id);
					this.cascadedSCAbortSubscription(cascClient, abortMessage, new CscAbortSubscriptionCallbackForCasc(request,
							subscription), AppContext.getBasicConfiguration().getSrvAbortOTIMillis());
				}
				subscription.getCscSubscriptionIds().clear();
			} else {
				// normal XAB procedure
				abortMessage.setSessionId(subscription.getId());
				SubscriptionLogger.logAbortSubscription((Subscription) session, reason);
				this.cascadedSCAbortSubscription(cascClient, abortMessage, new CscAbortSubscriptionCallbackForCasc(request,
						subscription), AppContext.getBasicConfiguration().getSrvAbortOTIMillis());
			}
		} else {
			LOGGER.error("session which is in relation with a cascadedSC timed out - should nerver occur");
		}
	}

	/**
	 * Adds the session.
	 * 
	 * @param subscription
	 *            the subscription {@inheritDoc}
	 */
	@Override
	public void addSession(AbstractSession subscription) {
		this.subscriptions.add(subscription);
	}

	/**
	 * Removes the session.
	 * 
	 * @param subscription
	 *            the subscription {@inheritDoc}
	 */
	@Override
	public void removeSession(AbstractSession subscription) {
		if (this.subscriptions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.subscriptions.remove(subscription);
	}

	/**
	 * Removes the session.
	 * 
	 * @param subscriptionId
	 *            the subscription id
	 */
	public void removeSession(String subscriptionId) {
		Subscription subscription = AppContext.getSubscriptionRegistry().getSubscription(subscriptionId);
		this.removeSession(subscription);
	}

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions {@inheritDoc}
	 */
	@Override
	public List<AbstractSession> getSessions() {
		return this.subscriptions;
	}

	/**
	 * Checks for free session.
	 * 
	 * @return true, if successful {@inheritDoc}
	 */
	@Override
	public boolean hasFreeSession() {
		return true;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions {@inheritDoc}
	 */
	@Override
	public int getMaxSessions() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Dump the cascaded SC into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("cascaded-sc");
		writer.writeAttribute("key", this.serverKey);
		writer.writeAttribute("socketAddress", this.socketAddress.getHostName() + Constants.SLASH + this.socketAddress.getPort());
		writer.writeAttribute("operationTimeoutMultiplier", this.operationTimeoutMultiplier);
		this.requester.dump(writer);
		writer.writeEndElement(); // end of cascaded-sc
	}
}