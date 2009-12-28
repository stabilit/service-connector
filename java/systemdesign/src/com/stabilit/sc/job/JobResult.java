package com.stabilit.sc.job;

import java.util.HashMap;
import java.util.Map;

public class JobResult implements IJobResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8257194290211724153L;

	private IJob job;
	
	private Map<String, Object> attrMap;

	public JobResult(IJob job) {
		this.job = job;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IJob getJob() {
		return job;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("JobResult [job key=" + job.getKey() + "]");
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
	
	@Override
	public Object getReturn() {
		return this.attrMap.get("return");
	}
	
	@Override
	public void setReturn(Object value) {
        this.attrMap.put("return", value);		
	}
}
