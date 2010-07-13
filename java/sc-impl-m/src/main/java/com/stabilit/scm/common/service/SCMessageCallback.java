package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.ISCMessageCallback;
import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.SCMessage;

public abstract class SCMessageCallback implements ISCMessageCallback {

	private IService service;

	public SCMessageCallback(IService service) {
		this.service = service;
	}

	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);

	@Override
	public IService getService() {
		return service;
	}
}
