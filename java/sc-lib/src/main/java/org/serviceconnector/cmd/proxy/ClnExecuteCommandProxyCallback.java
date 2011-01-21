package org.serviceconnector.cmd.proxy;

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
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class ClnExecuteCommandProxyCallback.
 */
public class ClnExecuteCommandProxyCallback implements ISCMPMessageCallback {

	/** The Constant logger. */
	protected final Logger logger = Logger.getLogger(ClnExecuteCommandProxyCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The session id. */
	private String sessionId;
	/** The request cache id. */
	private String requestCacheId;
	/** The request service name. */
	private String requestServiceName;
	/** The request oti. */
	private int requestOTI;

	/**
	 * Instantiates a new ClnExecuteCommandProxyCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 */
	public ClnExecuteCommandProxyCallback(IRequest request, IResponse response, IResponderCallback callback, String sessionId) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.sessionId = sessionId;
		SCMPMessage message = this.request.getMessage();
		this.requestCacheId = message.getCacheId();
		this.requestServiceName = message.getServiceName();
		this.requestOTI = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// check for cache id
		CacheManager cacheManager = null;
		String cacheId = reply.getCacheId();
		if (cacheId != null || this.requestCacheId != null) {
			// try save reply in cache
			cacheManager = AppContext.getCacheManager();
		}
		if (cacheManager != null && cacheManager.isCacheEnabled()) {
			try {
				Cache scmpCache = cacheManager.getCache(this.requestServiceName);
				if (scmpCache == null) {
					this.logger.error("cache write failed, no cache, service name = " + this.requestServiceName);
				} else {
					// check if reply is fault
					if (reply.isFault() || (cacheId == null && this.requestCacheId != null)) {
						if (cacheId == null) {
							cacheId = this.requestCacheId;
						}
						// remove cacheId from cache
						CacheComposite cacheComposite = scmpCache.getComposite(cacheId);
						if (cacheComposite != null) {
							scmpCache.removeComposite(cacheId);
							CacheLogger.warn("cache composite removed, server did reply with fault, cache (" + cacheId + ")");
						}
					} else {
						// check if clients cache id is different to servers reply cache id, if
						// so delete cache entry for clients id
						if (cacheId != null && this.requestCacheId != null && cacheId != this.requestCacheId) {
							CacheId replyCacheId = new CacheId(cacheId);
							CacheId requestCacheId = new CacheId(this.requestCacheId);
							if (replyCacheId.equalsCacheId(requestCacheId) == false) {
								// remove clients cache id from cache
								// remove cacheId from cache
								CacheComposite cacheComposite = scmpCache.getComposite(this.requestCacheId);
								if (cacheComposite != null) {
									scmpCache.removeComposite(this.requestCacheId);
									CacheLogger.warn("cache composite (" + this.requestCacheId
											+ ") removed, server did reply different cache id, cache (" + cacheId + ")");
								}
							}
						}
						CacheLogger.debug("cache message put reply, scmp reply cacheId = " + reply.getCacheId() + ", isReply = "
								+ reply.isReply() + ", isPart = " + reply.isPart() + ", isPollRequest = " + reply.isPollRequest());
						CacheId messageCacheId = null;
						try {
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheLoadedException e) {
							CacheLogger.warn("cache put message failed, already loaded, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply.getCacheId(), this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheExpiredException e) {
							CacheLogger.warn("cache put message failed, expired, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply.getCacheId(), this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						}
						String fullCacheId = messageCacheId.getFullCacheId();
						CacheLogger.debug("cache message put done using full cache id = " + fullCacheId);
						reply.setCacheId(fullCacheId);
					}
				}
			} catch (Exception e) {
				CacheLogger.debug("cache (" + reply.getCacheId() + ") message put did fail = " + e.toString());
				this.logger.error(e.toString());
			}
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(SCMPMsgType.CLN_EXECUTE);
		reply.setServiceName(this.requestServiceName);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection to server");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "error executing CLN_EXECUTE");
		}
		fault.setSessionId(sessionId);
		this.receive(fault);
	}
}
