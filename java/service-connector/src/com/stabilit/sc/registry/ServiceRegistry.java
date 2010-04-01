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
package com.stabilit.sc.registry;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.registry.Registry;

/**
 * @author JTraber
 * 
 */
public final class ServiceRegistry extends Registry {

	private static ServiceRegistry instance = new ServiceRegistry();
	
	private ServiceRegistry() {
		log = Logger.getLogger(ServiceRegistry.class);
	}

	public static ServiceRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, ServiceRegistryItem item) {
		this.put(key, item);
	}

	public synchronized ServiceRegistryItem allocate(Object key, SCMP scmp) throws Exception {
		ServiceRegistryItem item = (ServiceRegistryItem) this.get(key); // is this a list, TODO
		if (item.isAllocated()) {
			return null;
		}
		item.allocate(scmp);
		return item;
	}

	public synchronized void deallocate(ServiceRegistryItem item, SCMP scmp) throws Exception {
		if (item.isAllocated()) {
			item.deallocate(scmp);
		}
		return;
	}

}
