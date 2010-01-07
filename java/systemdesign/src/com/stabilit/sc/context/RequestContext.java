package com.stabilit.sc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext implements IRequestContext {

	private Map<String, Object> attrMap;

	public RequestContext() {
		this.attrMap = new ConcurrentHashMap<String, Object>();
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
