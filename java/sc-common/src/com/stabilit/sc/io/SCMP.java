package com.stabilit.sc.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.msg.IMessage;

public class SCMP implements Serializable {

	private static final long serialVersionUID = 2547798618820848999L;

	public static final String SUBSCRIBE_ID = "com.stabilit.sc.SUBSCRIBE_ID";
	
	public static final String MESSAGE_ID = "com.stabilit.sc.MESSAGE_ID";

	public static final String INDEX = "com.stabilit.sc.INDEX"; 

	private Map<String, String> header;
	private Object body;
	private IEncoderDecoder encoderDecoder = null;

	public SCMP() {
		header = new HashMap<String, String>();
	}

	public SCMP(Object body) {
		header = new HashMap<String, String>();
		this.setBody(body);
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

	public String getSubscribeId() {
		return header.get(SUBSCRIBE_ID);		
	}
	
	public void setSubsribeId(String subscribeId) {
		if (subscribeId == null) {
			return;
		}
		header.put(SUBSCRIBE_ID, subscribeId);
	}

	public String getMessageId() {
		return header.get(MESSAGE_ID);		
	}
	
	public void setMessageId(String messageId) {
		if (messageId == null) {
			return;
		}
		header.put(MESSAGE_ID, messageId);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void setBody(Object body) {
		this.body = body;
		if (this.body instanceof IMessage) {
			this.setMessageId(((IMessage)this.body).getKey());
		}
	}

	public Object getBody() {
		return body;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		if (this.encoderDecoder == null) {
			// get default encoder decoder
			this.encoderDecoder = EncoderDecoderFactory.newInstance();
		}
		this.encoderDecoder.encode(stream, this);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		if (this.encoderDecoder == null) {
			// get default encoder decoder
			this.encoderDecoder = EncoderDecoderFactory.newInstance();
		}
		this.encoderDecoder.decode(stream, this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCOP [header=");
		builder.append(header);
		builder.append("]");
		return builder.toString();
	}
}
