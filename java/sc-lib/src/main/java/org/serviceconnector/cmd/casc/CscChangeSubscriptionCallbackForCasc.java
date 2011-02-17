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
package org.serviceconnector.cmd.casc;

import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

public class CscChangeSubscriptionCallbackForCasc extends ClnCommandCascCallback implements ISubscriptionCallback {

	/** The subscription. */
	private Subscription cascSCSubscription;
	private String cascSCMaskString;

	public CscChangeSubscriptionCallbackForCasc(IRequest request, IResponse response, IResponderCallback callback,
			Subscription cascSCSubscription, String cascSCMaksString) {
		super(request, response, callback);
		this.cascSCSubscription = cascSCSubscription;
		this.cascSCMaskString = cascSCMaksString;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();

		if (reply.isFault() == false && reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION) == false) {
			// change subscription for cascaded SC
			SubscriptionQueue<SCMPMessage> queue = ((IPublishService) cascSCSubscription.getService()).getSubscriptionQueue();
			SubscriptionMask cascSCMask = new SubscriptionMask(cascSCMaskString);
			queue.changeSubscription(this.cascSCSubscription.getId(), cascSCMask);
			cascSCSubscription.setMask(cascSCMask);
			SubscriptionLogger.logChangeSubscribe(serviceName, this.cascSCSubscription.getId(), cascSCMaskString);
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CSC_CHANGE_SUBSCRIPTION);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.cascSCSubscription;
	}

	public IRequest getRequest() {
		return this.request;
	}
}
