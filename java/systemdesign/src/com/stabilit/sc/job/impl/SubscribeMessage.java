package com.stabilit.sc.job.impl;

import com.stabilit.sc.msg.Message;

public class SubscribeMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7873008149843069401L;
	public static String ID = "subscribe";

	public SubscribeMessage() {
		super(ID);
	}

}
