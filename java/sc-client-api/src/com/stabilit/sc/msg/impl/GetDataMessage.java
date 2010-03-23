package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class GetDataMessage extends Message {

	private static final long serialVersionUID = -5461603317301105352L;

	public static SCMPMsgType msgType = SCMPMsgType.REQ_CLN_DATA;

	public GetDataMessage() {
		super(msgType);
	}
}
