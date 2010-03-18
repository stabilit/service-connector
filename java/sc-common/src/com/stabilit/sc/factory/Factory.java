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
package com.stabilit.sc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTraber
 * 
 */
public class Factory {

	protected Map<String, IFactoryable> factoryMap = new ConcurrentHashMap<String, IFactoryable>();

	public IFactoryable getInstance() {
		return getInstance("default");
	}

	public void add(String key, IFactoryable factoryInstance) {
		factoryMap.put(key, factoryInstance);
	}

	public void remove(String key) {
		factoryMap.remove(key);
	}

	public IFactoryable getInstance(String key) {
		IFactoryable factoryInstance = factoryMap.get(key);
		return factoryInstance;
	}

	public IFactoryable newInstance() {
		return newInstance("default");
	}

	public IFactoryable newInstance(String key) {
		IFactoryable factoryInstance = this.getInstance(key);
		if (factoryInstance == null) {
			return null;
		}
		return factoryInstance.newInstance();
	}

}
