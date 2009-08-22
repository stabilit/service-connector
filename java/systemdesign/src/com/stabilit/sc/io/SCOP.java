package com.stabilit.sc.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SCOP implements Serializable {

	private static final long serialVersionUID = 2211653041443557380L;
	private Map<String, String> metaMap;
	private Object body;

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

	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		stream.writeObject(metaMap);
		stream.writeObject(body);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		this.metaMap = (Map<String, String>) stream.readObject();
		this.body = stream.readObject();
	}
}
