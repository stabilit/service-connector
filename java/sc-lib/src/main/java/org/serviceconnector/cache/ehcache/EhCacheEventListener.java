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
package org.serviceconnector.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.CacheLogger;

/**
 * The listener interface for receiving ehCacheEvent events.
 * The class that is interested in processing a ehCacheEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addEhCacheEventListener<code> method. When
 * the ehCacheEvent event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see EhCacheEventEvent
 */
public class EhCacheEventListener implements CacheEventListener {

	/** {@inheritDoc} */
	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
	}

	/** {@inheritDoc} */
	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
	}

	/** {@inheritDoc} */
	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
	}

	/** {@inheritDoc} */
	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		Object entry = element.getObjectValue();
		if (entry instanceof SCCacheMetaEntry) {
			SCCacheMetaEntry metaEntry = (SCCacheMetaEntry) entry;
			CacheLogger.messageExpired(metaEntry.getCacheId());
			AppContext.getSCCache().removeDataEntriesByMetaEntry((SCCacheMetaEntry) entry, "Meta Entry expired!");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	/** {@inheritDoc} */
	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
	}

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
