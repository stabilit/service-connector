package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessage;

public abstract class SCSessionServiceCallbackAdapter extends SCMessageCallbackAdapter {

	protected ISessionService sessionService;
	
	public SCSessionServiceCallbackAdapter(ISessionService sessionService) {
		super();
		this.sessionService = sessionService;
	}
	
	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);		
}
