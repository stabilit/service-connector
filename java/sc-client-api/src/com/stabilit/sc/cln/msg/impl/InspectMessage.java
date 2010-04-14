package com.stabilit.sc.cln.msg.impl;

import com.stabilit.sc.common.io.Message;
import com.stabilit.sc.common.io.SCMPMsgType;

public class InspectMessage extends Message {

	public static SCMPMsgType ID = SCMPMsgType.INSPECT;

	public InspectMessage() {
		super(ID);
	}
}
