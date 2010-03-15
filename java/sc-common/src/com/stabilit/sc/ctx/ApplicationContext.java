package com.stabilit.sc.ctx;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements IApplicationContext {

	protected String[] args;
	protected Properties props;

	private Map<String, Object> attrMap;

	public ApplicationContext() {
		this.args = new String[0];
		this.attrMap = new ConcurrentHashMap<String, Object>();
	}

	public void setArgs(String[] args) throws Exception {
		this.args = args;
	}

	@Override
	public Object getAttribute(String name) {
		//TODO ... args oder props zulassen, eines löschen!
		if (props != null)
			return props.getProperty(name);
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	@Override
	public void setProps(Properties props) {
		this.props = props;
	}
}
