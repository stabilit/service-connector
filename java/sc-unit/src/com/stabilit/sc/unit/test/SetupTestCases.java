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
import com.stabilit.sc.listener.ConnectionPoint;
import com.stabilit.sc.listener.ExceptionPoint;
import com.stabilit.sc.listener.IConnectionListener;
import com.stabilit.sc.listener.IExceptionListener;
import com.stabilit.sc.listener.ILoggerListener;
import com.stabilit.sc.listener.IPerformanceListener;
import com.stabilit.sc.listener.IRuntimeListener;
import com.stabilit.sc.listener.LoggerPoint;
import com.stabilit.sc.listener.PerformancePoint;
import com.stabilit.sc.listener.RuntimePoint;
import com.stabilit.sc.log.Level;
import com.stabilit.sc.log.impl.ConnectionLogger;
import com.stabilit.sc.log.impl.ExceptionLogger;
import com.stabilit.sc.log.impl.GeneralLogger;
import com.stabilit.sc.log.impl.LoggerFactory;
import com.stabilit.sc.log.impl.PerformanceLogger;
import com.stabilit.sc.log.impl.RuntimeLogger;
import com.stabilit.sc.sim.Simulation;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.unit.UnitCommandFactory;

/**
 * @author JTraber
 */
public class SetupTestCases {

	private static SetupTestCases setupTestCases = null;

	private SetupTestCases() {
	}

	public static void init() {
		deleteLog();
		// setup loggers
		try {
			LoggerFactory loggerFactory = LoggerFactory.getCurrentLoggerFactory();
			ConnectionPoint.getInstance().addListener(
					(IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class));
			ExceptionPoint.getInstance().addListener(
					(IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class));
			RuntimePoint.getInstance().addListener(
					(IRuntimeListener) loggerFactory.newInstance(RuntimeLogger.class));
			LoggerPoint.getInstance().addListener(
					(ILoggerListener) loggerFactory.newInstance(GeneralLogger.class));
			LoggerPoint.getInstance().setLevel(Level.DEBUG);
			PerformancePoint.getInstance().addListener(
					(IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class));
			PerformancePoint.getInstance().setOn(true);
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
