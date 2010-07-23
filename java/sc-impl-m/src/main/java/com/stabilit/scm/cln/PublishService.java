/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnSubscribeCall;
import com.stabilit.scm.common.call.SCMPClnUnsubscribeCall;
import com.stabilit.scm.common.call.SCMPReceivePublicationCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.IPublishService;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCServiceException;

public class PublishService extends Service implements IPublishService {

	private String mask;

	public PublishService(String serviceName, ISCContext context) {
		super(serviceName, context);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool()));
		this.serviceContext = new ServiceContext(context, this);
		this.mask = null;
	}

	@Override
	public void changeSubscription(String mask) throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		this.mask = mask;
		// TODO
	}

	@Override
	public void subscribe(String mask, ISCMessageCallback callback) throws Exception {
		if (this.callback != null) {
			throw new SCServiceException("already subscribed");
		}
		this.mask = mask;
		this.callback = new PublishServiceCallback(callback);
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(
				this.requester, this.serviceName);
		subscribeCall.invoke(this.callback);
		SCMPMessage reply = this.callback.getMessageSync();
		this.sessionId = reply.getSessionId();
		this.receivePublication();
	}

	private void receivePublication() throws Exception {
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		receivePublicationCall.setMask(mask);
		receivePublicationCall.invoke(this.callback);
	}

	@Override
	public void unsubscribe() throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("unsubscrib not possible - not subscribed");
		}
		SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		unsubscribeCall.invoke(this.callback);
		this.callback.getMessageSync();
		this.callback = null;
		this.mask = null;
	}

	private class PublishServiceCallback extends ServiceCallback {

		public PublishServiceCallback(ISCMessageCallback messageCallback) {
			super(PublishService.this, messageCallback);
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// TODO if no data message -> receivePublication again
			super.callback(scmpReply);
			PublishService.this.receivePublication();
		}
	}
}
