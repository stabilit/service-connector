package com.stabilit.sc.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SCOP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2211653041443557380L;

	private Map<String, String> metaMap;
	private Object body;
	private IEncoderDecoder encoderDecoder = null;

	public SCOP() {
		metaMap = new HashMap<String, String>();
	}

	public SCOP(Object body) {
		metaMap = new HashMap<String, String>();
		this.body = body;
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

	public Map<String, String> getMetaMap() {
		return metaMap;
	}

	public void setMetaMap(Map<String, String> metaMap) {
		this.metaMap = metaMap;
	}

	public void setBody(Object body) {
		this.body = body;
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
