package org.serviceconnector.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;

public class EhCacheEventListener implements CacheEventListener {

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		Object entry = element.getObjectValue();
		if (entry instanceof SCCacheMetaEntry) {
			SCCacheMetaEntry metaEntry = (SCCacheMetaEntry) entry;
			CacheLogger.messageExpired(metaEntry.getCacheId());
			AppContext.getSCCache().removeDataEntriesByMetaEntry((SCCacheMetaEntry) entry, "Meta Entry expired!");
		}
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
