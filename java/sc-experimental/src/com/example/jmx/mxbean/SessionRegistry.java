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
package com.example.jmx.mxbean;

/**
 * @author JTraber
 * @param <T>
 * 
 */
public final class SessionRegistry extends Registry implements SeesionRegistryMXBean {

	private static SessionRegistry instance = new SessionRegistry();

	private SessionRegistry() {
	}

	public static SessionRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, MapBean<?> mapBean) {
		this.put(key, mapBean);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapBeanSample[] getBeans() {
		MapBeanSample[] queuesss = new MapBeanSample[registryMap.size()];
		int i = 0;
		System.out.println(registryMap.size());
		for (MapBean que : registryMap.values()) {
			queuesss[i] = new MapBeanSample(que);
			i++;
		}
		return queuesss;
	}
}
