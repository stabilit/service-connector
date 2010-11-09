package org.serviceconnector.scmp.cache;

public interface ISCMPCacheImpl {
	public abstract Object get(Object key);
	public abstract void put(Object key, Object value);
	public abstract boolean remove(Object key);
}
