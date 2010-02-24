package com.stabilit.sc.job.impl;

import com.stabilit.sc.msg.Message;

public class AsyncCallMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3379060956300430418L;

	public static String ID = "asyncCall";
	
	public AsyncCallMessage() {
    	super("asyncCall");
    }
	
}
