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
package org.serviceconnector.cmd.sc;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.URLString;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ManageCommand. Responsible for validation and execution of manage command. Manage command is used to enable/disable
 * services.
 * 
 * @author JTraber
 */
public class ManageCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(ManageCommand.class);

	/**
	 * Instantiates a new manage command.
	 */
	public ManageCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.MANAGE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();
		String ipAddress = reqMsg.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);

		URLString urlRequestString = new URLString();
		urlRequestString.parseRequestURLString(bodyString);
		String callKey = urlRequestString.getCallKey();

		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());

		String serviceName = urlRequestString.getParamValue(Constants.SERVICE_NAME);

		// kill command
		if ((ipAddress.equals(localHost.getHostAddress())) && Constants.CC_CMD_KILL.equalsIgnoreCase(callKey)) {
			// kill request is allowed from localhost only!
			LOGGER.info("SC stopped by kill console command");
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			// wait 300 milliseconds until the response has been sent
			Thread.sleep(300);
			System.exit(0);
		}

		// other commands
		if (Constants.CC_CMD_DUMP.equalsIgnoreCase(callKey)) {
			AppContext.dump();
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			return;
		}

		if (Constants.CC_CMD_CLEAR_CACHE.equalsIgnoreCase(callKey)) {
			CacheManager cacheManager = AppContext.getCacheManager();
			cacheManager.clearAll();
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			return;
		}

		if (Constants.CC_CMD_ENABLE.equalsIgnoreCase(callKey)) {
			if (serviceName.equalsIgnoreCase(Constants.WILD_CARD_SIGN)) {
				// enable all services
				this.modifyStateOfAllServices(true);
			} else if (this.serviceRegistry.containsKey(serviceName) == false) {
				LOGGER.debug("service=" + serviceName + " not found");
				scmpReply = new SCMPMessageFault(SCMPError.SERVICE_NOT_FOUND, serviceName);
			} else {
				// enable service
				LOGGER.info("enable service=" + serviceName);
				this.serviceRegistry.getService(serviceName).setEnabled(true);
			}
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			return;
		}

		if (Constants.CC_CMD_DISABLE.equalsIgnoreCase(callKey)) {
			if (serviceName.equalsIgnoreCase(Constants.WILD_CARD_SIGN)) {
				// disable all services
				this.modifyStateOfAllServices(false);
			} else if (this.serviceRegistry.containsKey(serviceName) == false) {
				LOGGER.debug("service=" + serviceName + " not found");
				scmpReply = new SCMPMessageFault(SCMPError.SERVICE_NOT_FOUND, serviceName);
			} else {
				// disable service
				LOGGER.info("disable service=" + serviceName);
				this.serviceRegistry.getService(serviceName).setEnabled(false);
			}
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			return;
		}
		LOGGER.error("wrong manage command body=" + bodyString); // body has bad syntax
		scmpReply = new SCMPMessageFault(SCMPError.V_WRONG_MANAGE_COMMAND, bodyString);
		response.setSCMP(scmpReply);
		// initiate responder to send reply
		responderCallback.responseCallback(request, response);
	}

	/**
	 * Modify state of all services.
	 * 
	 * @param enable
	 *            the enable
	 */
	private void modifyStateOfAllServices(boolean enable) {
		Service[] services = this.serviceRegistry.getServices();

		for (Service service : services) {
			LOGGER.info("set service=" + service.getName() + " state enable=" + enable);
			service.setEnabled(enable);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// ipAddressList mandatory
			String ipAddressList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
			ValidatorUtility.validateIpAddressList(ipAddressList);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			LOGGER.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
