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
package com.stabilit.sc.sim.registry;

import com.stabilit.sc.common.io.Session;
import com.stabilit.sc.common.registry.Registry;

/**
 * @author JTraber
 *
 */
public final class SimulationSessionRegistry extends Registry {
	
	private static SimulationSessionRegistry instance = new SimulationSessionRegistry();
	
	private SimulationSessionRegistry() {
	}
	
	public static SimulationSessionRegistry getCurrentInstance() {
		return instance;
	}	
	
	public void add(Object key, Session session) {
		this.put(key, session);
	}
}
