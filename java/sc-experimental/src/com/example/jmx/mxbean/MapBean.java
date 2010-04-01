package com.example.jmx.mxbean;

import java.util.HashMap;
import java.util.Map;

public class MapBean<T> {

	protected Map<String, T> attrMap;

	public MapBean() {
		attrMap = new HashMap<String, T>();
	}

	public MapBean(Map<String, T> map) {
		attrMap = map;
	}

	public Map<String, T> getAttributeMap() {
		return this.attrMap;
	}

	protected void setAttributeMap(Map<String, T> attrMap) {
		this.attrMap = attrMap;
	}

	public T getAttribute(String name) {
		return this.attrMap.get(name);
	}

	public void setAttribute(String name, T value) {
		this.attrMap.put(name, value);
	}

	public T removeAttribute(String name) {
		return this.attrMap.remove(name);
	}
}
