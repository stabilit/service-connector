package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class UnSubscribeMessage extends Message {

	private static final long serialVersionUID = 2012750566022077421L;
	
	public static SCMPMsgType ID = SCMPMsgType.UNSUBSCRIBE;

	public UnSubscribeMessage() {
    	super(ID);
    }	
}
