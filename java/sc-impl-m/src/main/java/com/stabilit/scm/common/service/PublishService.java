/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnSubscribeCall;
import com.stabilit.scm.cln.call.SCMPClnUnsubscribeCall;
import com.stabilit.scm.cln.call.SCMPReceivePublicationCall;
import com.stabilit.scm.cln.service.ISCMessageCallback;
import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class PublishService implements IPublishService {

	private String serviceName;
	private String sessionId;
	private IServiceContext publishContext;
	private IRequester requester;
	private ISCMPCallback scmpCallback;
	private String mask;

	public PublishService(String serviceName, ISCContext context) {
		this.serviceName = serviceName;
		this.sessionId = null;
		this.requester = new Requester(context);
		this.publishContext = new ServiceContext(context, this);
		this.scmpCallback = null;
		this.mask = null;
	}

	@Override
	public IServiceContext getContext() {
		return this.publishContext;
	}

	@Override
	public void changeSubscription(String mask) {
	}

	@Override
	public void subscribe(String mask, ISCMessageCallback callback) throws Exception {
		this.mask = mask;
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(
				this.requester, this.serviceName);
		SCMPMessage reply = subscribeCall.invoke();
		this.sessionId = reply.getSessionId();
		if (this.scmpCallback != null) {
			throw new SCServiceException("already subscribed");
		}
		this.scmpCallback = new PublishServiceCallback(callback);
		this.receivePublication();
	}

	private void receivePublication() throws Exception {
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		receivePublicationCall.setMask(mask);
		receivePublicationCall.invoke(this.scmpCallback);
	}

	@Override
	public void unsubscribe() throws Exception {
		this.scmpCallback = null;
		SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		unsubscribeCall.invoke();
	}

	private class PublishServiceCallback extends ServiceCallback {

		public PublishServiceCallback(ISCMessageCallback messageCallback) {
			super(messageCallback);
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// TODO if no data message -> receivePublication again
			super.callback(scmpReply);
			PublishService.this.receivePublication();
		}
	}
}
