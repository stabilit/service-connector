package org.serviceconnector.service;

import org.serviceconnector.server.CascadedSC;

public class CascadedCacheGuardian extends CascadedPublishService {

	public CascadedCacheGuardian(String name, CascadedSC cascadedSC, int noDataIntervalSeconds) {
		super(name, cascadedSC, noDataIntervalSeconds);
		this.type = ServiceType.CASCADED_CACHE_GUARDIAN;
	}
}
