/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
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
package org.serviceconnector.cache;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.SCVersion;
import org.serviceconnector.util.DateTimeUtility;

public class XMLCacheDump {
	private XMLStreamWriter writer;

	public XMLCacheDump(OutputStream os) throws Exception {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		this.writer = factory.createXMLStreamWriter(os);
	}

	public void dumpAll(CacheManager cacheManager) throws Exception {
		this.writer.writeStartDocument();
		this.writer.writeStartElement("cache-manager-dump");
		this.writer.writeStartElement("head");
		writer.writeStartElement("meta");
		writer.writeAttribute("creation", DateTimeUtility.getCurrentTimeZoneMillis());
		// write sc version
		writer.writeEndElement(); // close meta tag
		writer.writeStartElement("meta");
		writer.writeAttribute("scversion", SCVersion.CURRENT.toString());
		writer.writeEndElement(); // close meta tag
		this.writer.writeEndElement(); // end of head
		Object[] caches = cacheManager.getAllCaches();
		if (caches == null) {
			this.writeElement("information", "no caches found");
			this.writer.writeEndElement(); // end of cache-manager-dump
			this.writer.writeEndDocument();
			this.writer.flush();
			return;
		}
		for (int i = 0; i < caches.length; i++) {
			for (Object obj : caches) {
				Cache cache = (Cache) obj;
				dumpCache(cache);
			}
		}
		this.writer.writeEndElement(); // end of cache-manager-dump
		this.writer.writeEndDocument();
		this.writer.flush();
	}

	private void dumpCache(Cache cache) throws Exception {
		this.writer.writeStartElement("cache-dump");
		this.writeElement("name", cache.getCacheName());
		this.writeElement("service", cache.getServiceName());
		this.writeElement("diskStoreSize", cache.getDiskStoreSize());
		this.writeElement("elementSize", cache.getElementSize());
		this.writeElement("memoryStoreSize", cache.getMemoryStoreSize());
		this.writeElement("elementSize", cache.getElementSize());
		Object[] compositeKeys = cache.getCompositeKeys();
		if (compositeKeys == null) {
			this.writeElement("information", "cache is empty, no composite keys found.");
			this.writer.writeEndElement(); // end of cache-dump
			return;
		}
		for (Object key : compositeKeys) {
			CacheKey cacheKey = (CacheKey) key;
			this.dumpComposite(cache, cacheKey);
		}
		this.writer.writeEndElement(); // end of cache-dump
	}

	private void dumpComposite(Cache cache, CacheKey cacheKey) throws Exception {
		this.writer.writeStartElement("composite-dump");
		String cacheId = cacheKey.getCacheId();
		this.writeElement("cacheId", cacheId);
		CacheComposite cacheComposite = cache.getComposite(cacheId);
		if (cacheComposite == null) {
			this.writeElement("exception", "invalid cache key (not found) but stored in cache registry.");
			this.writer.writeEndElement(); // end of composite-dump
			return;
		}
		this.writeElement("cacheState", cacheComposite.getCacheState().toString());
		this.writeElement("expiration", cacheComposite.getExpiration());
		this.writeElement("expirationTimestamp", cacheComposite.getExpirationTimestamp());
		this.writeElement("creationTime", cacheComposite.getCreationTime().toString());
		this.writeElement("lastModifiedTime", cacheComposite.getLastModifiedTime().toString());
		this.writeElement("lastModifiedTimeMillis", cacheComposite.getLastModifiedTimeMillis());
		this.writeElement("loadingTimeout", cacheComposite.getLoadingTimeout());
		int size = cacheComposite.getSize();
		this.writeElement("size", size);
		this.writeElement("isExpired", cacheComposite.isExpired());
		this.writeElement("isLoaded", cacheComposite.isLoaded());
		this.writeElement("isLoading", cacheComposite.isLoading());
		this.writeElement("isLoadingExpired", cacheComposite.isLoadingExpired());
		if ((size < 0) || (size > (1 << 16))) {
			this.writeElement("exception", "invalid cache composite size (" + size + ").");
		}
		// dump all messages
		CacheId localCacheId = new CacheId(cacheId);
		for (int i = 1; i <= size; i++) {
			localCacheId.setSequenceNr(String.valueOf(i));
			CacheMessage cacheMessage = cache.getMessage(localCacheId);
			this.writer.writeStartElement("cache-message");
			this.writeElement("cacheId", localCacheId.getFullCacheId());
			if (cacheMessage == null) {
				this.writeElement("exception", "cache message " + localCacheId.getFullCacheId() + " does not exists.");
				this.writer.writeEndElement(); // end of cache-message
				continue;
			}
			this.writeElement("messageType", cacheMessage.getMessageType());
			this.writeElement("isCompressed", cacheMessage.isCompressed());
			this.writeElement("isCompressed", cacheMessage.isCompressed());		
			this.writer.writeEndElement(); // end of cache-message
		}
		this.writer.writeEndElement(); // end of composite-dump

	}

	private void writeElement(String name, String value) throws Exception {
		this.writer.writeStartElement(name);
		if (value != null) {
		   this.writer.writeCData(value);
		}
		this.writer.writeEndElement();
	}

	private void writeElement(String name, int value) throws Exception {
		this.writer.writeStartElement(name);
		this.writer.writeCData(String.valueOf(value));
		this.writer.writeEndElement();
	}

	private void writeElement(String name, long value) throws Exception {
		this.writer.writeStartElement(name);
		this.writer.writeCData(String.valueOf(value));
		this.writer.writeEndElement();
	}

	private void writeElement(String name, boolean value) throws Exception {
		this.writer.writeStartElement(name);
		this.writer.writeCData(String.valueOf(value));
		this.writer.writeEndElement();
	}

}
