package com.stabilit.sc.message.impl;

import com.stabilit.sc.message.Message;

public class EchoMessage extends Message {

	private static final long serialVersionUID = 3998019689531196867L;

	public EchoMessage() {
    	super("echo");
    }
}
