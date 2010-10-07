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
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPPublishCall;
import org.serviceconnector.ctx.SCServerContext;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCPublishServer. A Server that publishes messages to an SC.
 */
public class SCPublishServer extends SCSessionServer implements ISCPublishServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishServer.class);

	/** {@inheritDoc} */
	@Override
	public void publish(String serviceName, String mask, Object data) throws Exception {
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		SrvServiceRegistry srvServiceRegistry = SCServerContext.getCurrentContext().getSrvServiceRegistry();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName);
		if (srvService == null) {
			throw new SCServiceException("Service not found, service name: " + serviceName);
		}
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(srvService
				.getRequester(), serviceName);
		publishCall.setRequestBody(data);
		publishCall.setMask(mask);
		publishCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
				* Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage message = this.callback.getMessageSync();
		if(message.isFault()) {
			SCMPFault fault = (SCMPFault) message;
			throw new SCServiceException(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void registerServer(String scHost, int scPort, String serviceName, int maxSessions, int maxConnections,
			ISCPublishServerCallback scCallback) throws Exception {
		super.registerServer(scHost, scPort, serviceName, maxSessions, maxConnections, scCallback);
	}
}
