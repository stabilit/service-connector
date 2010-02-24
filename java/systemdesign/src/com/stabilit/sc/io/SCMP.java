package com.stabilit.sc.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.msg.IMessage;

public class SCMP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2547798618820848999L;

	public static final String SUBSCRIBE_ID = "com.stabilit.sc.SUBSCRIBE_ID";
	
	public static final String MESSAGE_ID = "com.stabilit.sc.MESSAGE_ID";

	public static final String INDEX = "com.stabilit.sc.INDEX"; 

	private Map<String, String> metaMap;
	private Object body;
	private IEncoderDecoder encoderDecoder = null;

	public SCMP() {
		metaMap = new HashMap<String, String>();
	}

	public SCMP(Object body) {
		metaMap = new HashMap<String, String>();
		this.setBody(body);
	}

	public void setHeader(String name, String value) {
		metaMap.put(name, value);
	}

	public String getHeader(String name) {
		return metaMap.get(name);
	}

	public String getSessionId() {
		return metaMap.get(ISession.SESSION_ID);
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			return;
		}
		metaMap.put(ISession.SESSION_ID, sessionId);
	}

	public String getSubscribeId() {
		return metaMap.get(SUBSCRIBE_ID);		
	}
	
	public void setSubsribeId(String subscribeId) {
		if (subscribeId == null) {
			return;
		}
		metaMap.put(SUBSCRIBE_ID, subscribeId);
	}

	public String getMessageId() {
		return metaMap.get(MESSAGE_ID);		
	}
	
	public void setMessageId(String messageId) {
		if (messageId == null) {
			return;
		}
		metaMap.put(MESSAGE_ID, messageId);
	}

	public Map<String, String> getMetaMap() {
		return metaMap;
	}

	public void setMetaMap(Map<String, String> metaMap) {
		this.metaMap = metaMap;
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
		builder.append("SCOP [metaMap=");
		builder.append(metaMap);
		builder.append("]");
		return builder.toString();
	}

}
