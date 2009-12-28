package com.stabilit.sc.message;

import java.util.HashMap;
import java.util.Map;

public class MessageResult implements IMessageResult {

	private static final long serialVersionUID = 8257194290211724153L;

	private IMessage msg;
	
	private Map<String, Object> attrMap;

	public MessageResult(IMessage msg) {
		this.msg = msg;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IMessage getMessage() {
		return msg;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MessageResult [message key=" + msg.getKey() + "]");
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
