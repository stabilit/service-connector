package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IService;

public abstract class SCMessageCallback implements ISCMessageCallback {

	private IService service;

	public SCMessageCallback(IService service) {
		this.service = service;
	}

	@Override
	public abstract void callback(ISCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);

	@Override
	public IService getService() {
		return service;
	}
}
