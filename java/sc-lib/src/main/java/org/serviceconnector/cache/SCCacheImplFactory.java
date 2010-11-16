package org.serviceconnector.cache;

import org.serviceconnector.cache.impl.EHCacheImpl;
import org.serviceconnector.cache.impl.ICacheImpl;

public class SCCacheImplFactory {

	public static ICacheImpl getDefaultCacheImpl(SCCacheConfiguration scmpCacheConfiguration, String serviceName) {
		return new EHCacheImpl(scmpCacheConfiguration, serviceName);
	}
	
	public static void destroy() {
		EHCacheImpl.destroy();
	}

}
