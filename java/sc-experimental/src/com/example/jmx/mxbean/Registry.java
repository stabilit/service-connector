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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTraber
 * 
 */
public class Registry implements IRegistry {

	protected Map<Object, Queuesse> registryMap;

	public Registry() {
		registryMap = new ConcurrentHashMap<Object, Queuesse>();
	}

	public void put(Object key, Queuesse value) {
		registryMap.put(key, value);
	}

	public Queuesse get(Object key) {
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
}
