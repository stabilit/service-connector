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
package org.serviceconnector.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class CacheMessage is the wrapper class for each message stored in the cache.
 * 
 * Each instance has a header map and a body object. All attributes MUST be serializable.
 */

public class CacheMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6754331155322189585L;

	/** The header attribute map. */
	private Map<String, String> header;

	/** The cache id identifying this instance. */
	private CacheId cacheId;

	/** The body, which must be serializable. */
	private Object body;

	/**
	 * Instantiates a new cache message.
	 * 
	 * The message sequence nr is scmp specific and has no linkage to the cache sequence nr.
	 * 
	 * @param messageSequenceNr
	 *            the message sequence nr
	 * @param body
	 *            the body
	 */
	public CacheMessage(String messageSequenceNr, Object body) {
		this.body = body;
		this.cacheId = null;
		this.header = new HashMap<String, String>();
		this.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, messageSequenceNr);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(SCMPMsgType messageType) {
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageType.getValue());
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageTypeValue
	 *            the new message type
	 */
	public void setMessageType(String messageTypeValue) {
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageTypeValue);
	}

	/**
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	public String getMessageType() {
		return this.getHeader(SCMPHeaderAttributeKey.MSG_TYPE);
	}

	/**
	 * Sets the compressed.
	 * 
	 * @param compressed
	 *            the new compressed
	 */
	public void setCompressed(boolean compressed) {
		this.setHeader(SCMPHeaderAttributeKey.COMPRESSION, String.valueOf(compressed));
	}

	/**
	 * Checks if is compressed.
	 * 
	 * @return true, if is compressed
	 */
	public boolean isCompressed() {
		String compressed = this.getHeader(SCMPHeaderAttributeKey.COMPRESSION);
		return "true".equals(compressed);
	}

	/**
	 * Returns the value of the header attribute.
	 * 
	 * @param headerType
	 *            the header type
	 * @return the attribute value
	 */
	public String getHeader(SCMPHeaderAttributeKey headerType) {
		return this.header.get(headerType.getValue());
	}

	/**
	 * Sets the header attribute by type and value.
	 * 
	 * @param headerType
	 *            the header type
	 * @param attributeValue
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerType, String attributeValue) {
		this.header.put(headerType.getValue(), attributeValue);
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(CacheId cacheId) {
		this.cacheId = cacheId;
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		this.cacheId = new CacheId(cacheId);
	}

	/**
	 * Gets the cache id.
	 * 
	 * @return the cache id
	 */
	public CacheId getCacheId() {
		return cacheId;
	}

	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setBody(Object body) {
		this.body = body;
	}

}
