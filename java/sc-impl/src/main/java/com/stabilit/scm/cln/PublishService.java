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
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnChangeSubscriptionCall;
import com.stabilit.scm.common.call.SCMPClnSubscribeCall;
import com.stabilit.scm.common.call.SCMPClnUnsubscribeCall;
import com.stabilit.scm.common.call.SCMPReceivePublicationCall;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.IPublishService;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCServiceException;

/**
 * The Class PublishService. PublishService is a remote interface to a publish service and provides communication
 * functions.
 */
public class PublishService extends Service implements IPublishService {

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param context
	 *            the context
	 */
	public PublishService(String serviceName, ISCContext context) {
		super(serviceName, context);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));
		this.serviceContext = new ServiceContext(context, this);
	}

	/** {@inheritDoc} */
	@Override
	public void changeSubscription(String mask) throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		this.msgId.incrementMsgSequenceNr();
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		changeSubscriptionCall.setMask(mask);
		changeSubscriptionCall.invoke(this.callback);
		this.callback.getMessageSync();
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(String mask, String sessionInfo, int noDataInterval, ISCMessageCallback callback)
			throws Exception {
		this.subscribe(mask, sessionInfo, noDataInterval, null, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(String mask, String sessionInfo, int noDataInterval, String authenticationId,
			ISCMessageCallback callback) throws Exception {
		if (this.callback != null) {
			throw new SCServiceException("already subscribed");
		}
		this.msgId.reset();
		this.callback = new PublishServiceCallback(callback);
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(
				this.requester, this.serviceName);
		subscribeCall.setMask(mask);
		subscribeCall.setSessionInfo(sessionInfo);
		subscribeCall.setNoDataIntervalSeconds(noDataInterval);
		if (authenticationId != null) {
			subscribeCall.setAuthenticationId(authenticationId);
		}
		subscribeCall.invoke(this.callback);
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("subscribe failed", fault.getCause());
		}
		this.sessionId = reply.getSessionId();
		this.receivePublication();
	}

	/**
	 * Sends a receive publication to the SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void receivePublication() throws Exception {
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		this.msgId.incrementMsgSequenceNr();
		receivePublicationCall.invoke(this.callback);
	}

	/** {@inheritDoc} */
	@Override
	public void unsubscribe() throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("unsubscrib not possible - not subscribed");
		}
		this.msgId.incrementMsgSequenceNr();
		SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		unsubscribeCall.invoke(this.callback);
		this.callback.getMessageSync();
		this.msgId = null;
		this.callback = null;
	}

	/**
	 * The Class PublishServiceCallback. Responsible for handling the right communication sequence for publish subscribe
	 * protocol.
	 */
	private class PublishServiceCallback extends ServiceCallback {

		/**
		 * Instantiates a new publish service callback.
		 * 
		 * @param messageCallback
		 *            the message callback
		 */
		public PublishServiceCallback(ISCMessageCallback messageCallback) {
			super(PublishService.this, messageCallback);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// check if reply is real answer
			boolean noData = scmpReply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
			if (noData) {
				// no data reply received - send CRP again
				PublishService.this.receivePublication();
				return;
			}
			super.callback(scmpReply);
		}
	}
}
