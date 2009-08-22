package com.stabilit.sc.job;

import java.io.Serializable;

public interface IJob extends Serializable {
	public String getKey();
	
	public IJob newInstance();

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);
}
