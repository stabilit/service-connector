/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.unit.test;

import java.io.File;

import com.stabilit.sc.ServiceConnector;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.listener.PerformanceListenerSupport;
import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.log.Level;
import com.stabilit.sc.log.impl.ConnectionLogger;
import com.stabilit.sc.log.impl.ExceptionLogger;
import com.stabilit.sc.log.impl.GeneralLogger;
import com.stabilit.sc.log.impl.PerformanceLogger;
import com.stabilit.sc.log.impl.WarningLogger;
import com.stabilit.sc.sim.Simulation;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.unit.UnitCommandFactory;

/**
 * @author JTraber
 * 
 */
public class SetupTestCases {

	private static SetupTestCases setupTestCases = null;

	private SetupTestCases() {
	}

	public static void init() {
		deleteLog();
		// setup loggers
		try {
			ConnectionListenerSupport.getInstance().addListener(new ConnectionLogger());
			ExceptionListenerSupport.getInstance().addListener(new ExceptionLogger());
			WarningListenerSupport.getInstance().addListener(new WarningLogger());
			LoggerListenerSupport.getInstance().addListener(new GeneralLogger());
			LoggerListenerSupport.getInstance().setLevel(Level.DEBUG);
			PerformanceListenerSupport.getInstance().addListener(new PerformanceLogger());
			PerformanceListenerSupport.getInstance().setOn(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteLog() {
		File logDir = new File("log");

		for (File file : logDir.listFiles()) {
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".log")) {
					file.delete();
				}
			}
		}
	}

	public static void setupAll() {
		if (setupTestCases == null) {
			init();
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

	public static void setupSC() {
		if (setupTestCases == null) {
			init();
			setupTestCases = new SetupTestCases();
			try {
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				ServiceConnector.main(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
