package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class GetDataMessage extends Message {

	private static final long serialVersionUID = -5461603317301105352L;

	public static String ID = "getData";
	private String serviceName;
	private String data;

	public GetDataMessage() {
		super(ID);
	}

	public GetDataMessage(String data) {
		super(ID);
		this.data = data;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
