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
package com.stabilit.scm.sc.cmd.impl;

import java.net.SocketAddress;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.service.PublishService;

public class PublishCommand extends CommandAdapter implements IPassThroughPartMsg {

	public PublishCommand() {
		this.commandValidator = new PublishCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.PUBLISH;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		System.out.println("PublishCommand.run()");
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);

		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// lookup service and checks properness
		PublishService service = this.validatePublishService(serviceName);
		ISubscriptionPlace<SCMPMessage> place = service.getSubscriptionPlace();
		if (place == null) {
			throw new SCServiceException("no subscriptionPlace for serviceName : " + serviceName);
		}
		place.add(message); // throws an exception if failed
		SCMPMessage replyMessage = new SCMPMessage();
		replyMessage.setMessageType(message.getMessageType());
		replyMessage.setServiceName(message.getServiceName());
		response.setSCMP(replyMessage);
	}

	private class PublishCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}