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

import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.casc.CascClientAlreadySubscribedCallback;
import org.serviceconnector.casc.CascClientSubscribeCallback;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.cmd.casc.ClnSubscribeCommandCascCallback;
import org.serviceconnector.cmd.sc.ClnSubscribeCommandCallback;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.SubscriptionMask;

public class CascadedSC extends Server implements IStatefulServer {

	/** The subscriptions, list of subscriptions allocated on cascaded SC. */
	private List<AbstractSession> subscriptions;

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

	public void subscribe(CascadedClient cascClient, SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		Semaphore cascClientSemaphore = cascClient.getCascClientSemaphore();
		int oti = (int) (this.operationTimeoutMultiplier * timeoutMillis);
		boolean permit;
		try {
			permit = cascClientSemaphore.tryAcquire(oti, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			// thread interrupted during acquire a permit on semaphore
			callback.receive(ex);
			return;
		}

		if (permit == false) {
			// thread didn't get a permit in time
			callback.receive(new IdleTimeoutException("oti expired. operation - could not be completed."));
			return;
		}
		// thread got permit to continue
		if (cascClient.isSubscribed() == false) {
			// cascaded client not subscribed - subscribe
			SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, msgToForward);
			try {
				((ClnSubscribeCommandCallback) callback).setServer(this);
				CascClientSubscribeCallback cascCallback = new CascClientSubscribeCallback(callback, cascClient,
						cascClientSemaphore);
				// TODO JOT/JAN what OTI to continue??
				subscribeCall.invoke(cascCallback, oti);
				return;
			} catch (Exception e) {
				// subscribe failed - release permit
				cascClientSemaphore.release();
				callback.receive(e);
				return;
			}
		}
		// cascaded client already subscribed - special subscribe
		// TODO JOT/JAN what OTI to continue??
		this.cascSubscribe(cascClient, msgToForward, callback, oti);
	}

	private void cascSubscribe(CascadedClient cascClient, SCMPMessage msgToForward, ISCMPMessageCallback callback, int oti) {
		Semaphore cascClientSemaphore = cascClient.getCascClientSemaphore();
		// set cascaded subscriptonId and cascadedMask
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID, cascClient.getSubscriptionId());
		msgToForward.setHeader(SCMPHeaderAttributeKey.CASCADED_MASK, "cascadedMask");
		// TODO calculate new mask and put into cascadedMask
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, msgToForward);
		try {
			if (callback instanceof ClnSubscribeCommandCallback) {
				callback = new CascClientAlreadySubscribedCallback(callback, new SubscriptionMask(""));
				((CascClientAlreadySubscribedCallback) callback).setCascClient(cascClient);
			} else {
				((ClnSubscribeCommandCascCallback) callback).setCascClientSemaphore(cascClientSemaphore);
			}
			subscribeCall.invoke(callback, oti);
			return;
		} catch (Exception e) {
			// subscribe failed - release permit
			cascClientSemaphore.release();
			callback.receive(e);
			return;
		}
	}

	public void unsubscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(this.requester, msgToForward);

		try {
			unsubscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// unsubscribe failed
			callback.receive(e);
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

	/** {@inheritDoc} */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		// delete subscription, unsubscribe local and on cascadeSC, nothing more
	}

	/** {@inheritDoc} */
	@Override
	public void addSession(AbstractSession session) {
		this.subscriptions.add(session);
	}

	/** {@inheritDoc} */
	@Override
	public void removeSession(AbstractSession session) {
		if (this.subscriptions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.subscriptions.remove(session);
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
