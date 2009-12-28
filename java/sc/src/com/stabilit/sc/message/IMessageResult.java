package com.stabilit.sc.message;

import java.io.Serializable;

public interface IMessageResult extends Serializable {
	public IMessage getMessage();

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);
}
