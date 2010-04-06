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

import com.stabilit.sc.common.registry.Registry;
import com.stabilit.sc.common.util.MapBean;

/**
 * @author JTraber
 * 
 */
public final class ConnectionRegistry extends Registry {

	private static ConnectionRegistry instance = new ConnectionRegistry();

	private ConnectionRegistry() {
		log = Logger.getLogger("connections." + ConnectionRegistry.class.getName());
	}

	public static ConnectionRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, MapBean<Object> mapBean) {
		this.put(key, mapBean);
	}
}
