package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPMsgType;

public class PublishMessage extends Message {
	
	private static final long serialVersionUID = -5461603317301105352L;
	
	public static SCMPMsgType ID = SCMPMsgType.PUBLISH;

	public PublishMessage() {
    	super(ID);
    }
}

