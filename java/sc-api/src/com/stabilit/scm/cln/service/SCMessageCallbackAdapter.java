package com.stabilit.scm.cln.service;

public abstract class SCMessageCallbackAdapter implements ISCMessageCallback {

	private boolean open;
	
	public SCMessageCallbackAdapter() {
		this.open = false;
	}
	
	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);
	
	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public synchronized void setOpen(boolean open) {
        this.open = open;
        if (this.open == false) {
        	this.notify();
        }
	}
	
	@Override
	public synchronized void join() throws Exception {
       if (this.open) {
    	   this.wait();
       }
       return;
	}

}
