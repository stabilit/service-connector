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

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPPublishCall;
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

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishServer.class);

	/**
	 * Publish data.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param mask
	 *            the mask
	 * @param data
	 *            the data
	 * @throws Exception
	 *             the exception
	 */
	public void publish(String serviceName, String mask, Object data) throws Exception {
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		SrvServiceRegistry srvServiceRegistry = AppContext.getCurrentContext().getSrvServiceRegistry();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName);
		if (srvService == null) {
			throw new SCServiceException("Service not found, service name: " + serviceName);
		}
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(srvService
				.getRequester(), serviceName);
		publishCall.setRequestBody(data);
		publishCall.setMask(mask);
		SCServerCallback callback = new SCServerCallback(true);
		publishCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage message = callback.getMessageSync();
		if (message.isFault()) {
			SCMPFault fault = (SCMPFault) message;
			throw new SCServiceException(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	/**
	 * Register server for a service.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param serviceName
	 *            the service name
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public void registerServer(String scHost, int scPort, String serviceName, int maxSessions, int maxConnections,
			ISCPublishServerCallback scCallback) throws Exception {
		super.registerServer(scHost, scPort, serviceName, maxSessions, maxConnections, scCallback);
	}
}
