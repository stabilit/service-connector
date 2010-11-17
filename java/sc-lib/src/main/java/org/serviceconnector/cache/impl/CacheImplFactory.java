package org.serviceconnector.cache.impl;

import org.serviceconnector.cache.ICacheConfiguration;

public class CacheImplFactory {

	public static ICacheImpl getDefaultCacheImpl(ICacheConfiguration scmpCacheConfiguration, String serviceName) {
		return new EHCacheImpl(scmpCacheConfiguration, serviceName);
	}
	
	public static void destroy() {
		EHCacheImpl.destroy();
	}

}
