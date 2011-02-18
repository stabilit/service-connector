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
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheComposite.CACHE_STATE;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.util.URLCallString;
import org.serviceconnector.util.URLParameterString;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class InspectCommand. Responsible for validation and execution of inspect command. Inspect command is used for
 * testing/maintaining reasons. Returns dumps of internal stuff to requester.
 * 
 * @author JTraber
 */
public class InspectCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(InspectCommand.class);

	/**
	 * Instantiates a new InspectCommand.
	 */
	public InspectCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.INSPECT;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();

		URLCallString urlCallString = new URLCallString();
		urlCallString.parseString(bodyString);
		String callKey = urlCallString.getCallKey();

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		scmpReply.setMessageType(getKey());

		if (bodyString == null) {
			String inspectString = "serviceRegistry&" + this.getRegistryInspectString(this.serviceRegistry);
			inspectString += "sessionRegistry&" + this.getRegistryInspectString(this.sessionRegistry);
			inspectString += "serverRegistry&" + this.getRegistryInspectString(this.serverRegistry);

			// dump internal registries
			scmpReply.setBody(inspectString);
			response.setSCMP(scmpReply);
			// initiate responder to send reply
			responderCallback.responseCallback(request, response);
			return;
		}
		if (bodyString.startsWith(Constants.CC_CMD_STATE)) {
			// state for service requested
			String serviceName = bodyString.substring(6);
			logger.debug("state request for service:" + serviceName);

			if (this.serviceRegistry.containsKey(serviceName)) {
				if (this.serviceRegistry.getService(serviceName).isEnabled() == true) {
					scmpReply.setBody(Constants.CC_CMD_ENABLE);
					logger.debug("service:" + serviceName + "is enabled");
				} else if (this.serviceRegistry.getService(serviceName).isEnabled() == false) {
					scmpReply.setBody(Constants.CC_CMD_DISABLE);
					logger.debug("service:" + serviceName + "is disabled");
				} else {
					scmpReply.setBody("?");
					logger.debug("service:" + serviceName + "is state unknown");
				}
			} else {
				logger.debug("service=" + serviceName + " not found");
				scmpReply = new SCMPMessageFault(SCMPError.SERVICE_NOT_FOUND, serviceName);
			}
			response.setSCMP(scmpReply);
			// initiate responder to send reply
			responderCallback.responseCallback(request, response);
			return;
		}
		if (bodyString.startsWith(Constants.CC_CMD_SESSIONS)) {
			// state for service requested
			String serviceName = bodyString.substring(9);
			logger.debug("sessions request for service:" + serviceName);
			Service service = this.getService(serviceName);
			if (service.getType() != ServiceType.PUBLISH_SERVICE && service.getType() != ServiceType.SESSION_SERVICE) {
				// wrong service type
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVICE_TYPE, serviceName);
				scmpCommandException.setMessageType(getKey());
				throw scmpCommandException;
			}
			StatefulService statefulService = (StatefulService) service;
			scmpReply.setBody(statefulService.getCountAvailableSessions() + "/" + statefulService.getCountAllocatedSessions());
			response.setSCMP(scmpReply);
			// initiate responder to send reply
			responderCallback.responseCallback(request, response);
			return;
		}
		if (Constants.CC_CMD_INSPECT_CACHE.equalsIgnoreCase(callKey)) {
			String serviceName = urlCallString.getParameter(0);
			String cacheId = urlCallString.getParameter(1);
			logger.debug("cache inspect for serviceName: " + serviceName + ", cacheId:" + cacheId);
			String cacheInspectString = getCacheInspectString(serviceName, cacheId);
			scmpReply.setBody(cacheInspectString);
			response.setSCMP(scmpReply);
			// initiate responder to send reply
			responderCallback.responseCallback(request, response);
			return;
		}

		logger.error("wrong inspect command body=" + bodyString); // body has bad syntax
		scmpReply = new SCMPMessageFault(SCMPError.V_WRONG_INSPECT_COMMAND, bodyString);
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

	/**
	 * Gets the cache inspect string for given serviceName and cacheId.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param cacheId
	 *            the cache id
	 * @return the cache inspect string
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	private String getCacheInspectString(String serviceName, String cacheId) throws SCMPCommandException {
		CacheManager cacheManager = AppContext.getCacheManager();
		if (cacheManager == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_MANAGER_ERROR,
					"no cache manager (null)");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		Cache cache = cacheManager.getCache(serviceName);
		if (cache == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR, serviceName);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		try {
			CacheComposite cacheComposite = cache.getComposite(cacheId);
			if (cacheComposite == null) {
				URLParameterString parameterString = new URLParameterString();
				parameterString.put("cacheId", cacheId);
				parameterString.put("return", "notfound");
				return parameterString.toString();
			}
			CACHE_STATE cacheState = cacheComposite.getCacheState();
			// Date creationTime = cacheComposite.getCreationTime();
			// Date lastModifiedTime = cacheComposite.getLastModifiedTime();
			String expirationDateTime = cacheComposite.getExpiration();
			int size = cacheComposite.getSize();
			URLParameterString parameterString = new URLParameterString();
			parameterString.put("return", "success");
			parameterString.put("cacheId", cacheId);
			parameterString.put("cacheState", cacheState.toString());
			parameterString.put("cacheSize", String.valueOf(size));
			parameterString.put("cacheExpiration", expirationDateTime);
			return parameterString.toString();
		} catch (CacheException e) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR, e.toString());
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
	}

	/**
	 * Gets the registry inspect string.
	 * 
	 * @param registry
	 *            the registry
	 * @return the registry inspect string
	 */
	private String getRegistryInspectString(Registry<?, ?> registry) {
		String string = registry.toString();
		if (registry.getSize() == 0) {
			string += "@";
		}
		return string;
	}
}