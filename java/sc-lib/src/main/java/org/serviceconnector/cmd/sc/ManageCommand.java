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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ManageCommand. Responsible for validation and execution of manage command. Manage command is used to enable/disable
 * services.
 * 
 * @author JTraber
 */
public class ManageCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ManageCommand.class);

	/** The Constant MANAGE_REGEX_STRING. */
	private static final String MANAGE_REGEX_STRING = "(" + Constants.CC_CMD_KILL + "|" + Constants.CC_CMD_DUMP + "|"
			+ Constants.CC_CMD_CLEAR_CACHE + "|(" + Constants.CC_CMD_ENABLE + "|" + Constants.CC_CMD_DISABLE + ")"
			+ Constants.EQUAL_SIGN + "(.*))";

	/** The Constant MANAGE_PATTERN. */
	private static final Pattern MANAGE_PATTERN = Pattern.compile(MANAGE_REGEX_STRING, Pattern.CASE_INSENSITIVE);

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

		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());

		Matcher m = MANAGE_PATTERN.matcher(bodyString);
		if (!m.matches()) {
			logger.error("wrong manage command body=" + bodyString); // body has bad syntax
			scmpReply = new SCMPMessageFault(SCMPError.V_WRONG_MANAGE_COMMAND, bodyString);
			response.setSCMP(scmpReply);
			// initiate responder to send reply
			responderCallback.responseCallback(request, response);
			return;
		}
		String command = m.group(1);
		String function = m.group(2);
		String serviceName = m.group(3);

		// kill command
		if ((ipAddress.equals(localHost.getHostAddress())) && (command.equalsIgnoreCase(Constants.CC_CMD_KILL))) {
			// kill request is allowed from localhost only!
			logger.info("SC stopped by kill console command");
			response.setSCMP(scmpReply);
			responderCallback.responseCallback(request, response);
			// wait a second until the response has been sent
			Thread.sleep(1000);
			System.exit(0);
		}

		// other commands
		if (command.equals(Constants.CC_CMD_DUMP)) {
			AppContext.dump();
		} else if (command.equals(Constants.CC_CMD_CLEAR_CACHE)) {
			CacheManager cacheManager = AppContext.getCacheManager();
			cacheManager.clearAll();
		} else if (this.serviceRegistry.containsKey(serviceName)) {
			// service exists
			if (function.equalsIgnoreCase(Constants.CC_CMD_ENABLE)) {
				// enable service
				logger.info("enable service=" + serviceName);
				this.serviceRegistry.getService(serviceName).setEnabled(true);
			} else {
				// disable service
				logger.info("disable service=" + serviceName);
				this.serviceRegistry.getService(serviceName).setEnabled(false);
			}
		} else {
			logger.debug("service=" + serviceName + " not found");
			scmpReply = new SCMPMessageFault(SCMPError.SERVICE_NOT_FOUND, serviceName);
		}
		response.setSCMP(scmpReply);
		// initiate responder to send reply
		responderCallback.responseCallback(request, response);
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
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
