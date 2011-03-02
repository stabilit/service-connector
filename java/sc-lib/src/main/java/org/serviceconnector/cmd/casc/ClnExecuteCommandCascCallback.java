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

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(ClnExecuteCommandCascCallback.class);
	/** The request cache id. */
	private String requestCacheId;
	private SCMPMessage requestMessage;
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
		this.requestMessage = this.request.getMessage();
		this.requestCacheId = this.requestMessage.getCacheId();
		this.requestOTI = this.requestMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
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
				CacheLogger.debug("cache casc callback, sc replied cacheId=" + cacheId);
				Cache scmpCache = cacheManager.getCache(serviceName);
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
							if (cacheComposite.isLoadingSessionId(this.requestMessage.getSessionId()) && cacheComposite.isPartLoading() == false) {
								scmpCache.removeComposite(this.requestMessage.getSessionId(), cacheId);
								if (reply.isFault()) {
									CacheLogger.warn("cache casc: cache composite removed, server did reply with fault, cache ("
											+ cacheId + "), cacheComposite state=" + cacheComposite.getCacheState() + ", cache loadingSessionId=" + cacheComposite.getLoadingSessionId()
											+ ", request sessiondId=" + this.requestMessage.getSessionId());
								} else {
									CacheLogger.warn("cache casc: cache composite removed, server did reply no cacheId, cache ("
											+ cacheId + "), cacheComposite state=" + cacheComposite.getCacheState() + ", cache loadingSessionId=" + cacheComposite.getLoadingSessionId()
											+ ", request sessiondId=" + this.requestMessage.getSessionId());
								}
							}
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
									if (cacheComposite.isLoadingSessionId(this.requestMessage.getSessionId())) {
										scmpCache.removeComposite(this.requestMessage.getSessionId(), this.requestCacheId);
										CacheLogger.warn("cache composite (" + this.requestCacheId
												+ ") removed, server did reply different cacheId, cache (" + cacheId + ")");
									}
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
							scmpCache.startLoading(reply, this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						} catch (CacheExpiredException e) {
							CacheLogger.warn("cache put message failed, expired, remove cache (" + reply.getCacheId()
									+ ") and start loading");
							scmpCache.startLoading(reply, this.requestOTI);
							messageCacheId = scmpCache.putMessage(reply);
						}
						CacheLogger.debug("cache message put done using full cacheId = " + messageCacheId.getCacheId()
								+ ", cachePartNr=" + messageCacheId.getSequenceNr());
						reply.setFullCacheId(messageCacheId);
					}
				}
			} catch (Exception e) {
				CacheLogger.debug("cache (" + reply.getCacheId() + ") message put did fail = " + e.toString());
				ClnExecuteCommandCascCallback.LOGGER.error(e.toString());
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
