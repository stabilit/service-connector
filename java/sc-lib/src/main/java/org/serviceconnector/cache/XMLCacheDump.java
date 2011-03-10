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
package org.serviceconnector.cache;

import javax.xml.stream.XMLStreamWriter;

/**
 * The Class XMLCacheDump.
 */
public class XMLCacheDump {

	/** The writer. */
	private XMLStreamWriter writer;

	/**
	 * Instantiates a new XML cache dump.
	 * 
	 * @param writer
	 *            the xml writer
	 * @throws Exception
	 *             the exception
	 */
	public XMLCacheDump(XMLStreamWriter writer) throws Exception {
		this.writer = writer;
	}

	/**
	 * Dump all cache instances into xml stream.
	 * 
	 * @param cacheManager
	 *            the cache manager
	 * @throws Exception
	 *             the exception
	 */
	public void dumpAll(CacheManager cacheManager) throws Exception {
		writer.writeStartElement("cache-manager");
		writer.writeAttribute("enabled", String.valueOf(cacheManager.isCacheEnabled()));
		writer.writeAttribute("diskPath", cacheManager.getCacheConfiguration().getDiskPath());
		writer.writeAttribute("maxElementsInMemory", String.valueOf(cacheManager.getCacheConfiguration().getMaxElementsInMemory()));
		writer.writeAttribute("maxElementsOnDisk", String.valueOf(cacheManager.getCacheConfiguration().getMaxElementsOnDisk()));
		writer.writeStartElement("cache-list");
		Cache[] caches = cacheManager.getAllCaches();
		if (caches == null) {
			writer.writeAttribute("information", "no caches found");
		} else {
			for (Cache cache : caches) {
				dumpCache(cache);
			}
		}
		writer.writeEndElement(); // end of cache-list
		writer.writeEndElement(); // end of cache-manager
	}

	/**
	 * Dump the cache into the xml writer.
	 * 
	 * @param cache
	 *            the cache
	 * @throws Exception
	 *             the exception
	 */
	private void dumpCache(Cache cache) throws Exception {
		writer.writeStartElement("cache");
		writer.writeAttribute("service", cache.getServiceName());
		writer.writeAttribute("name", cache.getCacheName());
		writer.writeAttribute("elementSize", String.valueOf(cache.getElementSize()));
		writer.writeAttribute("diskStoreSize", String.valueOf(cache.getDiskStoreSize()));
		writer.writeAttribute("memoryStoreSize", String.valueOf(cache.getMemoryStoreSize()));
		Object[] compositeKeys = cache.getCompositeKeys();
		if (compositeKeys != null) {
			writer.writeStartElement("messages");
			for (Object key : compositeKeys) {
				CacheKey cacheKey = (CacheKey) key;
				this.dumpMessage(cache, cacheKey);
			}
			writer.writeEndElement(); // end of messages
		}
		writer.writeEndElement(); // end of cache
	}

	/**
	 * Dump the composite into the xml writer.
	 * 
	 * @param cache
	 *            the cache
	 * @param cacheKey
	 *            the cache key
	 * @throws Exception
	 *             the exception
	 */
	private void dumpMessage(Cache cache, CacheKey cacheKey) throws Exception {
		writer.writeStartElement("message");
		String cacheId = cacheKey.getCacheId();
		writer.writeAttribute("cacheId", cacheId);
		CacheComposite cacheComposite = cache.getComposite(cacheId);
		if (cacheComposite == null) {
			writer.writeAttribute("exception", "invalid cacheId (not found) but stored in cache registry.");
		} else {
			this.writeElement("state", cacheComposite.getCacheState().toString());
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
			// dump all messages
			writer.writeStartElement("message-parts");
			CacheId localCacheId = new CacheId(cacheId);
			for (int i = 1; i <= size; i++) {
				localCacheId.setSequenceNr(String.valueOf(i));
				CacheMessage cacheMessage = cache.getMessage(localCacheId);
				writer.writeStartElement("part");
				this.writeElement("cacheId", localCacheId.getFullCacheId());
				if (cacheMessage == null) {
					this.writeElement("exception", "cache message " + localCacheId.getFullCacheId() + " does not exists.");
				} else {
					this.writeElement("messageType", cacheMessage.getMessageType());
					this.writeElement("isCompressed", cacheMessage.isCompressed());
				}
				writer.writeEndElement(); // end of part
			}
			writer.writeEndElement(); // end of message-parts
		}
		writer.writeEndElement(); // end of message
	}

	/**
	 * Write element.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	private void writeElement(String name, String value) throws Exception {
		writer.writeStartElement(name);
		if (value != null) {
			writer.writeCData(value);
		}
		writer.writeEndElement();
	}

	/**
	 * Write element converting int value to String.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	private void writeElement(String name, int value) throws Exception {
		writer.writeStartElement(name);
		writer.writeCData(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write element converting long value to String.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	private void writeElement(String name, long value) throws Exception {
		writer.writeStartElement(name);
		writer.writeCData(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write element converting boolean value to String.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	private void writeElement(String name, boolean value) throws Exception {
		writer.writeStartElement(name);
		writer.writeCData(String.valueOf(value));
		writer.writeEndElement();
	}
}
