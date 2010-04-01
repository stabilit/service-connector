/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.common.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.common.registry.jmx.IRegistryMXBean;
import com.stabilit.sc.common.registry.jmx.MapBeanWrapperJMX;
import com.stabilit.sc.common.util.MapBean;

/**
 * @author JTraber
 * 
 */
public class Registry implements IRegistry, IRegistryMXBean {

	private Map<Object, MapBean<?>> registryMap;

	public Registry() {
		registryMap = new ConcurrentHashMap<Object, MapBean<?>>();
	}

	public void put(Object key, MapBean<?> value) {
		registryMap.put(key, value);
	}

	public MapBean<?> get(Object key) {
		return registryMap.get(key);
	}
	
	public void remove(Object key) {
		this.registryMap.remove(key);
	}
	
	public boolean containsKey(Object key) {
		return registryMap.containsKey(key);
	}

	@Override
	public String toString() {
		StringBuffer dump = new StringBuffer();
		for (Object key : registryMap.keySet()) {
			dump.append(key);
			dump.append(":");
			dump.append(registryMap.get(key).toString());
		}
		return dump.toString();
	}
	
	@Override
	public MapBeanWrapperJMX[] getEntries() {
		MapBeanWrapperJMX[] mapBeanStringJMX = new MapBeanWrapperJMX[registryMap.size()];
		int i = 0;
		for (Object key : registryMap.keySet()) {
			mapBeanStringJMX[i] = new MapBeanWrapperJMX(key.toString(),(MapBean<?>) registryMap.get(key) );
			i++;
		}
		return mapBeanStringJMX;
	}
}
