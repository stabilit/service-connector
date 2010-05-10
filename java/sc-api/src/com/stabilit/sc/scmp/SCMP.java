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
package com.stabilit.sc.scmp;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.listener.ExceptionListenerSupport;

/**
 * The Class SCMP. Service Connector Message Protocol. Data Container for one Message.
 */
public class SCMP {

	/** The Constant SCMP_VERSION. */
	public static final String SCMP_VERSION = "1.0";
	// TODO implementation version where?
	/** The Constant SC_VERSION. */
	public static final String SC_VERSION = "1.0-00";
	/** The Constant LARGE_MESSAGE_LIMIT. */
	public static final int LARGE_MESSAGE_LIMIT = 60 << 10; // 64Kb
	/** The is reply. */
	private boolean isReply;
	/** The header. */
	protected Map<String, String> header;
	/** The internal status. */
	private SCMPInternalStatus internalStatus; // internal usage only
	/** The body. */
	protected Object body;

	/**
	 * Instantiates a new SCMP.
	 */
	public SCMP() {
		this.internalStatus = SCMPInternalStatus.NONE;
		header = new HashMap<String, String>();
		isReply = false;
	}

	/**
	 * Instantiates a new sCMP.
	 * 
	 * @param body
	 *            the body
	 */
	public SCMP(Object body) {
		header = new HashMap<String, String>();
		this.setBody(body);
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
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		return false;
	}

	/**
	 * Checks if is part.
	 * 
	 * @return true, if is part
	 */
	public boolean isPart() {
		return false;
	}

	/**
	 * Is true if there is an offset to use when accessing body data.
	 * 
	 * @return true, if there is body offset
	 */
	public boolean isBodyOffset() {
		return false;
	}

	/**
	 * Gets the body offset.
	 * 
	 * @return the body offset
	 */
	public int getBodyOffset() {
		return 0;
	}

	/**
	 * Checks if is composite.
	 * 
	 * @return true, if is composite
	 */
	public boolean isComposite() {
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
	 * Checks if body is of type byte array.
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
	 * Checks if body is of type string.
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
	 * Checks if is a large message.
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
	 * Removes the header.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeHeader(String name) {
		header.remove(name);
	}

	/**
	 * Removes the header.
	 * 
	 * @param headerType
	 *            the header type
	 */
	public void removeHeader(SCMPHeaderAttributeKey headerType) {
		header.remove(headerType.getName());
	}

	/**
	 * Sets the header.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setHeader(String name, String value) {
		header.put(name, value);
	}

	/**
	 * Sets the header.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @param value
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerAttr, String value) {
		header.put(headerAttr.getName(), value);
	}

	/**
	 * Sets the header.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @param value
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerAttr, boolean value) {
		if (value) {
			header.put(headerAttr.getName(), "1");
		} else {
			header.put(headerAttr.getName(), "0");
		}
	}

	/**
	 * Sets the header.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @param value
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerAttr, int value) {
		header.put(headerAttr.getName(), String.valueOf(value));
	}

	/**
	 * Sets the header.
	 * 
	 * @param scmp
	 *            the new header
	 */
	public void setHeader(SCMP scmp) {
		this.setHeader(scmp.getHeader());
	}

	/**
	 * Sets the header.
	 * 
	 * @param scmp
	 *            the scmp
	 * @param key
	 *            the key
	 */
	public void setHeader(SCMP scmp, SCMPHeaderAttributeKey key) {
		String value = scmp.getHeader(key);
		if (value == null) {
			return;
		}
		this.setHeader(key, value);
	}

	/**
	 * Gets the header.
	 * 
	 * @param name
	 *            the name
	 * @return the header
	 */
	public String getHeader(String name) {
		return header.get(name);
	}

	/**
	 * Gets the header.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @return the header
	 */
	public String getHeader(SCMPHeaderAttributeKey headerAttr) {
		return header.get(headerAttr.getName());
	}

	/**
	 * Gets the header boolean.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @return the header boolean
	 */
	public Boolean getHeaderBoolean(SCMPHeaderAttributeKey headerAttr) {
		String value = header.get(headerAttr.getName());

		if ("0".equals(value)) {
			return false;
		}
		if ("1".equals(value)) {
			return true;
		}
		return null;
	}

	/**
	 * Gets the header int.
	 * 
	 * @param headerAttr
	 *            the header attr
	 * @return the header int
	 */
	public Integer getHeaderInt(SCMPHeaderAttributeKey headerAttr) {
		String value = header.get(headerAttr.getName());
		if (value == null)
			return null;
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(value);
		} catch (Throwable th) {
			ExceptionListenerSupport.getInstance().fireException(this, th);
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
		return header.get(SCMPHeaderAttributeKey.SESSION_ID.getName());
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
		if (this.body == null) {
			this.removeHeader(SCMPHeaderAttributeKey.BODY_LENGTH);
			this.removeHeader(SCMPHeaderAttributeKey.BODY_TYPE);
			return;
		}
		this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
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
			return SCMPBodyType.message;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
	 * Checks if is reply.
	 * 
	 * @return true, if is reply
	 */
	public boolean isReply() {
		return isReply;
	}

	/**
	 * Sets the checks if is reply.
	 * 
	 * @param isReply
	 *            the new checks if is reply
	 */
	public void setIsReply(boolean isReply) {
		this.isReply = isReply;
	}
}
