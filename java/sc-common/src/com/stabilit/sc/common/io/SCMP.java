package com.stabilit.sc.common.io;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SCMP implements Serializable {

	private static final long serialVersionUID = -3464445251398033295L;

	public static final String SCMP_VERSION = "1.0";
	//TODO implementation version where?
	public static final String SC_VERSION = "1.0-00";
	public static final int LARGE_MESSAGE_LIMIT = 1;

	protected Map<String, String> header;
	protected Object body;

	public SCMP() {
		header = new HashMap<String, String>();
	}

	public SCMP(Object body) {
		header = new HashMap<String, String>();
		this.setBody(body);
	}

	public void setMessageType(String messageType) {
		setHeader(SCMPHeaderAttributeType.MSG_TYPE.getName(), messageType);
	}

	public String getMessageType() {
		return getHeader(SCMPHeaderAttributeType.MSG_TYPE.getName());
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

	public void setHeader(String name, String value) {
		header.put(name, value);
	}

	public void setHeader(String name, boolean value) {
		if (value) {
			header.put(name, "1");
		} else {
			header.put(name, "0");
		}
	}

	public void setHeader(String name, int value) {
		header.put(name, String.valueOf(value));
	}

	public String getHeader(String name) {
		return header.get(name);
	}

	public Boolean getHeaderBoolean(String name) {
		String value = header.get(name);

		if ("0".equals(value)) {
			return false;
		}
		if ("1".equals(value)) {
			return true;
		}
		return null;
	}

	public Integer getHeaderInt(String name) {
		String value = header.get(name);
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
		return header.get(SCMPHeaderAttributeType.SESSION_ID.getName());
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		header.put(SCMPHeaderAttributeType.SESSION_ID.getName(), sessionId);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public int getBodyLength() {
		if (body == null) {
			return 0;
		}
		if (String.class == body.getClass()) {
			return ((String)body).length();
		}		
		if (byte[].class == body.getClass()) {
			return ((byte[])body).length;
		}
		if(File.class == body.getClass()) {
			return (int) ((File)body).length();
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

	public boolean isLargeMessage() {
		if (this.body == null) {
			return false;
		}
		int bodyLength = this.getBodyLength();
		return bodyLength > LARGE_MESSAGE_LIMIT;
	}

}
