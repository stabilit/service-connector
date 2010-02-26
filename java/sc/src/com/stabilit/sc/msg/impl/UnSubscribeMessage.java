package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class UnSubscribeMessage extends Message {

	private static final long serialVersionUID = 2012750566022077421L;
	
	public static String ID = "unsubscribe";

	public UnSubscribeMessage() {
    	super(ID);
    }
	
}
