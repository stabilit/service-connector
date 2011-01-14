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
import org.serviceconnector.cache.CacheExpiredException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheLoadedException;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
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
public class ClnExecuteCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnExecuteCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String sessionId = reqMessage.getSessionId();
		Session session = this.getSessionById(sessionId);
		if (session.hasPendingRequest() == true) {
			SessionLogger.error("session " + sessionId + "has pending request");
		}
		session.setPendingRequest(true);
		// cancel session timeout
		this.sessionRegistry.cancelSessionTimeout(session);

		CacheManager cacheManager = null;
		if (reqMessage.getCacheId() != null) {
			cacheManager = AppContext.getCacheManager();
		}
		if (cacheManager != null && cacheManager.isCacheEnabled()) {
			// caching is enabled, if message request is a large message, then
			// ignore PRQ (part messages) and accept the ending REQ message only
			// but do not ignore any POLL (PAC) messages
			if (reqMessage.isPollRequest() == true || reqMessage.isPart() == false) {
				logger.info("client execute command with cache id = " + reqMessage.getCacheId());
				// try to load response from cache
				try {
					if (tryLoadingMessageFromCache(request, response, responderCallback)) {
						session.setPendingRequest(false);
						this.sessionRegistry.scheduleSessionTimeout(session);
						return;
					}
				} catch (Exception e) {
					session.setPendingRequest(false);
					this.sessionRegistry.scheduleSessionTimeout(session);
					throw e;
				}
			}
		}
		ClnExecuteCommandCallback callback = null;
		StatefulServer server = session.getStatefulServer();
		// try sending to the server
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);

		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			callback = new ClnExecuteCommandCallback(request, response, responderCallback, sessionId);
			try {
				server.execute(reqMessage, callback, oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					session.setPendingRequest(false);
					// only one loop outstanding - don't continue throw current exception
					// schedule session timeout
					this.sessionRegistry.scheduleSessionTimeout(session);
					logger.warn(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
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
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLength(1, sessionId, 256, SCMPError.HV_WRONG_SESSION_ID);
			// message info optional
			String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
			// cacheId optional
			String cacheId = (String) message.getHeader(SCMPHeaderAttributeKey.CACHE_ID);
			ValidatorUtility.validateStringLengthIgnoreNull(1, cacheId, 256, SCMPError.HV_WRONG_SESSION_INFO);
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
	 * Try loading message from cache. This method tries to load the message from its cache. An exception is thrown if the message is
	 * not full part of the cache. In case of a successful cache load the method return true otherwise false.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            the responder callback
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean tryLoadingMessageFromCache(IRequest request, IResponse response, IResponderCallback responderCallback)
			throws Exception {
		SCMPMessage message = request.getMessage();
		if (message.getCacheId() == null) {
			CacheLogger.debug("message has no cache id, isReply = " + message.isReply() + ", isPart = " + message.isPart()
					+ ", message = " + message.isPollRequest());
			return false;
		}
		String sessionId = message.getSessionId();
		CacheManager cacheManager = AppContext.getCacheManager();
		String serviceName = message.getServiceName();
		Cache scmpCache = cacheManager.getCache(serviceName);
		if (scmpCache == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
					"no cache instance, service=" + message.getServiceName());
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		CacheId cacheId = new CacheId(message.getCacheId());
		CacheComposite cacheComposite = scmpCache.getComposite(cacheId);

		if (cacheComposite != null) {
			synchronized (cacheManager) {
				// check if cache is loading
				if (cacheComposite.isLoading()) {
					// check if it is a part request and sequence nr in cache equals cache composite size
					CacheLogger.debug("cache is loading (" + cacheId + ")");
					int size = cacheComposite.getSize();
					int sequenceNr = cacheId.getSequenceNrInt();
					if (!(message.isPart() && (sequenceNr == size))) {
						CacheLogger.info("cache is loading, retry later, service=" + message.getServiceName() + " cacheId="
								+ message.getCacheId());
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING, "service="
								+ message.getServiceName() + " cacheId=" + message.getCacheId());
						scmpCommandException.setMessageType(this.getKey());
						throw scmpCommandException;
					}
				}
			}
			if (cacheComposite.isLoaded() && cacheComposite.isExpired()) {
				// cache has been loaded but its content message is expired, in case of a full cache id we
				// must abort this communication, because we do not exactly know the state of the cache content
				// for given cache id
				if (cacheId.isCompositeId() == false) {
					CacheLogger.warn("cache is expired and has unknown state, retry later, service name = "
							+ message.getServiceName());
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_LOADING,
							"cache is expired and has unknown state, retry later, service name = " + message.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			}
			if (cacheComposite.isLoaded() && cacheComposite.isExpired() == false) {
				CacheLogger.debug("cache composite (" + cacheId + ") found and loaded, expiration time is "
						+ cacheComposite.getExpiration());
				// cache has been loaded, try to get cache message, get the first one if cache id belongs to composite id
				// increment cache id sequence nr
				cacheId = cacheId.nextSequence();
				CacheMessage cacheMessage = scmpCache.getMessage(cacheId);
				if (cacheMessage == null) {
					CacheLogger.error("cache has illegal state, loaded but no message, cacheId=" + message.getCacheId());
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
							"cache has illegal state, loaded but no message, cacheId=" + message.getCacheId());
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
					CacheLogger.error("cache message has illegal state, cacheId=null");
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.CACHE_ERROR,
							"cache message has illegal state, cacheId=null");
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
				scmpReply.setCacheId(cacheId.getFullCacheId());
				if (cacheMessage.isCompressed()) {
					scmpReply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
				}
				scmpReply.setBody(cacheMessage.getBody());

				response.setSCMP(scmpReply);
				// schedule session timeout
				Session session = this.sessionRegistry.getSession(sessionId);
				this.sessionRegistry.scheduleSessionTimeout(session);
				session.setPendingRequest(false);
				responderCallback.responseCallback(request, response);
				CacheLogger.debug("Sent a cache message to the client cacheId=" + cacheId);
				return true; // message loaded from cache
			}
		}
		if (cacheComposite == null) {
			CacheLogger.debug("cache does not exist, start loading from server");
			// cache does not exist, this is the first request for it
			scmpCache.startLoading(message.getCacheId());
		}
		return false; // message not loaded from cache
	}

	/**
	 * The Class ClnExecuteCommandCallback.
	 */
	private class ClnExecuteCommandCallback implements ISCMPMessageCallback {

		/** The callback. */
		private IResponderCallback responderCallback;
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
			this.responderCallback = callback;
			this.request = request;
			this.response = response;
			this.sessionId = sessionId;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMPMessage reply) {
			String serviceName = reply.getServiceName();
			// check for cache id
			CacheManager cacheManager = null;
			if (reply.getCacheId() != null) {
				// try save reply in cache
				cacheManager = AppContext.getCacheManager();
			}
			if (cacheManager != null && cacheManager.isCacheEnabled()) {
				try {
					Cache scmpCache = cacheManager.getCache(serviceName);
					if (scmpCache == null) {
						CommandCallback.logger.error("cache write failed, no cache, service name = " + serviceName);
					} else {
						CacheLogger.debug("cache message put reply, scmp reply cacheId = " + reply.getCacheId() + ", isReply = "
								+ reply.isReply() + ", isPart = " + reply.isPart() + ", isPollRequest = " + reply.isPollRequest());
						CacheId messageCacheId = null;
						try {
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheLoadedException e) {
							CacheLogger.warn("cache put message failed, already loaded, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply.getCacheId());
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheExpiredException e) {
							CacheLogger.warn("cache put message failed, expired, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply.getCacheId());
							messageCacheId = scmpCache.putMessage(reply);
						}
						String fullCacheId = messageCacheId.getFullCacheId();
						CacheLogger.debug("cache message put done using full cache id = " + fullCacheId);
						reply.setCacheId(fullCacheId);
					}
				} catch (Exception e) {
					CacheLogger.debug("cache (" + reply.getCacheId() + " message put did fail = " + e.toString());
					CommandCallback.logger.error(e.toString());
				}
			}
			// forward server reply to client
			reply.setIsReply(true);
			reply.setMessageType(getKey());
			reply.setServiceName(serviceName);
			this.response.setSCMP(reply);
			// schedule session timeout
			Session session = this.sessionRegistry.getSession(this.sessionId);
			this.sessionRegistry.scheduleSessionTimeout(session);
			session.setPendingRequest(false);
			this.responderCallback.responseCallback(request, response);
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception ex) {
			SCMPMessage fault = null;
			if (ex instanceof IdleTimeoutException) {
				// operation timeout handling
				fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT_EXPIRED, "Operation timeout expired on SC");
			} else if (ex instanceof IOException) {
				fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection to server");
			} else {
				fault = new SCMPMessageFault(SCMPError.SC_ERROR, "error executing CLN_EXECUTE");
			}
			fault.setSessionId(sessionId);
			this.receive(fault);
		}
	}
}