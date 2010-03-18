package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.MsgType;

public class RegisterMessage extends Message {

	private static final long serialVersionUID = 7873008149843069401L;
	public static MsgType ID = MsgType.REGISTER_SERVICE;
	
	public RegisterMessage() {
		super(ID);
	}
}
