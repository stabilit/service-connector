package com.stabilit.sc.cln.msg.impl;

import com.stabilit.sc.common.io.Message;
import com.stabilit.sc.common.io.SCMPMsgType;

public class EchoMessage extends Message {
	
	private static final long serialVersionUID = -5461603317301105352L;
	
	public static SCMPMsgType ID = SCMPMsgType.ECHO_SRV;

	public EchoMessage() {
    	super(ID);
    }
}
