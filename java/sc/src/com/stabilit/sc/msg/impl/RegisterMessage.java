package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class RegisterMessage extends Message {

	private static final long serialVersionUID = 7873008149843069401L;
	public static String ID = "register";
	private String serviceName;	
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public RegisterMessage() {
		super("register");
	}
}
