package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.SCMessage;

public abstract class SCPublishServiceCallbackAdapter extends SCMessageCallbackAdapter {

	protected IPublishService service;
	
	public SCPublishServiceCallbackAdapter(IPublishService service) {
		super();
		this.service = service;
	}
	
	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);		
}
