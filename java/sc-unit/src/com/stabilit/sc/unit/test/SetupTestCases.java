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
package com.stabilit.sc.unit.test;

import com.stabilit.sc.ServiceConnector;
import com.stabilit.sc.sim.Simulation;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.unit.UnitCommandFactory;


/**
 * @author JTraber
 * 
 */
public class SetupTestCases {

	private static SetupTestCases setupTestCases = null;

	public static void setup() {
		if (setupTestCases == null) {
			setupTestCases = new SetupTestCases();
			try {
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				ServiceConnector.main(null);
				Simulation.main(null);			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private SetupTestCases() {

	}
}
