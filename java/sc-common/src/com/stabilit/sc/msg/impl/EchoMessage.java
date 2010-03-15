package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class EchoMessage extends Message {
	
	private static final long serialVersionUID = -5461603317301105352L;
	
	public static String ID = "echo";

	public EchoMessage() {
    	super(ID);
    }
}
