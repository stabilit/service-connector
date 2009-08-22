package com.stabilit.sc.job;

import java.io.Serializable;

public interface IJobResult extends Serializable {
	public IJob getJob();

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);
}
