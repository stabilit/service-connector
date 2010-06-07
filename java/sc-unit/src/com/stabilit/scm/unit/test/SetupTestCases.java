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
package com.stabilit.scm.unit.test;

import java.io.File;
import java.io.IOException;

import com.stabilit.scm.ServiceConnector;
import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.IConnectionListener;
import com.stabilit.scm.listener.IExceptionListener;
import com.stabilit.scm.listener.ILoggerListener;
import com.stabilit.scm.listener.IPerformanceListener;
import com.stabilit.scm.listener.IRuntimeListener;
import com.stabilit.scm.listener.ISessionListener;
import com.stabilit.scm.listener.LoggerPoint;
import com.stabilit.scm.listener.PerformancePoint;
import com.stabilit.scm.listener.RuntimePoint;
import com.stabilit.scm.listener.SessionPoint;
import com.stabilit.scm.log.Level;
import com.stabilit.scm.log.impl.ConnectionLogger;
import com.stabilit.scm.log.impl.ExceptionLogger;
import com.stabilit.scm.log.impl.GeneralLogger;
import com.stabilit.scm.log.impl.LoggerFactory;
import com.stabilit.scm.log.impl.PerformanceLogger;
import com.stabilit.scm.log.impl.RuntimeLogger;
import com.stabilit.scm.log.impl.SessionLogger;
import com.stabilit.scm.sim.Simulation;
import com.stabilit.scm.srv.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.conf.ResponderConfig;
import com.stabilit.scm.unit.UnitCommandFactory;

/**
 * @author JTraber
 */
public class SetupTestCases {

	private static SetupTestCases setupTestCases = null;

	private SetupTestCases() {
	}

	public static void init() throws IOException {
		ResponderConfig config = new ResponderConfig();
		config.load("sc.properties");

		deleteLog();
		// setup loggers
		try {
			LoggerFactory loggerFactory = LoggerFactory.getCurrentLoggerFactory(config.getLoggerKey());
			ConnectionPoint.getInstance().addListener(
					(IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class));
			ExceptionPoint.getInstance().addListener(
					(IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class));
			RuntimePoint.getInstance().addListener((IRuntimeListener) loggerFactory.newInstance(RuntimeLogger.class));
			LoggerPoint.getInstance().addListener((ILoggerListener) loggerFactory.newInstance(GeneralLogger.class));
			LoggerPoint.getInstance().setLevel(Level.DEBUG);
			PerformancePoint.getInstance().addListener(
					(IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class));
			PerformancePoint.getInstance().setOn(true);
			SessionPoint.getInstance().addListener((ISessionListener) loggerFactory.newInstance(SessionLogger.class));
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
			try {
				init();
				setupTestCases = new SetupTestCases();
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
			try {
				init();
				setupTestCases = new SetupTestCases();
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				ServiceConnector.main(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
