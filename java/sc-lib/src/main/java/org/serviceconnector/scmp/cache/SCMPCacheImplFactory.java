package org.serviceconnector.scmp.cache;

import org.serviceconnector.scmp.cache.impl.EHCacheSCMPCacheImpl;

public class SCMPCacheImplFactory {

	public static ISCMPCacheImpl getDefaultCacheImpl(SCMPCacheConfiguration scmpCacheConfiguration, String serviceName) {
		return new EHCacheSCMPCacheImpl(scmpCacheConfiguration, serviceName);
	}
	
	public static void destroy() {
		EHCacheSCMPCacheImpl.destroy();
	}

}
