package com.stabilit.sc.msg;

import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1763291531850424661L;

	private String key;
	
	private Map<String, Object> attrMap;

	public Message() {
		this("");
	}
	
	public Message(String key) {
		this.key = key;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IMessage newInstance() {
		return new Message();
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Job [key=" + key + "]");
		for (String name : attrMap.keySet()) {
			sb.append(" ");
			sb.append(name);
			sb.append("=");
			sb.append(attrMap.get(name));
		}
		return sb.toString();
	}

	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}
}
