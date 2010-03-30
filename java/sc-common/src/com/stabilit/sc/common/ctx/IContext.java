package com.stabilit.sc.common.ctx;

public interface IContext {

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);

}
