/*
 * QueueSample.java - Java type representing a snapshot of a given queue.
 * It bundles together the instant time the snapshot was taken, the queue
 * size and the queue head.
 */

package com.example.jmx.mxbean;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Queuesse {

	private Date date;
	private int size;
	private String head;
	private String test;
	protected Map<String, Object> attrMap;

	@ConstructorProperties( {})
	public Queuesse() {
		attrMap = new HashMap<String, Object>();
	}

	public Queuesse(Map<String, Object> map) {
		attrMap = map;
	}

	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	public Object removeAttribute(String name) {
		return this.attrMap.remove(name);
	}

	public String[] getAtts() {
		String[] values = new String[attrMap.size()];
		int i = 0;
		for (Object obj : attrMap.values()) {
			values[i] = obj.toString();
			i++;
		}
		return values;
	}

	@Override
	public String toString() {
		String string = "";
		for (String key : attrMap.keySet()) {
			string += key + "=" + attrMap.get(key) + ";";
		}
		return string;
	}

	public Queuesse(Date date, int size, String head, Map<String, Object> attrMap) {
		this.date = date;
		this.size = size;
		this.head = head;
		this.attrMap = attrMap;
	}

	public Date getDate() {
		return date;
	}

	public int getSize() {
		return size;
	}

	public String getHead() {
		return head;
	}

	public String getTest() {
		return test;
	}

}
