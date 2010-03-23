package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class SubscribeMessage extends Message {

	private static final long serialVersionUID = 3024442630559924028L;
	public static SCMPMsgType ID = SCMPMsgType.REQ_SUBSCRIBE;

	public SubscribeMessage() {
		super(ID);
	}

}
