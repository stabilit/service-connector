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
package org.serviceconnector.cmd.casc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheExpiredException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheLoadedException;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.service.Session;

/**
 * The Class ClnExecuteCommandCascCallback.
 */
public class ClnExecuteCommandCascCallback extends CommandCascCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ClnExecuteCommandCascCallback.class);
	/** The request cache id. */
	private String requestCacheId;
	/** The request message. */
	private SCMPMessage requestMessage;
	/** The request oti. */
	private int requestOTI;
	/** The session registry. */
	private SessionRegistry sessionRegistry = AppContext.getSessionRegistry();

	/**
	 * Instantiates a new ClnExecuteCommandCascCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 */
	public ClnExecuteCommandCascCallback(IRequest request, IResponse response, IResponderCallback callback) {
		super(request, response, callback);
		this.requestMessage = this.request.getMessage();
		this.requestCacheId = this.requestMessage.getCacheId();
		this.requestOTI = this.requestMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = this.requestMessage.getServiceName();
		// check for cache id
		CacheManager cacheManager = null;
		String cacheId = reply.getCacheId();
		if (cacheId != null || this.requestCacheId != null) {
			// try save reply in cache
			cacheManager = AppContext.getCacheManager();
		}
		if (cacheManager != null && cacheManager.isCacheEnabled()) {
			Cache scmpCache = null;
			try {
				CacheLogger.trace("cache casc callback, sc replied cacheId=" + cacheId + ", messageSequenceNr="
						+ reply.getMessageSequenceNr() + ", expirationDateTime="
						+ reply.getHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME));
				scmpCache = cacheManager.getCache(serviceName);
				if (scmpCache == null) {
					ClnExecuteCommandCascCallback.LOGGER.error("cache write failed, no cache, service name = " + serviceName);
				} else {
					// check if reply is fault
					// check if reply is fault
					if (reply.isFault() || (cacheId == null && this.requestCacheId != null)) {
						if (cacheId == null) {
							cacheId = this.requestCacheId;
							// this happens, when client request belongs to large message
							// caching is enabled, if message request is a large message, then
							// ignore PRQ (part messages) and accept the ending REQ message only
							// but do not ignore any POLL (PAC) messages
							if (this.requestMessage.isPollRequest() == true || this.requestMessage.isPart() == false) {
								CacheLogger.warn("cache: server did reply with no cacheId (null) we use requestCacheId="
										+ this.requestCacheId + ", request sessiondId=" + this.requestMessage.getSessionId());
							}
						}
						// remove cacheId from cache
						CacheComposite cacheComposite = scmpCache.getComposite(cacheId);
						if (cacheComposite != null) {
							// in this case the case composite state must be PART_LOADING otherwise remove this composite from cache
							if (cacheComposite.isLoadingSessionId(this.requestMessage.getSessionId())
									&& cacheComposite.isPartLoading() == false) {
								scmpCache.removeComposite(this.requestMessage.getSessionId(), cacheId);
								if (reply.isFault()) {
									CacheLogger.warn("cache casc: cache composite removed, server did reply with fault, cache ("
											+ cacheId + "), cacheComposite state=" + cacheComposite.getCacheState()
											+ ", cache loadingSessionId=" + cacheComposite.getLoadingSessionId()
											+ ", request sessiondId=" + this.requestMessage.getSessionId());
								} else {
									CacheLogger.warn("cache casc: cache composite removed, server did reply no cacheId, cache ("
											+ cacheId + "), cacheComposite state=" + cacheComposite.getCacheState()
											+ ", cache loadingSessionId=" + cacheComposite.getLoadingSessionId()
											+ ", request sessiondId=" + this.requestMessage.getSessionId());
								}
							}
						}
					} else {
						// check if clients cache id is different to servers reply cache id, if
						// so delete cache entry for clients id
						if (cacheId != null && this.requestCacheId != null && cacheId != this.requestCacheId) {
							CacheId replyCacheId = new CacheId(cacheId);
							CacheId requestCacheIdLocal = new CacheId(this.requestCacheId);
							if (replyCacheId.equalsCacheId(requestCacheIdLocal) == false) {
								// remove clients cache id from cache
								// remove cacheId from cache
								CacheComposite cacheComposite = scmpCache.getComposite(this.requestCacheId);
								if (cacheComposite != null) {
									if (cacheComposite.isLoadingSessionId(this.requestMessage.getSessionId())) {
										scmpCache.removeComposite(this.requestMessage.getSessionId(), this.requestCacheId);
										CacheLogger.warn("cache composite (" + this.requestCacheId
												+ ") removed, server did reply different cacheId, cache (" + cacheId + ")");
									}
								}
							}
						}
						CacheLogger.trace("cache message put reply, scmp reply cacheId = " + reply.getCacheId() + ", isReply = "
								+ reply.isReply() + ", isPart = " + reply.isPart() + ", isPollRequest = " + reply.isPollRequest());
						CacheId messageCacheId = null;
						try {
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheLoadedException e) {
							CacheLogger.warn("cache put message failed, already loaded, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply, this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheExpiredException e) {
							CacheLogger.warn("cache put message failed, expired, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply, this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						}
						CacheLogger.trace("cache message put done using full cacheId = " + messageCacheId.getCacheId()
								+ ", cachePartNr=" + messageCacheId.getSequenceNr());
						reply.setFullCacheId(messageCacheId);
					}
				}
			} catch (Exception e) {
				CacheLogger.trace("cache (" + reply.getCacheId() + ") message put did fail = " + e.toString());
				ClnExecuteCommandCascCallback.LOGGER.error(e.toString());
				if (scmpCache != null) {
					scmpCache.removeComposite(this.requestMessage.getSessionId(), this.requestCacheId);
					CacheLogger
							.warn("cache composite removed because an expcetion did occure, cache (" + this.requestCacheId + ")");
				}
			}
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(this.msgType);
		reply.setServiceName(serviceName);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	@Override
	public void receive(Exception ex) {
		LOGGER.warn(ex);
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC sid="
					+ requestMessage.getSessionId());
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection to server sid="
					+ requestMessage.getSessionId());
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "error executing " + this.msgType + " sid="
					+ requestMessage.getSessionId());
		}
		String serviceName = this.requestMessage.getServiceName();
		String sessionId = this.requestMessage.getSessionId();
		// forward server reply to client
		fault.setSessionId(this.request.getMessage().getSessionId());
		fault.setIsReply(true);
		fault.setMessageType(this.msgType);
		fault.setServiceName(serviceName);
		this.response.setSCMP(fault);
		// check for cache id
		CacheManager cacheManager = null;
		if (this.requestCacheId != null) {
			// try save reply in cache
			cacheManager = AppContext.getCacheManager();
		}
		if (cacheManager != null && cacheManager.isCacheEnabled()) {
			Cache scmpCache = null;
			try {
				scmpCache = cacheManager.getCache(serviceName);
				if (scmpCache == null) {
					ClnExecuteCommandCascCallback.LOGGER.error("cache write failed, no cache, service name = " + serviceName);
				} else {
					// an exception did occur, remove those composite from cache
					// remove request cacheId from cache
					CacheComposite cacheComposite = scmpCache.getComposite(this.requestCacheId);
					if (cacheComposite != null) {
						scmpCache.removeComposite(sessionId, this.requestCacheId);
						CacheLogger.warn("cache composite removed, server did reply with fault, cache (" + this.requestCacheId
								+ ")");
					}
				}
			} catch (Exception e) {
				CacheLogger.trace("cache (" + this.requestCacheId + ") message put did fail = " + e.toString());
				ClnExecuteCommandCascCallback.LOGGER.error(e.toString());
				if (scmpCache != null) {
					scmpCache.removeComposite(sessionId, this.requestCacheId);
					CacheLogger
							.warn("cache composite removed because an exception did occure, cache (" + this.requestCacheId + ")");
				}
			}
		}
		this.responderCallback.responseCallback(request, response);
	}
}
