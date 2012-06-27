package org.serviceconnector.service;

public class CacheUpdateRetriever extends PublishService {

	public CacheUpdateRetriever(String name) {
		super(name);
		this.type = ServiceType.CACHE_UPDATE_RETRIEVER;
	}
}
