package org.serviceconnector.scmp.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.serviceconnector.scmp.cache.ISCMPCacheImpl;

public class EHCacheSCMPCacheImpl implements ISCMPCacheImpl {

	private static CacheManager manager = new CacheManager();
	private CacheConfiguration config;
	private Cache cache;

	public EHCacheSCMPCacheImpl(String serviceName) {
		this.config = new CacheConfiguration(serviceName, 1000);
		//TODO from configuration file
		this.config.setTimeToIdleSeconds(60);
		this.config.setTimeToLiveSeconds(120);
		this.config.setMaxElementsInMemory(10000);
		this.config.setMaxElementsOnDisk(1000000);
		this.config.setDiskStorePath("cache");
		this.cache = new Cache(this.config);
		manager.addCache(this.cache);
	}
	
	public Object get(Object key) {
		Element element = this.cache.get(key);
		if (element == null) {
			return null;
		}
		return element.getObjectValue();
	}
	
	public void put(Object key, Object value) {
		Element element = new Element(key, value);
		this.cache.put(element);
	}
	
	public boolean remove(Object key) {
		boolean ret = this.cache.remove(key);
		return ret;
	}
}
