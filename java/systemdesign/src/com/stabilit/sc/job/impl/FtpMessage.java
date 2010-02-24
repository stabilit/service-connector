package com.stabilit.sc.job.impl;

import com.stabilit.sc.msg.Message;

public class FtpMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3181128027295939911L;
	
	public static String ID = "ftp";
	
	public FtpMessage() {
    	super(ID);
    }
}
