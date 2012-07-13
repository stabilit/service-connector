package org.serviceconnector.service;

import org.serviceconnector.ctx.AppContext;

public class CacheGuardian extends PublishService {

	public CacheGuardian(String name) {
		super(name);
		this.type = ServiceType.CACHE_GUARDIAN;
	}

	@Override
	public void notifyRemovedSession() {
		if (this.getCountAllocatedSessions() == 0) {
			// last session just removed - disable cache guardian
			AppContext.getSCCache().removeManagedDataForGuardian(this.getName());
		}
	}
}
