package com.stabilit.sc.context;

public interface IContext {

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);

}
