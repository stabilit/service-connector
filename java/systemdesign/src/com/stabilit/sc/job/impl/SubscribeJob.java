package com.stabilit.sc.job.impl;

import com.stabilit.sc.job.ISubscribe;
import com.stabilit.sc.job.Job;

public class SubscribeJob extends Job implements ISubscribe {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3350958773182752728L;

	public SubscribeJob() {
		super("subscribe");
	}

	public void setSubsribeID(String subscribeID) {
		this.setAttribute(SUBSCRIBE_ID, subscribeID);
	}
	
	public String getSubscribeID() {
		return (String)this.getAttribute(SUBSCRIBE_ID);		
	}
}
