package com.stabilit.sc.io;

import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage {

	private static final long serialVersionUID = -1763291531850424661L;

	private SCMPMsgType key;
	
	protected Map<String, Object> attrMap;

	public Message() {
		this(SCMPMsgType.UNDEFINED);
	}
	
	public Message(SCMPMsgType key) {
		this.key = key;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IMessage newInstance() {
		return new Message();
	}
	
	@Override
	public SCMPMsgType getKey() {
		return key;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Msg [key=" + key + "]");
		for (String name : attrMap.keySet()) {
			sb.append(" ");
			sb.append(name);
			sb.append("=");
			sb.append(attrMap.get(name));
		}
		return sb.toString();
	}
	
	
	@Override
	public Map<String, Object> getAttributeMap() {
		return attrMap;
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
