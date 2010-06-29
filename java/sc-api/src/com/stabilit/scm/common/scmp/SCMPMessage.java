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
package com.stabilit.scm.common.scmp;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.scm.common.SCVersion;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;

/**
 * Service Connector Message Protocol. Data container for one message.
 */
public class SCMPMessage {

	/** The Constant SCMP_VERSION. */
	public static final SCMPVersion SCMP_VERSION = SCMPVersion.ONE;
	/** The actual SC_VERSION. */
	public static final SCVersion SC_VERSION = SCVersion.ONE;
	/** The Constant LARGE_MESSAGE_LIMIT. */
	public static final int LARGE_MESSAGE_LIMIT = 60 << 10; // 64Kb
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
		header = new HashMap<String, String>();
		this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		isReply = false;
	}

	/**
	 * Instantiates a new SCMP message.
	 * 
	 * @param messageBody
	 *            the message body
	 */
	public SCMPMessage(Object messageBody) {
		header = new HashMap<String, String>();
		this.setBody(messageBody);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(String messageType) {
		setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageType);
	}

	/**
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	public String getMessageType() {
		return getHeader(SCMPHeaderAttributeKey.MSG_TYPE);
	}

	/**
	 * Checks if the message is a fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		return false; // this is the default value!
	}

	/**
	 * Checks if the message is a part.
	 * 
	 * @return true, if is part
	 */
	public boolean isPart() {
		return false; // this is the default value!
	}
	
	public boolean isKeepAlive() {
		return false;
	}

	/**
	 * Is true if there is an offset to use when accessing body data.
	 * 
	 * @return true, if there is body offset
	 */
	public boolean isBodyOffset() {
		return false; // this is the default value!
	}

	/**
	 * Gets the body offset.
	 * 
	 * @return the body offset
	 */
	public int getBodyOffset() {
		return 0; // this is the default value!
	}

	/**
	 * Checks if the message is a composite.
	 * 
	 * @return true, if is composite
	 */
	public boolean isComposite() {
		return false; // this is the default value!
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
		if (this.body == null) {
			return false;
		}
		if (this.body instanceof IInternalMessage) {
			return false;
		}
		int bodyLength = this.getBodyLength();
		return bodyLength > LARGE_MESSAGE_LIMIT;
	}

	/**
	 * Removes the attribute with the specified name from the header.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeHeader(String name) {
		header.remove(name);
	}

	/**
	 * Removes the attribute with the specified type from the header.
	 * 
	 * @param headerType
	 *            the header type
	 */
	public void removeHeader(SCMPHeaderAttributeKey headerType) {
		header.remove(headerType.getName());
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
		header.put(attributeName, attributeValue);
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
		header.put(headerType.getName(), attributeValue);
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
			header.put(headerType.getName(), "1");
		} else {
			header.put(headerType.getName(), "0");
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
		header.put(headerType.getName(), String.valueOf(attributeValue));
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
		return header.get(attributeName);
	}

	/**
	 * Returns the value of the header attribute.
	 * 
	 * @param headerType
	 *            the header type
	 * @return the attribute value
	 */
	public String getHeader(SCMPHeaderAttributeKey headerType) {
		return header.get(headerType.getName());
	}

	/**
	 * Returns the boolean value of the header attribute. Be careful if header field is not set - null is returned and
	 * if you unbox return value automatically into boolean than a NullPointerException will be thrown.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @return the boolean attribute value
	 */
	public Boolean getHeaderBoolean(SCMPHeaderAttributeKey headerType) {
		String value = header.get(headerType.getName());

		if ("0".equals(value)) {
			return false;
		}
		if ("1".equals(value)) {
			return true;
		}
		return null;
	}

	/**
	 * Returns the integer value of the header attribute.
	 * 
	 * @param headerType
	 *            the header attribute
	 * @return the int attribute value
	 */
	public Integer getHeaderInt(SCMPHeaderAttributeKey headerType) {
		String value = header.get(headerType.getName());
		if (value == null) {
			return null;
		}
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(value);
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(this, th);
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
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		header.put(SCMPHeaderAttributeKey.SESSION_ID.getName(), sessionId);
	}

	/**
	 * Gets the whole header.
	 * 
	 * @return the whole header
	 */
	public Map<String, String> getHeader() {
		return header;
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
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setBody(Object body) {
		this.body = body;
		this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		if (this.body == null) {
			this.removeHeader(SCMPHeaderAttributeKey.BODY_TYPE);
			return;
		}
		this.setHeader(SCMPHeaderAttributeKey.BODY_TYPE, this.getBodyTypeAsString());
	}

	/**
	 * Gets the body type as string.
	 * 
	 * @return the body type as string
	 */
	private String getBodyTypeAsString() {
		return getBodyType().getName();
	}

	/**
	 * Gets the body type.
	 * 
	 * @return the body type
	 */
	public SCMPBodyType getBodyType() {
		if (body == null) {
			return SCMPBodyType.undefined;
		}
		if (String.class == body.getClass()) {
			return SCMPBodyType.text;
		}
		if (byte[].class == body.getClass()) {
			return SCMPBodyType.binary;
		}
		if (body instanceof IInternalMessage) {
			return SCMPBodyType.internalMessage;
		}
		return SCMPBodyType.undefined;
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
		if (body == null) {
			return 0;
		}
		if (String.class == body.getClass()) {
			return ((String) body).length();
		}
		if (byte[].class == body.getClass()) {
			return ((byte[]) body).length;
		}
		if (body instanceof IInternalMessage) {
			return ((IInternalMessage) body).getLength();
		}
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMP [header=");
		builder.append(header);
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
	 * Checks if is request. Marks if this SCMP is a complete or completing part of a request. Last part SCMP of a
	 * request returns true.
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
