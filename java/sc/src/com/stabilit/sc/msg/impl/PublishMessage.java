package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class PublishMessage extends Message {
	
	private static final long serialVersionUID = -5461603317301105352L;
	
	public static String ID = "publish";
	private String msg;

	public PublishMessage() {
    	super(ID);
    }
}

