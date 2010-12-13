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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnExecuteCommand. Responsible for validation and execution of execute command. Execute command sends any data to the
 * server. Execute command runs asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ClnExecuteCommand extends CommandAdapter implements IAsyncCommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnExecuteCommand.class);

	/**
	 * Instantiates a new ClnExecuteCommand.
	 */
	public ClnExecuteCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		// check for cache id
		if (message.getCacheId() != null) {
			logger.info("client execute command with cache id = " + message.getCacheId());
			// try to load response from cache
			try {
				CacheManager scmpCacheManager = AppContext.getCacheManager();
				String serviceName = message.getServiceName();
				Cache scmpCache = scmpCacheManager.getCache(serviceName);
				if (scmpCache == null) {
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
							"no cache instance, service name = " + message.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
				CacheId cacheId = new CacheId(message.getCacheId());
				CacheComposite cacheComposite = scmpCache.getComposite(cacheId);
				if (cacheComposite != null) {
					synchronized (scmpCacheManager) {
						// check if cache is loading
						if (cacheComposite.isLoading()) {
							// check if it is a part request and sequence nr in cache equals cache composite size
							int size = cacheComposite.getSize();
							int sequenceNr = cacheId.getSequenceNrInt();
							if (!(message.isPart() && (sequenceNr == size))) {
								CacheLogger.warn("cache is loading retry later, service name = " + message.getServiceName());
								SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING,
										"cache is loading, retry it later, service name = " + message.getServiceName());
								scmpCommandException.setMessageType(this.getKey());
								throw scmpCommandException;
							}
						}
					}
					if (cacheComposite.isLoaded()) {
						// cache has been loaded, try to get cache message, get the first one if cache id belongs to composite id
						// increment cache id sequence nr
						cacheId = cacheId.nextSequence();
						CacheMessage cacheMessage = scmpCache.getMessage(cacheId);
						if (cacheMessage == null) {
							// check if cache is loading
							SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING,
									"cache has illegal state, loaded but no message, cacheId = " + message.getCacheId());
							scmpCommandException.setMessageType(this.getKey());
							throw scmpCommandException;
						}
						SCMPMessage scmpReply = null;
						if (cacheComposite.isLastMessage(cacheMessage)) {
							scmpReply = new SCMPMessage();
						} else {
							scmpReply = new SCMPPart();
						}
						scmpReply.setMessageType(getKey());
						cacheId = cacheMessage.getCacheId();
						if (cacheId == null) {
							// check if cache is loading
							SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING,
									"cache message has illegal state, no cacheId (null)");
							scmpCommandException.setMessageType(this.getKey());
							throw scmpCommandException;
						}
						scmpReply.setCacheId(cacheId.getFullCacheId());
						if (cacheMessage.isCompressed()) {
							scmpReply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
						}
						scmpReply.setBody(cacheMessage.getBody());

						response.setSCMP(scmpReply);
						responderCallback.responseCallback(request, response);
						// schedule session timeout
						Session session = this.sessionRegistry.getSession(sessionId);
						this.sessionRegistry.scheduleSessionTimeout(session);
						CacheLogger.debug("Sent a cache message to the client (" + cacheId + " "
								+ message.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME) + ")");
						return;
					}
				}
				if (cacheComposite == null) {
					// cache does not exist, this is the first request for it
					scmpCache.startLoading(message.getCacheId());
				}
			} catch (Exception e) {
				Session session = this.sessionRegistry.getSession(sessionId);
				this.sessionRegistry.scheduleSessionTimeout(session);
				throw e;
			}
		}

		Session session = this.getSessionById(sessionId);
		// cancel session timeout
		this.sessionRegistry.cancelSessionTimeout(session);

		StatefulServer server = session.getStatefulServer();
		// try sending to the server
		int oti = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);

		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			ClnExecuteCommandCallback callback = new ClnExecuteCommandCallback(request, response, responderCallback, sessionId);
			try {
				server.execute(message, callback, oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					// schedule session timeout
					this.sessionRegistry.scheduleSessionTimeout(session);
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION,
							"no free connection on server for service " + message.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			} catch (Exception ex) {
				// schedule session timeout
				this.sessionRegistry.scheduleSessionTimeout(session);
				throw ex;
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// msgSequenceNr
			String msgSequenceNr = message.getMessageSequenceNr();
			if (msgSequenceNr == null || msgSequenceNr.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "msgSequenceNr must be set");
			}
			// serviceName
			String serviceName = message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// operation timeout
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
			// message info
			String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
			if (messageInfo != null) {
				ValidatorUtility.validateStringLength(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
			}
			// compression
			message.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
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

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * The Class ClnExecuteCommandCallback.
	 */
	private class ClnExecuteCommandCallback extends CommandCallback {

		/** The callback. */
		private IResponderCallback callback;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;
		/** The session id. */
		private String sessionId;

		private SessionRegistry sessionRegistry = AppContext.getSessionRegistry();

		/**
		 * Instantiates a new ClnExecuteCommandCallback.
		 * 
		 * @param request
		 *            the request
		 * @param response
		 *            the response
		 * @param callback
		 *            the callback
		 */
		public ClnExecuteCommandCallback(IRequest request, IResponse response, IResponderCallback callback, String sessionId) {
			this.callback = callback;
			this.request = request;
			this.response = response;
			this.sessionId = sessionId;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) {
			// check for cache id
			if (scmpReply.getCacheId() != null) {
				// try save reply in cache
				try {
					CacheManager scmpCacheManager = AppContext.getCacheManager();
					String serviceName = scmpReply.getServiceName();
					Cache scmpCache = scmpCacheManager.getCache(serviceName);
					if (scmpCache == null) {
						CommandCallback.logger.error("cache write failed, no cache, service name = " + serviceName);
					} else {
						CacheId messageCacheId = scmpCache.putMessage(scmpReply);
						scmpReply.setCacheId(messageCacheId.getFullCacheId());
					}
				} catch (Exception e) {
					CommandCallback.logger.error(e.toString());
				}
			}
			scmpReply.setMessageType(getKey());
			this.response.setSCMP(scmpReply);
			this.callback.responseCallback(request, response);
			// schedule session timeout
			Session session = this.sessionRegistry.getSession(this.sessionId);
			this.sessionRegistry.scheduleSessionTimeout(session);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Exception ex) {
			SCMPMessage fault = null;
			if (ex instanceof IdleTimeoutException) {
				// operation timeout handling
				fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT_EXPIRED, ERROR_STRING_TIMEOUT);
			} else if (ex instanceof IOException) {
				fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, ERROR_STRING_CONNECTION);
			} else if (ex instanceof ConnectionPoolBusyException) {
				fault = new SCMPMessageFault(ex, SCMPError.SC_ERROR, ERROR_STRING_FAIL);
			} else {
				fault = new SCMPMessageFault(SCMPError.SC_ERROR, ERROR_STRING_FAIL);
			}
			// set sid & serviceName for EXC
			try {
				SCMPMessage message;
				message = request.getMessage();
				fault.setServiceName(message.getServiceName());
			} catch (Exception e) {
				logger.warn("not possible to set service name in EXC of execute command.");
			}
			fault.setSessionId(sessionId);
			this.callback(fault);
			// schedule session timeout
			Session session = this.sessionRegistry.getSession(this.sessionId);
			this.sessionRegistry.scheduleSessionTimeout(session);
		}
	}
}