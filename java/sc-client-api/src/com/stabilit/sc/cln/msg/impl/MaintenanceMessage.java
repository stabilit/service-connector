package com.stabilit.sc.cln.msg.impl;

import com.stabilit.sc.common.io.Message;
import com.stabilit.sc.common.io.SCMPMsgType;

public class MaintenanceMessage extends Message {

	public static SCMPMsgType ID = SCMPMsgType.REQ_MAINTENANCE;

	public MaintenanceMessage() {
		super(ID);
	}
}
