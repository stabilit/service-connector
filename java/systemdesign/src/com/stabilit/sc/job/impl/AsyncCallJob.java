package com.stabilit.sc.job.impl;

import com.stabilit.sc.job.ISubscribe;
import com.stabilit.sc.job.Job;

public class AsyncCallJob extends Job implements ISubscribe {

	private static final long serialVersionUID = -6937364888160411389L;
	
	public AsyncCallJob(ISubscribe subscribeJob) {
    	super("asyncCall");
    	this.setSubsribeID(subscribeJob.getSubscribeID());
    }

	public void setSubsribeID(String subscribeID) {
		this.setAttribute(SUBSCRIBE_ID, subscribeID);
	}
	
	public String getSubscribeID() {
		return (String)this.getAttribute(SUBSCRIBE_ID);		
	}
	
}
