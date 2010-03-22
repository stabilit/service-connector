package com.stabilit.sc.io;

import java.io.IOException;
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

	public String getHeader(String name) {
		return header.get(name);
	}

	public String getSessionId() {
		return header.get(ISession.SESSION_ID);
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		header.put(ISession.SESSION_ID, sessionId);
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

	private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		if (this.encoderDecoder == null) {
			// get default encoder decoder
			this.encoderDecoder = EncoderDecoderFactory.newInstance();
		}
		this.encoderDecoder.encode(stream, this);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		if (this.encoderDecoder == null) {
			// get default encoder decoder
			this.encoderDecoder = EncoderDecoderFactory.newInstance();
		}
		this.encoderDecoder.decode(stream, this);
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
