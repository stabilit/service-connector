package org.serviceconnector.service;

import org.serviceconnector.ctx.AppContext;

public class CacheUpdateRetriever extends PublishService {

	public CacheUpdateRetriever(String name) {
		super(name);
		this.type = ServiceType.CACHE_UPDATE_RETRIEVER;
	}

	@Override
	public void notifyRemovedSession() {
		if (this.getCountAllocatedSessions() == 0) {
			// last session just removed - disable cache guardian
			AppContext.getSCCache().removeManagedDataForGuardian(this.getName());
		}
	}
}
