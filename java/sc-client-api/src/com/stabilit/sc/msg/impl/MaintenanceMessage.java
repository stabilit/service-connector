package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class MaintenanceMessage extends Message {

	public static SCMPMsgType ID = SCMPMsgType.REQ_MAINTENANCE;

	public MaintenanceMessage() {
		super(ID);
	}
}
