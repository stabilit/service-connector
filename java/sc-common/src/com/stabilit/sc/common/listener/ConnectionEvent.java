package com.stabilit.sc.common.listener;

import java.util.EventObject;

public class ConnectionEvent extends EventObject {
	private Object data;
	
	public ConnectionEvent(Object source, Object data) {
		super(source);
	    this.data = data;
	}
	
	public Object getData() {
		return data;
	}
}
