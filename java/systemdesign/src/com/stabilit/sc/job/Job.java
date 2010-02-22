package com.stabilit.sc.job;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.job.impl.FileSystemJob;

public class Job implements IJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1763291531850424661L;

	private String key;
	
	private Map<String, Object> attrMap;

	public Job(String key) {
		this.key = key;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IJob newInstance() {
		return new FileSystemJob();
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
