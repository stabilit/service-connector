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
package org.serviceconnector.scmp;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.SCVersion;

/**
 * Service Connector Message Protocol. Data container for one message.
 * 
 * @author JTraber
 */
public class SCMPMessage {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(SCMPMessage.class);

	/** The Constant SCMP_VERSION. */
	public static final SCMPVersion SCMP_VERSION = SCMPVersion.CURRENT;
	/** The actual SC_VERSION. */
	public static final SCVersion SC_VERSION = SCVersion.CURRENT;
	/** The is reply. */
	private boolean isReply;
	/** The message header. */
	protected Map<String, String> header;
	/** The internal status. */
	private SCMPInternalStatus internalStatus; // internal usage only
	/** The message body. */
	private Object body;

	/**
	 * Instantiates a new SCMP.
	 */
	public SCMPMessage() {
		this.internalStatus = SCMPInternalStatus.NONE;
		this.header = new HashMap<String, String>();
		this.isReply = false;
	}

	/**
	 * Instantiates a new SCMP message.
	 * 
	 * @param messageBody
	 *            the message body
	 */
	public SCMPMessage(Object messageBody) {
		this.header = new HashMap<String, String>();
		this.setBody(messageBody);
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
	 * Gets the message sequence nr.
	 * 
	 * @return the message sequence nr
	 */
	public String getMessageSequenceNr() {
		return this.getHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR);
	}

	/**
	 * Gets the cache id.
	 * 
	 * @return the cache id
	 */
	public String getCacheId() {
		return this.getHeader(SCMPHeaderAttributeKey.CACHE_ID);
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		if (cacheId == null) {
			return;
		}
		this.setHeader(SCMPHeaderAttributeKey.CACHE_ID, cacheId);
	}

	/**
	 * Checks if the message is a fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		// this is the default value!
		return false;
	}

	/**
	 * Checks if the message is a part.
	 * 
	 * @return true, if is part
	 */
	public boolean isPart() {
		// this is the default value!
		return false;
	}

	/**
	 * Checks if the message is a poll request in large message sequence.
	 * 
	 * @return true, if is poll
	 */
	public boolean isPollRequest() {
		// this is the default value!
		return false;
	}

	/**
	 * Checks if is keep alive.
	 * 
	 * @return true, if is keep alive
	 */
	public boolean isKeepAlive() {
		return false;
	}

	/**
	 * Is true if there is an offset to use when accessing body data.
	 * 
	 * @return true, if there is body offset
	 */
	public boolean isBodyOffset() {
		// this is the default value!
		return false;
	}

	/**
	 * Gets the body offset.
	 * 
	 * @return the body offset
	 */
	public int getBodyOffset() {
		// this is the default value!
		return 0;
	}

	/**
	 * Checks if the message is a composite.
	 * 
	 * @return true, if is composite
	 */
	public boolean isComposite() {
		// this is the default value!
		return false;
	}

	/**
	 * Checks if is part of a group call.
	 * 
	 * @return true, if is group call
	 */
	public boolean isGroup() {
		return internalStatus == SCMPInternalStatus.GROUP;
	}

	/**
	 * Checks if the body is of type byte array.
	 * 
	 * @return true, if body is of type byte array
	 */
	public boolean isByteArray() {
		if (this.body == null) {
			return false;
		}
		return byte[].class == this.body.getClass();
	}

	/**
	 * Checks if the body is of type string.
	 * 
	 * @return true, if body is of type string
	 */
	public boolean isString() {
		if (this.body == null) {
			return false;
		}
		return String.class == this.body.getClass();
	}

	/**
	 * Checks if this is a large message.
	 * 
	 * @return true, if is a large message
	 */
	public boolean isLargeMessage() {
		// in case of composite message need to get body first
		if (this.getBody() == null) {
			return false;
		}
		int bodyLength = this.getBodyLength();
		return bodyLength > Constants.MAX_MESSAGE_SIZE;
	}

	public boolean isCompressed() {
		if (this.header.keySet().contains(SCMPHeaderAttributeKey.COMPRESSION.getValue())) {
			return true;
		}
		return false;
	}

	/**
	 * Removes the attribute with the specified name from the header.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeHeader(String name) {
		this.header.remove(name);
	}

	/**
	 * Removes the attribute with the specified type from the header.
	 * 
	 * @param headerType
	 *            the header type
	 */
	public void removeHeader(SCMPHeaderAttributeKey headerType) {
		this.header.remove(headerType.getValue());
	}

	/**
	 * Sets the header attribute by name and value.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @param attributeValue
	 *            the value of the attribute
	 */
	public void setHeader(String attributeName, String attributeValue) {
		this.header.put(attributeName, attributeValue);
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
	 * Sets the header attribute by type and value.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @param attributeValue
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerType, boolean attributeValue) {
		if (attributeValue) {
			this.header.put(headerType.getValue(), "1");
		} else {
			this.header.put(headerType.getValue(), "0");
		}
	}

	/**
	 * Sets the header by type and value.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @param attributeValue
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerType, int attributeValue) {
		this.header.put(headerType.getValue(), String.valueOf(attributeValue));
	}

	/**
	 * Sets the header by type and value.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @param attributeValue
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerType, long attributeValue) {
		this.header.put(headerType.getValue(), String.valueOf(attributeValue));
	}

	/**
	 * Copies the header from another message.
	 * 
	 * @param sourceMessage
	 *            the message with header header to be copied
	 */
	public void setHeader(SCMPMessage sourceMessage) {
		this.setHeader(sourceMessage.getHeader());
	}

