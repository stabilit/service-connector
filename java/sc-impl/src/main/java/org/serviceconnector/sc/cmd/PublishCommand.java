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
package org.serviceconnector.sc.cmd;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.ICommandValidator;
import org.serviceconnector.common.cmd.IPassThroughPartMsg;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.scmp.HasFaultResponseException;
import org.serviceconnector.common.scmp.IRequest;
import org.serviceconnector.common.scmp.IResponse;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.common.scmp.internal.SCMPPart;
import org.serviceconnector.sc.registry.SubscriptionQueue;
import org.serviceconnector.sc.service.PublishService;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class PublishCommand. Responsible for validation and execution of publish command. Allows publishing messages to
 * clients.
 * 
 * @author JTraber
 */
public class PublishCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishCommand.class);
	
	/**
	 * Instantiates a new PublishCommand.
	 */
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
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);

		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// lookup service and checks properness
		PublishService service = this.validatePublishService(serviceName);
		SubscriptionQueue<SCMPMessage> queue = service.getSubscriptionQueue();
		// throws an exception if failed
		queue.insert(message);

		// reply to server
		SCMPMessage reply = null;
		if (message.isPart()) {
			// incoming message is of type part - outgoing must be part too
			reply = new SCMPPart();
		} else {
			reply = new SCMPMessage();
		}
		reply.setMessageType(this.getKey());
		reply.setIsReply(true);
		reply.setServiceName(message.getServiceName());
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
		response.setSCMP(reply);
	}

	/**
	 * The Class PublishCommandValidator.
	 */
	private class PublishCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// message info
				String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
				if (messageInfo != null) {
					ValidatorUtility.validateStringLength(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
				}
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable ex) {
				logger.error("validate", ex);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}