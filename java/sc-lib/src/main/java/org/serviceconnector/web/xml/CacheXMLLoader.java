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

package org.serviceconnector.web.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.Constants;
import org.serviceconnector.cache.ISCCache;
import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.cache.SC_CACHE_ENTRY_STATE;
import org.serviceconnector.cache.SC_CACHE_TYPE;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class CacheXMLLoader.
 */
public class CacheXMLLoader extends AbstractXMLLoader {

	/**
	 * Load body.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		writer.writeStartElement("cache");
		org.serviceconnector.cache.SCCacheManager cacheManager = AppContext.getCacheManager();
		SCCacheConfiguration cacheConfiguration = cacheManager.getCacheConfiguration();
		this.writeCacheConfiguration(writer, cacheConfiguration);
		writer.writeEndElement(); // close cache tag
		writer.writeStartElement("cacheLoading");
		this.writeCacheLoading(writer, cacheManager);
		writer.writeEndElement();
		writer.writeStartElement("caches");
		this.writeCaches(writer, request);
		writer.writeEndElement(); // close caches tag
	}

	/**
	 * Write cache configuration.
	 * 
	 * @param writer
	 *            the writer
	 * @param cacheConfiguration
	 *            the cache configuration
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheConfiguration(XMLStreamWriter writer, SCCacheConfiguration cacheConfiguration) throws XMLStreamException {
		writer.writeStartElement("config");
		writer.writeStartElement("diskPath");
		writer.writeCharacters(cacheConfiguration.getDiskPath());
		writer.writeEndElement(); // close diskPath tag
		writer.writeStartElement("maxElementsInMemory");
		writer.writeCharacters(String.valueOf(cacheConfiguration.getMaxElementsInMemory()));
		writer.writeEndElement(); // close maxElementsInMemory tag
		writer.writeStartElement("maxElementsOnDisk");
		writer.writeCharacters(String.valueOf(cacheConfiguration.getMaxElementsOnDisk()));
		writer.writeEndElement(); // close maxElementsOnDisk tag
		writer.writeStartElement("enabled");
		writer.writeCharacters(String.valueOf(cacheConfiguration.isCacheEnabled()));
		writer.writeEndElement(); // close enabled tag
		writer.writeEndElement(); // close config tag
	}

	/**
	 * Write caches.
	 * 
	 * @param writer
	 *            the writer
	 * @param cacheKeys
	 *            the caches
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCaches(XMLStreamWriter writer, IWebRequest request) throws Exception {
		String cacheParam = request.getParameter("cache");
		int simulation = this.getParameterInt(request, "sim", 0);
		List<String> cacheKeys = new ArrayList<String>();
		cacheKeys.add(SC_CACHE_TYPE.META_DATA_CACHE.name());
		cacheKeys.add(SC_CACHE_TYPE.DATA_CACHE.name());
		Paging paging = this.writePagingAttributes(writer, request, cacheKeys.size(), ""); // no prefix
		// String showSessionsParameter = request.getParameter("showsessions");
		int startIndex = paging.getStartIndex();
		int endIndex = paging.getEndIndex();

		for (int i = startIndex; i < endIndex; i++) {
			String cacheKey = cacheKeys.get(i);
			ISCCache<?> scCache = AppContext.getCacheRegistry().getCache(cacheKey);
			writer.writeStartElement("cache");
			writer.writeStartElement("cacheName");
			writer.writeCharacters(scCache.getCacheName());
			writer.writeEndElement(); // close cacheName tag
			writer.writeStartElement("cachedMessageCount");
			writer.writeCharacters(String.valueOf(scCache.getKeyList().size() + simulation));
			writer.writeEndElement(); // close cachedMessageCount tag
			writer.writeStartElement("numberOfMessagesInMemoryStore");
			writer.writeCharacters(String.valueOf(scCache.getNumberOfMessagesInStore()));
			writer.writeEndElement(); // close memoryStoreSize tag
			writer.writeStartElement("numberOfMessagesInDiskStore");
			writer.writeCharacters(String.valueOf(scCache.getNumberOfMessagesInDiskStore()));
			writer.writeEndElement(); // close diskStoreSize tag
			if (scCache.getCacheName().equals(cacheParam)) {
				writeCacheDetails(writer, scCache, request);
			}
			writer.writeEndElement(); // close cache tag
		}
	}

	/**
	 * Write cache loading.
	 * 
	 * @param writer
	 *            the writer
	 * @param cacheManager
	 *            the cache manager
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheLoading(XMLStreamWriter writer, org.serviceconnector.cache.SCCacheManager cacheManager)
			throws XMLStreamException {
		HashMap<String, String> loadingSessionIds = cacheManager.getLoadingSessionIds();
		Set<String> sessionIds = loadingSessionIds.keySet();
		for (String sid : sessionIds) {
			writer.writeStartElement("session");
			writer.writeAttribute("sessionId", sid);
			writer.writeStartElement("cacheId");
			writer.writeCharacters(loadingSessionIds.get(sid));
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * Write cache details.
	 * 
	 * @param writer
	 *            the writer
	 * @param cache
	 *            the cache
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheDetails(XMLStreamWriter writer, ISCCache<?> cache, IWebRequest request) throws Exception {
		writer.writeStartElement("details");
		List<String> cacheKeys = cache.getKeyList();
		// sort cacheKeys
		if (cache.getCacheName().equals(SC_CACHE_TYPE.DATA_CACHE.name())) {
			Collections.sort(cacheKeys, new DataCacheKeyComparator());
		} else {
			Collections.sort(cacheKeys);
		}
		int simulation = this.getParameterInt(request, "sim", 0);
		if (simulation > 0) {
			if (cacheKeys == null) {
				cacheKeys = new ArrayList<String>();
			}
			List<String> sim = new ArrayList<String>();
			System.arraycopy(cacheKeys, 0, sim, 0, cacheKeys.size());
			for (int i = cacheKeys.size(); i < simulation + cacheKeys.size(); i++) {
				sim.add("sim " + i);
			}
			cacheKeys = sim;
		}
		if (cacheKeys == null) {
			writer.writeAttribute("size", "0");
			writer.writeEndElement();
			return;
		}
		Paging paging = this.writePagingAttributes(writer, request, cacheKeys.size(), "comp_"); // no prefix
		// String showSessionsParameter = request.getParameter("showsessions");
		int startIndex = paging.getStartIndex();
		int endIndex = paging.getEndIndex();

		for (int i = startIndex; i < endIndex; i++) {
			String key = cacheKeys.get(i);
			if (cache.getCacheName().equals(SC_CACHE_TYPE.META_DATA_CACHE.name())) {
				SCCacheMetaEntry metaEntry = (SCCacheMetaEntry) cache.get(key);
				if (metaEntry == null && simulation > 0) {
					metaEntry = new SCCacheMetaEntry("");
				}
				if (metaEntry != null) {
					writeCacheMetaEntry(writer, cache, key, metaEntry, request);
				}
			} else {
				SCMPMessage cachedMessage = (SCMPMessage) cache.get(key);
				if (cachedMessage == null && simulation > 0) {
					cachedMessage = new SCMPMessage("");
				}
				if (cachedMessage != null) {
					writeCacheMessage(writer, cache, key, cachedMessage, request);
				}
			}
		}
		writer.writeEndElement(); // end of details
	}

	/**
	 * Write cache meta entry.
	 * 
	 * @param writer
	 *            the writer
	 * @param cache
	 *            the cache
	 * @param cacheKey
	 *            the cache key
	 * @param metaEntry
	 *            the cache composite
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheMetaEntry(XMLStreamWriter writer, ISCCache<?> cache, String cacheKey, SCCacheMetaEntry metaEntry,
			IWebRequest request) throws Exception {
		int simulation = this.getParameterInt(request, "sim", 0);
		writer.writeStartElement("cacheMessage");
		writer.writeStartElement("key");
		writer.writeCharacters(cacheKey);
		writer.writeEndElement();

		writer.writeStartElement("state");
		writer.writeCharacters(metaEntry.getSCCacheEntryState().name());
		writer.writeEndElement(); // end of state
		writer.writeStartElement("loadingSessionId");
		writer.writeCharacters(String.valueOf(metaEntry.getLoadingSessionId()));
		writer.writeEndElement(); // end of loadingSessionId
		writer.writeStartElement("expirationTimeout");
		Date expireDate = cache.getExpirationTime(cacheKey);
		Date creationDate = cache.getCreationTime(cacheKey);
		long expireTimeoutMillis = expireDate.getTime() - creationDate.getTime();
		writer.writeCharacters(String.valueOf(expireTimeoutMillis));
		writer.writeEndElement(); // end of expirationTimeout
		writer.writeStartElement("creation");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(creationDate));
		writer.writeEndElement(); // end of creation
		writer.writeStartElement("lastAccess");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(metaEntry.getLastModifiedTime()));
		writer.writeEndElement(); // end of lastAccess
		writer.writeStartElement("size");
		writer.writeCharacters(String.valueOf(metaEntry.getNumberOfParts() + simulation));
		writer.writeEndElement(); // end of size
		writer.writeStartElement("header");
		Map<String, String> metaEntryHeader = metaEntry.getHeader();
		this.writeHeaderMap(writer, metaEntryHeader);
		writer.writeEndElement(); // end of header
		writer.writeEndElement(); // end of cacheMessage
	}

	/**
	 * Write cache message.
	 * 
	 * @param writer
	 *            the writer
	 * @param cache
	 *            the cache
	 * @param cacheKey
	 *            the cache key
	 * @param cacheMessage
	 *            the cache message
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	private void writeCacheMessage(XMLStreamWriter writer, ISCCache<?> cache, String cacheKey, SCMPMessage cacheMessage,
			IWebRequest request) throws Exception {
		writer.writeStartElement("cacheMessage");
		writer.writeStartElement("key");
		writer.writeCharacters(cacheKey);
		writer.writeEndElement();
		writer.writeStartElement("state");
		writer.writeCharacters(SC_CACHE_ENTRY_STATE.LOADED.name());
		writer.writeEndElement(); // end of state
		writer.writeStartElement("expirationTimeout");
		Date expireDate = cache.getExpirationTime(cacheKey);
		Date creationDate = cache.getCreationTime(cacheKey);
		long expireTimeoutMillis = expireDate.getTime() - creationDate.getTime();
		writer.writeCharacters(String.valueOf(expireTimeoutMillis));
		writer.writeEndElement(); // end of expirationTimeout
		writer.writeStartElement("loadingSessionId");
		writer.writeCharacters(cacheMessage.getSessionId());
		writer.writeEndElement(); // end of loadingSessionId
		writer.writeStartElement("creation");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(creationDate));
		writer.writeEndElement(); // end of creation
		writer.writeStartElement("lastAccess");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cache.getLastAccessTime(cacheKey)));
		writer.writeEndElement(); // end of lastAccess
		writer.writeStartElement("header");
		Map<String, String> cacheMessageHeader = cacheMessage.getHeader();
		this.writeHeaderMap(writer, cacheMessageHeader);
		writer.writeEndElement(); // end of header
		writer.writeEndElement(); // end of cacheMessage
	}

	/**
	 * The Class CacheKeyComparator. The key comparator contains knowledge of sorting the keys. It compares serviceName + cacheId.
	 * If both are the same value partNr is considered.
	 */
	private class DataCacheKeyComparator implements Comparator<String> {

		@Override
		public int compare(String cacheKey1, String cacheKey2) {
			String serviceNameCacheId1 = cacheKey1.substring(0, cacheKey1.lastIndexOf(Constants.SLASH));
			String serviceNameCacheId2 = cacheKey2.substring(0, cacheKey2.lastIndexOf(Constants.SLASH));
			int stringResult = serviceNameCacheId1.compareTo(serviceNameCacheId2);
			if (stringResult != 0) {
				// // service names are not equal
				return stringResult;
			}

			String partNr1 = cacheKey1.substring(cacheKey1.lastIndexOf(Constants.SLASH) + 1);
			String partNr2 = cacheKey2.substring(cacheKey2.lastIndexOf(Constants.SLASH) + 1);
			int partNr1Int = new Integer(partNr1);
			int partNr2Int = new Integer(partNr2);

			if (partNr1Int == partNr2Int)
				return 0;
			if (partNr1Int > partNr2Int)
				return 1;
			return -1;
		}
	}
}