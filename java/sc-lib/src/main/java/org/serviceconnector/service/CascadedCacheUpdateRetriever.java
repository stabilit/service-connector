package org.serviceconnector.service;

import org.serviceconnector.server.CascadedSC;

public class CascadedCacheUpdateRetriever extends CascadedPublishService {

	public CascadedCacheUpdateRetriever(String name, CascadedSC cascadedSC, int noDataIntervalSeconds) {
		super(name, cascadedSC, noDataIntervalSeconds);
		this.type = ServiceType.CASCADED_CACHE_UPDATE_RETRIEVER;
	}
}
