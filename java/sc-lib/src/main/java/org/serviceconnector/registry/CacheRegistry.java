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
package org.serviceconnector.registry;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.serviceconnector.cache.ISCCache;
import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class CacheRegistry. Registries stores caches available in SC.
 */
public class CacheRegistry extends Registry<String, ISCCache<?>> {

	/**
	 * Adds the cache.
	 * 
	 * @param key
	 *            the key
	 * @param cache
	 *            the cache
	 */
	public void addCache(String key, ISCCache<?> cache) {
		super.put(key, cache);
	}

	/**
	 * Gets the cache.
	 * 
	 * @param key
	 *            the key
	 * @return the cache
	 */
	public ISCCache<?> getCache(String key) {
		return this.get(key);
	}

	/**
	 * Removes the cache.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeCache(String key) {
		this.remove(key);
	}

	/**
	 * Dump.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("caches");
		Set<Entry<String, ISCCache<?>>> entries = this.registryMap.entrySet();
		for (Entry<String, ISCCache<?>> entry : entries) {
			ISCCache<?> scCache = entry.getValue();
			writer.writeStartElement("cache");
			writer.writeAttribute("cacheName", scCache.getCacheName());
			writer.writeAttribute("numberOfMessagesInDiskStore", scCache.getNumberOfMessagesInDiskStore());
			writer.writeAttribute("numberOfMessagesInStore", scCache.getNumberOfMessagesInStore());
			writer.writeAttribute("numberOfMessagesInCache", scCache.getKeyList().size());

			List<String> keys = scCache.getKeyList();
			writer.writeStartElement("cacheElements");
			for (String key : keys) {
				writer.writeStartElement("cacheElement");
				Object cacheMessage = scCache.get(key);

				if (cacheMessage instanceof SCMPMessage) {
					SCMPMessage scmp = ((SCMPMessage) cacheMessage);
					writer.writeAttribute("cacheKey", key);
					writer.writeAttribute("serviceName", scmp.getServiceName());
					writer.writeAttribute("sessionId", scmp.getSessionId());
					writer.writeAttribute("bodyLength", scmp.getBodyLength());
					writer.writeAttribute("expirationTime", DateTimeUtility.getDateTimeAsString(scCache.getExpirationTime(key)));
					writer.writeAttribute("creationTime", DateTimeUtility.getDateTimeAsString(scCache.getCreationTime(key)));
					writer.writeAttribute("lastAccessTime", DateTimeUtility.getDateTimeAsString(scCache.getLastAccessTime(key)));
					writer.writeAttribute("bodyLength", scmp.getBodyLength());
				} else if (cacheMessage instanceof SCCacheMetaEntry) {
					SCCacheMetaEntry metaEntry = ((SCCacheMetaEntry) cacheMessage);
					writer.writeAttribute("cacheKey", key);
					writer.writeAttribute("loadingSessionId", metaEntry.getLoadingSessionId());
					writer.writeAttribute("loadingTimeoutMillis", metaEntry.getLoadingTimeoutMillis());
					writer.writeAttribute("scCacheEntryState", metaEntry.getSCCacheEntryState().name());
					writer.writeAttribute("numberOfParts", metaEntry.getNumberOfParts());
					writer.writeAttribute("expirationTime", DateTimeUtility.getDateTimeAsString(scCache.getExpirationTime(key)));
					writer.writeAttribute("creationTime", DateTimeUtility.getDateTimeAsString(scCache.getCreationTime(key)));
					writer.writeAttribute("lastModifiedTime", DateTimeUtility.getDateTimeAsString(metaEntry.getLastModifiedTime()));
					writer.writeAttribute("lastAccessTime", DateTimeUtility.getDateTimeAsString(scCache.getLastAccessTime(key)));
				}
				writer.writeEndElement(); // end of cacheElement
			}
			writer.writeEndElement(); // end of cacheElements
			writer.writeEndElement(); // end of cache
		}
		writer.writeEndElement(); // end of caches
	}
}