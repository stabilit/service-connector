package com.stabilit.sc.message.impl;

import com.stabilit.sc.message.ISubscribe;
import com.stabilit.sc.message.Message;

public class AsyncCallMessage extends Message implements ISubscribe {

	private static final long serialVersionUID = -6937364888160411389L;
	
	public AsyncCallMessage(ISubscribe subscribeJob) {
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
