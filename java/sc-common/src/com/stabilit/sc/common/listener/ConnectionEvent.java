package com.stabilit.sc.common.listener;

import java.util.EventObject;

public class ConnectionEvent extends EventObject {
	private int offset;
	private int length;
	private Object data;

	public ConnectionEvent(Object source, Object data) {
		this(source, data, -1, -1);
	}

	public ConnectionEvent(Object source, Object data, int offset, int length) {
		super(source);
		this.offset = offset;
		this.length = length;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
}
