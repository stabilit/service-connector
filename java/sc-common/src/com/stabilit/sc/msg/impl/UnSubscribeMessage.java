package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.MsgType;

public class UnSubscribeMessage extends Message {

	private static final long serialVersionUID = 2012750566022077421L;
	
	public static MsgType ID = MsgType.UNSUBSCRIBE;

	public UnSubscribeMessage() {
    	super(ID);
    }	
}
