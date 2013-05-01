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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.SCCache;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.casc.ClnExecuteCommandCascCallback;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.CascadedSessionService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnExecuteCommand. Responsible for validation and execution of execute command. Execute command sends any data to the
 * server. Execute command runs
 * asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ClnExecuteCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ClnExecuteCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		// check service is present
		Service abstractService = this.getService(serviceName);

		SCCache cache = AppContext.getSCCache();

		switch (abstractService.getType()) {
		case CASCADED_SESSION_SERVICE:
			if (cache.isCacheEnabled()) {
				// try to load response from cache
				SCMPMessage message = cache.tryGetMessageFromCacheOrLoad(reqMessage);
				if (message != null) {
					// message found in cache - hand it to the client
					response.setSCMP(message);
					responderCallback.responseCallback(request, response);
					return;
				}
			}
			this.executeCascadedService(request, response, responderCallback);
			return;
		default:
			// code for other types of services is below
			break;
		}

		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		String sessionId = reqMessage.getSessionId();
		Session session = this.getSessionById(sessionId);
		if (session.hasPendingRequest() == true) {
			LOGGER.warn("session " + sessionId + " has pending request");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.PARALLEL_REQUEST, "service="
					+ reqMessage.getServiceName() + " sid=" + sessionId);
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		// sets the time of last execution
		session.resetExecuteTime();
		synchronized (session) {
			session.setPendingRequest(true); // IMPORTANT - set true before reset timeout - because of parallel echo call
			// reset session timeout to OTI+ECI - during wait for server reply
			this.sessionRegistry.resetSessionTimeout(session, (otiOnSCMillis + session.getSessionTimeoutMillis()));
		}

		if (cache.isCacheEnabled()) {
			try {
				// try to load response from cache
				SCMPMessage message = cache.tryGetMessageFromCacheOrLoad(reqMessage);
				if (message != null) {
					synchronized (session) {
						// reset session timeout to ECI
						this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
						session.setPendingRequest(false); // IMPORTANT - set false after reset timeout - parallel echo call
					}
					// message found in cache - hand it to the client
					response.setSCMP(message);
					responderCallback.responseCallback(request, response);
					return;
				}
			} catch (Exception e) {
				synchronized (session) {
					// reset session timeout to ECI
					this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
					session.setPendingRequest(false); // IMPORTANT - set false after reset timeout - because of parallel echo call
				}
				throw e;
			}
		}
		ExecuteCommandCallback callback = null;
		StatefulServer server = session.getStatefulServer();
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			// reset msgType, might have been modified in below execute try
			reqMessage.setMessageType(this.getKey());
			callback = new ExecuteCommandCallback(request, response, responderCallback, sessionId);
			try {
				server.execute(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				LOGGER.debug("ConnectionPoolBusyException caught in wait mec of execute, tries left=" + tries);
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					synchronized (session) {
						// reset session timeout to ECI
						this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
						session.setPendingRequest(false); // IMPORTANT - set false after timeout - because of parallel echo call
					}
					LOGGER.debug(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);
	}

	/**
	 * Execute cascaded service.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            the responder callback
	 * @throws Exception
	 *             the exception
	 */
	private void executeCascadedService(IRequest request, IResponse response, IResponderCallback responderCallback)
			throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		Service abstractService = this.getService(serviceName);
		CascadedSC cascadedSC = ((CascadedSessionService) abstractService).getCascadedSC();
		ClnExecuteCommandCascCallback callback = new ClnExecuteCommandCascCallback(request, response, responderCallback);
		cascadedSC.execute(reqMessage, callback, oti);
		return;
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME,
					SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_CLN, otiValue, Constants.MAX_OTI_VALUE,
					SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
			// message info optional
			String messageInfo = message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, messageInfo, Constants.MAX_STRING_LENGTH_256,
					SCMPError.HV_WRONG_MESSAGE_INFO);
			// cacheId optional
			String cacheId = message.getHeader(SCMPHeaderAttributeKey.CACHE_ID);
			ValidatorUtility.validateStringLengthIgnoreNull(1, cacheId, Constants.MAX_STRING_LENGTH_256,
					SCMPError.HV_WRONG_SESSION_INFO);
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