package org.serviceconnector.cmd.casc;

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
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class ClnExecuteCommandCascCallback.
 */
public class ClnExecuteCommandCascCallback extends ClnCommandCascCallback {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ClnExecuteCommandCascCallback.class);
	/** The request cache id. */
	private String requestCacheId;
	/** The request oti. */
	private int requestOTI;

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
		SCMPMessage message = this.request.getMessage();
		this.requestCacheId = message.getCacheId();
		this.requestOTI = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = this.request.getMessage().getServiceName();
		// check for cache id
		CacheManager cacheManager = null;
		String cacheId = reply.getCacheId();
		if (cacheId != null || this.requestCacheId != null) {
			// try save reply in cache
			cacheManager = AppContext.getCacheManager();
		}
		if (cacheManager != null && cacheManager.isCacheEnabled()) {
			try {
				Cache scmpCache = cacheManager.getCache(serviceName);
				if (scmpCache == null) {
					ClnExecuteCommandCascCallback.logger.error("cache write failed, no cache, service name = " + serviceName);
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
				ClnExecuteCommandCascCallback.logger.error(e.toString());
			}
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(this.msgType);
		reply.setServiceName(serviceName);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}
}
