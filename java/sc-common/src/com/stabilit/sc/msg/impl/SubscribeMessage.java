package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.MsgType;

public class SubscribeMessage extends Message {

	private static final long serialVersionUID = 3024442630559924028L;
	public static MsgType ID = MsgType.SUBSCRIBE;

	public SubscribeMessage() {
		super(ID);
	}

}
