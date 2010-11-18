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
package org.serviceconnector.api.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPPublishCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCPublishServer. A Server that publishes messages to an SC.
 */
public class SCPublishServer extends SCSessionServer {

	public SCPublishServer(SCServerContext scServerContext, String serviceName) {
		super(scServerContext, serviceName);
	}

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishServer.class);

	public void publish(SCPublishMessage publishMessage) throws Exception {
		this.publish(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, publishMessage);
	}

	public void publish(int operationTimeoutSeconds, SCPublishMessage publishMessage) throws Exception {
		if (publishMessage == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "subscibeMessage can not be null");
		}
		ValidatorUtility.validateStringLength(1, this.serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(this.serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		SrvService srvService = srvServiceRegistry.getSrvService(this.serviceName);
		if (srvService == null) {
			throw new SCServiceException("Service not found, service name: " + this.serviceName);
		}
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(srvService.getRequester(),
				serviceName);
		publishCall.setRequestBody(publishMessage.getData());
		publishCall.setMask(publishMessage.getMask());
		SCServerCallback callback = new SCServerCallback(true);
		publishCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage message = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (message.isFault()) {
			SCMPFault fault = (SCMPFault) message;
			throw new SCServiceException(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}
}
