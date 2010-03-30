package com.stabilit.sc.common.io;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class SCMP implements Serializable {

	private static final long serialVersionUID = -3464445251398033295L;

	public static final String VERSION = "1.0-00";

	protected Map<String, String> header;
	private Object body;
	private IEncoderDecoder encoderDecoder = null;

	public SCMP() {
		header = new HashMap<String, String>();
	}

	public SCMP(Object body) {
		header = new HashMap<String, String>();
		this.setBody(body);
	}
	
	public void setMessageType(String messageType) {
		setHeader(SCMPHeaderType.MSG_TYPE.getName(), messageType);
	}
	
	public String getMessageType() {
		return getHeader(SCMPHeaderType.MSG_TYPE.getName());
	}

	public boolean isFault() {
		return false;
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

	public String getSessionId() {
		return header.get(SCMPHeaderType.SESSION_ID.getName());
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		header.put(SCMPHeaderType.SESSION_ID.getName(), sessionId);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMP [header=");
		builder.append(header);
		builder.append("]");
		return builder.toString();
	}
}
