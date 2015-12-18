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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.cache.CacheIdComparator;
import org.serviceconnector.cache.ISCCacheModule;
import org.serviceconnector.cache.SCCacheMetaEntry;
import org.serviceconnector.cache.SC_CACHE_ENTRY_STATE;
import org.serviceconnector.cache.SC_CACHE_MODULE_TYPE;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPVersion;
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
		org.serviceconnector.cache.SCCache cache = AppContext.getSCCache();
		SCCacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
		this.writeCacheConfiguration(writer, cacheConfiguration);
		writer.writeStartElement("cacheLoading");
		this.writeCacheLoading(writer, cache);
		writer.writeEndElement();
		writer.writeStartElement("cacheModules");
		this.writeCaches(writer, request);
		writer.writeEndElement(); // close cache tag
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
	 * Write cache modules.
	 * 
	 * @param writer
	 *            the writer
	 * @param cacheModuleKeys
	 *            the cache module keys
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCaches(XMLStreamWriter writer, IWebRequest request) throws Exception {
		String cacheModuleParam = request.getParameter("cacheModule");
		int simulation = this.getParameterInt(request, "sim", 0);
		List<String> cacheModuleKeys = new ArrayList<String>();
		cacheModuleKeys.add(SC_CACHE_MODULE_TYPE.META_DATA_CACHE_MODULE.name());
		cacheModuleKeys.add(SC_CACHE_MODULE_TYPE.DATA_CACHE_MODULE.name());
		Paging paging = this.writePagingAttributes(writer, request, cacheModuleKeys.size(), ""); // no prefix
		// String showSessionsParameter = request.getParameter("showsessions");
		int startIndex = paging.getStartIndex();
		int endIndex = paging.getEndIndex();

		for (int i = startIndex; i < endIndex; i++) {
			String cacheModuleKey = cacheModuleKeys.get(i);
			ISCCacheModule<?> scCacheModule = AppContext.getCacheModuleRegistry().getCache(cacheModuleKey);
			writer.writeStartElement("cacheModule");
			writer.writeStartElement("cacheModuleName");
			writer.writeCharacters(scCacheModule.getCacheModuleName());
			writer.writeEndElement(); // close cacheModuleName tag
			writer.writeStartElement("cachedMessageCount");
			writer.writeCharacters(String.valueOf(scCacheModule.getKeyList().size() + simulation));
			writer.writeEndElement(); // close cachedMessageCount tag
			writer.writeStartElement("numberOfMessagesInMemoryStore");
			writer.writeCharacters(String.valueOf(scCacheModule.getNumberOfMessagesInStore()));
			writer.writeEndElement(); // close memoryStoreSize tag
			writer.writeStartElement("numberOfMessagesInDiskStore");
			writer.writeCharacters(String.valueOf(scCacheModule.getNumberOfMessagesInDiskStore()));
			writer.writeEndElement(); // close diskStoreSize tag
			if (scCacheModule.getCacheModuleName().equals(cacheModuleParam)) {
				writeCacheModuleDetails(writer, scCacheModule, request);
			}
			writer.writeEndElement(); // close cache module tag
		}
	}

	/**
	 * Write cache loading.
	 * 
	 * @param writer
	 *            the writer
	 * @param cache
	 *            the cache
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheLoading(XMLStreamWriter writer, org.serviceconnector.cache.SCCache cache) throws XMLStreamException {
		HashMap<String, String> loadingSessionIds = cache.getLoadingSessionIds();
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
	 * Write cache module details.
	 * 
	 * @param writer
	 *            the writer
	 * @param cacheModule
	 *            the cache module
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private void writeCacheModuleDetails(XMLStreamWriter writer, ISCCacheModule<?> cacheModule, IWebRequest request)
			throws Exception {
		writer.writeStartElement("details");
		List<String> cacheKeys = cacheModule.getKeyList();
		// sort cacheKeys
		if (cacheModule.getCacheModuleName().equals(SC_CACHE_MODULE_TYPE.DATA_CACHE_MODULE.name())) {
			Collections.sort(cacheKeys, new CacheIdComparator());
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
			if (cacheModule.getCacheModuleName().equals(SC_CACHE_MODULE_TYPE.META_DATA_CACHE_MODULE.name())) {
				SCCacheMetaEntry metaEntry = (SCCacheMetaEntry) cacheModule.get(key);
				if (metaEntry == null && simulation > 0) {
					metaEntry = new SCCacheMetaEntry("");
				}
				if (metaEntry != null) {
					writeCacheMetaEntry(writer, cacheModule, key, metaEntry, request);
				}
			} else {
				SCMPMessage cachedMessage = (SCMPMessage) cacheModule.get(key);
				if (cachedMessage == null && simulation > 0) {
					cachedMessage = new SCMPMessage(SCMPVersion.CURRENT);
					cachedMessage.setBody("");
				}
				if (cachedMessage != null) {
					writeCacheMessage(writer, cacheModule, key, cachedMessage, request);
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
	 * @param cacheModule
	 *            the cache module
	 * @param cacheKey
	 *            the cache key
	 * @param metaEntry
	 *            the cache composite
	 * @param request
	 *            the request
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void writeCacheMetaEntry(XMLStreamWriter writer, ISCCacheModule<?> cacheModule, String cacheKey,
			SCCacheMetaEntry metaEntry, IWebRequest request) throws Exception {
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
		writer.writeStartElement("expiration");
		Date expireDate = cacheModule.getExpirationTime(cacheKey);
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(expireDate));
		writer.writeEndElement(); // end of expirationTimeout
		writer.writeStartElement("creation");
		Date creationDate = cacheModule.getCreationTime(cacheKey);
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(creationDate));
		writer.writeEndElement(); // end of creation
		writer.writeStartElement("lastAccess");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(metaEntry.getLastModifiedTime()));
		writer.writeEndElement(); // end of lastAccess
		writer.writeStartElement("cacheGuardianName");
		writer.writeCharacters(metaEntry.getCacheGuardianName());
		writer.writeEndElement(); // end of cacheGuardianName
		writer.writeStartElement("nrOfAppendix");
		writer.writeCharacters(String.valueOf(metaEntry.getNrOfAppendix() + simulation));
		writer.writeEndElement(); // end of nrOfAppendix
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
	 * @param cacheModule
	 *            the cache module
	 * @param cacheKey
	 *            the cache key
	 * @param cacheMessage
	 *            the cache message
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	private void writeCacheMessage(XMLStreamWriter writer, ISCCacheModule<?> cacheModule, String cacheKey,
			SCMPMessage cacheMessage, IWebRequest request) throws Exception {
		writer.writeStartElement("cacheMessage");
		writer.writeStartElement("key");
		writer.writeCharacters(cacheKey);
		writer.writeEndElement();
		writer.writeStartElement("state");
		writer.writeCharacters(SC_CACHE_ENTRY_STATE.LOADED.name());
		writer.writeEndElement(); // end of state
		writer.writeStartElement("expirationTimeout");
		Date expireDate = cacheModule.getExpirationTime(cacheKey);
		Date creationDate = cacheModule.getCreationTime(cacheKey);
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
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cacheModule.getLastAccessTime(cacheKey)));
		writer.writeEndElement(); // end of lastAccess
		writer.writeStartElement("header");
		Map<String, String> cacheMessageHeader = cacheMessage.getHeader();
		this.writeHeaderMap(writer, cacheMessageHeader);
		writer.writeEndElement(); // end of header
		writer.writeEndElement(); // end of cacheMessage
	}
}