	/**
	 * Copies a header attribute from another message.
	 * 
	 * @param sourceMessage
	 *            the source message from which header header should be copied
	 * @param headerType
	 *            the key to be copied. If the key does not exist in the source message, attribute is not set.
	 */
	public void setHeader(SCMPMessage sourceMessage, SCMPHeaderAttributeKey headerType) {
		String value = sourceMessage.getHeader(headerType);
		if (value == null) {
			return;
		}
		this.setHeader(headerType, value);
	}

	/**
	 * Returns the value of the header attribute.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @return the attribute value
	 */
	public String getHeader(String attributeName) {
		return this.header.get(attributeName);
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
	 * Returns the boolean value of the header attribute. Be careful if header field is not set - null is returned and if you unbox
	 * return value automatically into boolean than a NullPointerException will be thrown.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @return the boolean attribute value
	 */
	public Boolean getHeaderBoolean(SCMPHeaderAttributeKey headerType) {
		String value = this.header.get(headerType.getValue());

		if ("0".equals(value)) {
			return false;
		}
		if ("1".equals(value)) {
			return true;
		}
		return null;
	}

	/**
	 * Gets the header flag. Gets a header flag if header contains header key. Value totally irrelevant in this case.
	 * 
	 * @param headerKey
	 *            the header key
	 * @return the header flag
	 */
	public boolean getHeaderFlag(SCMPHeaderAttributeKey headerKey) {
		if (this.header.containsKey(headerKey.getValue())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the integer value of the header attribute.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @return the integer attribute value
	 */
	public Integer getHeaderInt(SCMPHeaderAttributeKey headerType) {
		String value = this.header.get(headerType.getValue());
		if (value == null) {
			return null;
		}
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(value);
		} catch (Exception ex) {
			logger.warn("getHeaderInt " + ex.toString());
			return null;
		}
		return intValue;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId() {
		return this.getHeader(SCMPHeaderAttributeKey.SESSION_ID);
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		this.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		this.header.put(SCMPHeaderAttributeKey.SESSION_ID.getValue(), sessionId);
	}

	/**
	 * Gets the whole header.
	 * 
	 * @return the whole header
	 */
	public Map<String, String> getHeader() {
		return this.header;
	}

	/**
	 * Sets the whole header.
	 * 
	 * @param header
	 *            the whole header
	 */
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	/**
	 * Sets the header flag. Sets a headerKey with value null.
	 * 
	 * @param headerKey
	 *            the new header flag
	 */
	public void setHeaderFlag(SCMPHeaderAttributeKey headerKey) {
		this.header.put(headerKey.getValue(), null);
	}

	/**
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setBody(Object body) {
		this.body = body;
		// set body type (bty) in header fields if body is of type TEXT
		SCMPBodyType bodyType = this.getBodyType();
		switch (bodyType) {
		case BINARY:
		case INPUT_STREAM:
		case UNDEFINED:
			return;
		case TEXT:
			this.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, SCMPBodyType.TEXT.getValue());
		default:
			return;
		}
	}

	/**
	 * Sets the body.
	 * 
	 * @param buffer
	 *            the buffer
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 */
	public void setBody(byte[] buffer, int offset, int length) {
		if (buffer == null) {
			return;
		}
		if (offset == 0 && buffer.length == length) {
			this.setBody(buffer);
			return;
		}
		byte[] temp = Arrays.copyOfRange(buffer, offset, length);
		this.setBody(temp);
	}

	/**
	 * Gets the body type.
	 * 
	 * @return the body type
	 */
	public SCMPBodyType getBodyType() {
		if (body == null) {
			return SCMPBodyType.UNDEFINED;
		}
		if (byte[].class == body.getClass()) {
			return SCMPBodyType.BINARY;
		}
		if (String.class == body.getClass()) {
			return SCMPBodyType.TEXT;
		}
		if (body instanceof InputStream) {
			return SCMPBodyType.INPUT_STREAM;
		}
		return SCMPBodyType.UNDEFINED;
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
	 * Gets the body length.
	 * 
	 * @return the body length
	 */
	public int getBodyLength() {
		// gets body in case of composite component
		Object body = this.getBody();
		if (body == null) {
			return 0;
		}
		if (byte[].class == body.getClass()) {
			return ((byte[]) body).length;
		}
		if (String.class == body.getClass()) {
			return ((String) body).length();
		}
		if (body instanceof InputStream) {
			/*
			 * needs to be different in case of INPUT_STREAM body length is always unknown for streams. Set it on Integer.MAX_VALUE
			 * 2^31-1 (2048 MB). Never rely on bodyLength for body type INPUT_STREAM.
			 */
			return Integer.MAX_VALUE;
		}
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMP [header=");
		builder.append(this.header);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Gets the internal status.
	 * 
	 * @return the internal status
	 */
	public SCMPInternalStatus getInternalStatus() {
		return internalStatus;
	}

	/**
	 * Sets the internal status.
	 * 
	 * @param internalStatus
	 *            the new internal status
	 */
	public void setInternalStatus(SCMPInternalStatus internalStatus) {
		this.internalStatus = internalStatus;
	}

	/**
	 * Checks if is request. Marks if this SCMP is a complete or completing part of a request. Last part SCMP of a request returns
	 * true.
	 * 
	 * @return true, if is request
	 */
	public boolean isRequest() {
		return internalStatus == SCMPInternalStatus.REQ;
	}

	/**
	 * Checks if this message is a reply.
	 * 
	 * @return true, if is reply
	 */
	public boolean isReply() {
		return isReply;
	}

	/**
	 * Sets the the reply flag in the message.
	 * 
	 * @param isReply
	 *            the reply flag value
	 */
	public void setIsReply(boolean isReply) {
		this.isReply = isReply;
	}
}
