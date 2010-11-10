package org.serviceconnector.scmp.cache.impl;

import java.io.File;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.serviceconnector.scmp.cache.ISCMPCacheImpl;

public class EHCacheSCMPCacheImpl implements ISCMPCacheImpl {

	private static Object syncObj = new Object();
	private static CacheManager manager = null;
	private static CacheConfiguration config = null;
	private Cache cache;

	public EHCacheSCMPCacheImpl(String serviceName) {
		synchronized (syncObj) {
			if (manager == null) {
				Configuration configuration = new Configuration();
				DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
				diskStoreConfiguration.setPath("../../dev/cache");
				configuration.addDiskStore(diskStoreConfiguration);
				configuration.setName("scCache");
				CacheConfiguration defaultCacheConfiguration = new CacheConfiguration(
						"scCache", 1000);
				// TODO from configuration file
				defaultCacheConfiguration.setTimeToIdleSeconds(60);
				defaultCacheConfiguration.setTimeToLiveSeconds(120);
				defaultCacheConfiguration.setMaxElementsInMemory(10000);
				defaultCacheConfiguration.setMaxElementsOnDisk(1000000);
				defaultCacheConfiguration.setDiskPersistent(true);
				defaultCacheConfiguration.setName("scCache");
				configuration
						.setDefaultCacheConfiguration(defaultCacheConfiguration);
				configuration.setUpdateCheck(false); // disable update checker
				manager = new CacheManager(configuration);
			}
		}
		this.config = new CacheConfiguration(serviceName, 1000);
		// TODO from configuration file
		this.config.setTimeToIdleSeconds(60);
		this.config.setTimeToLiveSeconds(120);
		this.config.setMaxElementsInMemory(10000);
		this.config.setMaxElementsOnDisk(1000000);
		this.config.setDiskPersistent(true);
		this.config.setName("scCache." + serviceName);
		this.cache = new Cache(this.config);
		this.cache.setName("scCache." + serviceName);
		this.cache.setDiskStorePath(serviceName);
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

	public static void destroy() {
		synchronized (syncObj) {
			if (manager != null) {
				manager.clearAll();
				String[] cacheNames = manager.getCacheNames();
				for (String cacheName : cacheNames) {
					Ehcache ehCache = manager.getEhcache(cacheName);
					if (ehCache instanceof Cache) {
						Cache cache = (Cache) ehCache;
						cache.dispose();
					}
				}
				manager.removalAll();
				String diskStorePath = manager.getDiskStorePath();
				File diskStorePathFile = new File(diskStorePath);
				if (diskStorePathFile.exists()) {
					File[] files = diskStorePathFile.listFiles();
					for (int i = 0; i < files.length; i++) {
//						if (files[i].isDirectory()) {
//							deleteDirectory(files[i]);
//						} else {
						if (files[i].isFile()) {
							files[i].delete();
						}
//						}
					}
				}
			}
		}
	}

	private static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}
}
