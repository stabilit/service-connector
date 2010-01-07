package com.stabilit.sc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements IApplicationContext {

	protected String[] args;

	private Map<String, Object> attrMap;

	public ApplicationContext() {
		this.args = new String[0];
		this.attrMap = new ConcurrentHashMap<String, Object>();
	}
	
	@Override
	public String[] getArgs() {
		return args;
	}
	
    public void setArgs(String[] args) {
		this.args = args;
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
