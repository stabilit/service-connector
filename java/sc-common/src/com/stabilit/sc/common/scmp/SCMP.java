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
package com.stabilit.sc.common.scmp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SCMP implements Serializable {

	private static final long serialVersionUID = -3464445251398033295L;

	public static final String SCMP_VERSION = "1.0";
	// TODO implementation version where?
	public static final String SC_VERSION = "1.0-00";
	public static final int LARGE_MESSAGE_LIMIT = 60 << 10;

	protected Map<String, String> header;
	protected SCMPInternalStatus internalStatus; // internal usage only
	protected Object body;

	public SCMP() {
		this.internalStatus = SCMPInternalStatus.NONE;
		header = new HashMap<String, String>();
	}

	public SCMP(Object body) {
		header = new HashMap<String, String>();
		this.setBody(body);
	}

	public void setMessageType(String messageType) {
		setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageType);
	}

	public String getMessageType() {
		return getHeader(SCMPHeaderAttributeKey.MSG_TYPE);
	}

	public boolean isFault() {
		return false;
	}

	public boolean isReply() {
		return false;
	}

	public boolean isPart() {
		return false;
	}

	public boolean isBodyOffset() {
		return false;
	}
	
	public int getBodyOffset() {
		return 0;
	}

	public boolean isComposite() {
		return false;
	}

	public boolean isByteArray() {
		if (this.body == null) {
			return false;
		}
		return byte[].class == this.body.getClass();
	}

	public boolean isString() {
		if (this.body == null) {
			return false;
		}
		return String.class == this.body.getClass();
	}

	public boolean isLargeMessage() {
		if (this.body == null) {
			return false;
		}
		if(this.body instanceof IInternalMessage) {
			return false;
		}
		int bodyLength = this.getBodyLength();
		return bodyLength > LARGE_MESSAGE_LIMIT;
	}

	public void removeHeader(String name) {
		header.remove(name);
	}

	public void removeHeader(SCMPHeaderAttributeKey headerType) {
		header.remove(headerType.getName());
	}

	 public void setHeader(String name, String value) {
		header.put(name, value);
	}

	public void setHeader(SCMPHeaderAttributeKey headerAttr, String value) {
		header.put(headerAttr.getName(), value);
	}

	public void setHeader(SCMPHeaderAttributeKey headerAttr, boolean value) {
		if (value) {
			header.put(headerAttr.getName(), "1");
		} else {
			header.put(headerAttr.getName(), "0");
		}
	}

	public void setHeader(SCMPHeaderAttributeKey headerAttr, int value) {
		header.put(headerAttr.getName(), String.valueOf(value));
	}

	public void setHeader(SCMP scmp) {
		this.setHeader(scmp.getHeader());
	}

	public void setHeader(SCMP scmp, SCMPHeaderAttributeKey key) {
		String value = scmp.getHeader(key);
		if (value == null) {
			return;
		}
		this.setHeader(key, value);
	}

	 public String getHeader(String name) {
		return header.get(name);
	}

	public String getHeader(SCMPHeaderAttributeKey headerAttr) {
		return header.get(headerAttr.getName());
	}

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

	public Integer getHeaderInt(SCMPHeaderAttributeKey headerAttr) {
		String value = header.get(headerAttr.getName());
		if (value == null)
			return null;
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(value);
		} catch (Throwable th) {
			return null;
		}
		return intValue;
	}

	public String getSessionId() {
		return header.get(SCMPHeaderAttributeKey.SESSION_ID.getName());
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		header.put(SCMPHeaderAttributeKey.SESSION_ID.getName(), sessionId);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

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

	private String getBodyTypeAsString() {
		return getBodyType().getName();
	}

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

	public Object getBody() {
		return body;
	}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMP [header=");
		builder.append(header);
		builder.append("]");
		return builder.toString();
	}

	public void setInternalStatus(SCMPInternalStatus internalStatus) {
		this.internalStatus = internalStatus;
	}

	public boolean isRequest() {
		return internalStatus == SCMPInternalStatus.REQ;
	}
}
