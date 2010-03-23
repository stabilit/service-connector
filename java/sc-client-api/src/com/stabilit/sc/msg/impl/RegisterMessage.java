package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class RegisterMessage extends Message {

	private static final long serialVersionUID = 7873008149843069401L;
	public static SCMPMsgType ID = SCMPMsgType.REQ_REGISTER_SERVICE;
	
	public RegisterMessage() {
		super(ID);
	}
}
