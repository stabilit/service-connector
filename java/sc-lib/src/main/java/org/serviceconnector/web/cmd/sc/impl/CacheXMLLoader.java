/*
 * Copyright � 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */

package org.serviceconnector.web.cmd.sc.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheKey;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.cache.ICacheConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class CacheXMLLoader.
 */
public class CacheXMLLoader extends AbstractXMLLoader {

	/**
	 * Instantiates a new default xml loader.
	 */
	public CacheXMLLoader() {
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		writer.writeStartElement("cache");
		CacheManager cacheManager = AppContext.getCacheManager();
		ICacheConfiguration cacheConfiguration = cacheManager.getCacheConfiguration();
		this.writeCacheConfiguration(writer, cacheConfiguration);
		writer.writeEndElement(); // close cache tag
		writer.writeStartElement("caches");
		Object[] caches = cacheManager.getAllCaches();
		this.writeCaches(writer, caches, request);
		writer.writeEndElement(); // close caches tag
	}

	private void writeCacheConfiguration(XMLStreamWriter writer, ICacheConfiguration cacheConfiguration)
			throws XMLStreamException {
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

	private void writeCaches(XMLStreamWriter writer, Object[] caches, IWebRequest request) throws XMLStreamException {
		String cacheParam = request.getParameter("cache");
		for (Object obj : caches) {
			if (obj instanceof Cache == false) {
				continue;
			}
			Cache cache = (Cache) obj;
			writer.writeStartElement("cache");
			writer.writeStartElement("serviceName");
			writer.writeCharacters(cache.getServiceName());
			writer.writeEndElement(); // close serviceName tag
			writer.writeStartElement("compositeSize");
			writer.writeCharacters(String.valueOf(cache.getCompositeSize()));
			writer.writeEndElement(); // close compositeSize tag
			writer.writeStartElement("elementSize");
			writer.writeCharacters(String.valueOf(cache.getElementSize()));
			writer.writeEndElement(); // close elementSize tag
			writer.writeStartElement("memoryStoreSize");
			writer.writeCharacters(String.valueOf(cache.getMemoryStoreSize()));
			writer.writeEndElement(); // close memoryStoreSize tag
			writer.writeStartElement("diskStoreSize");
			writer.writeCharacters(String.valueOf(cache.getDiskStoreSize()));
			writer.writeEndElement(); // close diskStoreSize tag
			if (cache.getServiceName().equals(cacheParam)) {
				writeCacheDetails(writer, cache, request);
			}
			writer.writeEndElement(); // close cache tag
		}
	}

	private void writeCacheDetails(XMLStreamWriter writer, Cache cache, IWebRequest request) throws XMLStreamException {
		writer.writeStartElement("details");
		Object[] compositeKeys = cache.getCompositeKeys();
		if (compositeKeys == null) {
			writer.writeEndElement();
			return;
		}
		for (Object obj : compositeKeys) {
			CacheKey cacheKey = (CacheKey) obj;
			try {
				CacheComposite cacheComposite = cache.getComposite(cacheKey.getCacheId());
				if (cacheComposite != null) {
					writeCacheComposite(writer, cache, cacheKey, cacheComposite, request);
				}
			} catch (CacheException e) {
			}
		}
		writer.writeEndElement(); // end of details
	}

	private void writeCacheComposite(XMLStreamWriter writer, Cache cache, CacheKey cacheKey, CacheComposite cacheComposite,
			IWebRequest request) throws XMLStreamException {
		String compositeParam = request.getParameter("composite");
		writer.writeStartElement("composite");
		writer.writeStartElement("key");
		writer.writeCharacters(cacheKey.getCacheId());
		writer.writeEndElement();
		writer.writeStartElement("state");
		writer.writeCharacters(cacheComposite.getCacheState().toString());
		writer.writeEndElement(); // end of state
		writer.writeStartElement("size");
		writer.writeCharacters(String.valueOf(cacheComposite.getSize()));
		writer.writeEndElement(); // end of size
		writer.writeStartElement("loadingTimeout");
		writer.writeCharacters(String.valueOf(cacheComposite.getLoadingTimeout()));
		writer.writeEndElement(); // end of loadingTimeout
		writer.writeStartElement("expiration");
		if (cacheComposite.getExpiration() != null) {
			writer.writeCharacters(cacheComposite.getExpiration());
		}
		writer.writeEndElement(); // end of expiration
		writer.writeStartElement("creation");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cacheComposite.getCreationTime()));
		writer.writeEndElement(); // end of creation
		writer.writeStartElement("lastModified");
		writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cacheComposite.getLastModifiedTime()));
		writer.writeEndElement(); // end of lastModified
		if (compositeParam != null && compositeParam.equals(cacheKey.getCacheId())) {
			// get all messages
			CacheId cacheId = new CacheId(cacheKey.getCacheId());
			for (int i = 0; i < cacheComposite.getSize(); i++) {
				cacheId.setSequenceNr(String.valueOf(i + 1));
				writer.writeStartElement("message");
				try {
					CacheMessage cacheMessage = cache.getMessage(cacheId.getFullCacheId());
					writeCacheMessage(writer, cacheMessage);
				} catch (CacheException e) {
				}
				writer.writeEndElement();

			}
		}
		writer.writeEndElement(); // end of composite
	}

	/**
	 * Write cache message.
	 * 
	 * @param cacheMessage
	 *            the cache message
	 * @throws XMLStreamException
	 */
	private void writeCacheMessage(XMLStreamWriter writer, CacheMessage cacheMessage) throws XMLStreamException {
		if (cacheMessage == null) {
			return;
		}
		writer.writeStartElement("id");
		writer.writeCharacters(cacheMessage.getCacheId().getFullCacheId());
		writer.writeEndElement();
		writer.writeStartElement("sequenceNr");
		writer.writeCharacters(cacheMessage.getCacheId().getSequenceNr());
		writer.writeEndElement();
		writer.writeStartElement("messageType");
		writer.writeCharacters(cacheMessage.getMessageType());
		writer.writeEndElement();
		writer.writeStartElement("compressed");
		writer.writeCharacters(String.valueOf(cacheMessage.isCompressed()));
		writer.writeEndElement();
		Object body = cacheMessage.getBody();
		if (body != null && body instanceof byte[]) {
			writer.writeStartElement("bodyLength");
			writer.writeCharacters(String.valueOf(((byte[]) body).length));
			writer.writeEndElement();
		}
		if (body != null && body instanceof String) {
			writer.writeStartElement("bodyLength");
			writer.writeCharacters(String.valueOf(((String) body).length()));
			writer.writeEndElement();
		}
	}

	@Override
	public IFactoryable newInstance() {
		return new CacheXMLLoader();
	}
}