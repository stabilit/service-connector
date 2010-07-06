package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.SCMessage;

public abstract class SCServiceCallbackAdapter extends SCMessageCallbackAdapter {

	protected IService service;
	
	public SCServiceCallbackAdapter(IService service) {
		super();
		this.service = service;
	}
	
	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);		
}
