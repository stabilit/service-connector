package org.serviceconnector.scmp.cache;

import org.serviceconnector.scmp.cache.impl.EHCacheSCMPCacheImpl;

public class SCMPCacheImplFactory {

	public static ISCMPCacheImpl getDefaultCacheImpl(String serviceName) {
		return new EHCacheSCMPCacheImpl(serviceName);
	}

}
