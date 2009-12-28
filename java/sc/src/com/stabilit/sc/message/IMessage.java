package com.stabilit.sc.message;

import java.io.Serializable;

public interface IMessage extends Serializable {
	public String getKey();
	
	public IMessage newInstance();

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);
}
