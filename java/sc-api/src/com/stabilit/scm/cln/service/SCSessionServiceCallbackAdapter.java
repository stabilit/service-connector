package com.stabilit.scm.cln.service;

public abstract class SCSessionServiceCallbackAdapter extends SCMessageCallbackAdapter {

	private ISessionService sessionService;
	
	public SCSessionServiceCallbackAdapter(ISessionService sessionService) {
		super();
		this.sessionService = sessionService;
	}
	
	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);		
}
