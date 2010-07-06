package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.ISCMessageCallback;
import com.stabilit.scm.cln.service.SCMessage;

public abstract class SCMessageCallbackAdapter implements ISCMessageCallback, IActiveState {

	private boolean active;

	public SCMessageCallbackAdapter() {
		this.active = false;
	}

	@Override
	public abstract void callback(SCMessage reply) throws Exception;

	@Override
	public abstract void callback(Throwable th);

	@Override
	public final boolean isActive() {
		return this.active;
	}
	
	@Override
	public final synchronized void setActive(boolean active) {
		this.active = active;
		if (this.active == false) {
			this.notifyAll();
		}
	}

	@Override
	public final synchronized void join() throws Exception {
		if (this.active) {
			this.wait();
		}
		return;
	}
}
