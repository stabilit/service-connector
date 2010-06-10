/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.unit;

import java.io.File;
import java.io.IOException;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.ResponderConfig;
import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IConnectionListener;
import com.stabilit.scm.common.listener.IExceptionListener;
import com.stabilit.scm.common.listener.ILoggerListener;
import com.stabilit.scm.common.listener.IPerformanceListener;
import com.stabilit.scm.common.listener.IRuntimeListener;
import com.stabilit.scm.common.listener.ISessionListener;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.listener.RuntimePoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.log.Level;
import com.stabilit.scm.common.log.impl.ConnectionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.log.impl.GeneralLogger;
import com.stabilit.scm.common.log.impl.LoggerFactory;
import com.stabilit.scm.common.log.impl.PerformanceLogger;
import com.stabilit.scm.common.log.impl.RuntimeLogger;
import com.stabilit.scm.common.log.impl.SessionLogger;
import com.stabilit.scm.sc.ServiceConnector;
import com.stabilit.scm.sim.Simulation;

public class TestEnvironmentStarter {

	public static void main(String[] args) throws Exception {
		TestEnvironmentStarter starter = new TestEnvironmentStarter();
		starter.startUpServer();
	}

	private void startUpServer() throws Exception {
//		this.init();
		CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
		ServiceConnector.main(null);
		Simulation.main(null);
	}

	public void init() throws IOException {
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
}
