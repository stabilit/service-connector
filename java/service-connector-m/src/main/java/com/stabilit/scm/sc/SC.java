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
package com.stabilit.scm.sc;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.CommunicatorConfigPool;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.DefaultStatisticsListener;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IConnectionListener;
import com.stabilit.scm.common.listener.IExceptionListener;
import com.stabilit.scm.common.listener.ILoggerListener;
import com.stabilit.scm.common.listener.IPerformanceListener;
import com.stabilit.scm.common.listener.ISessionListener;
import com.stabilit.scm.common.listener.IStatisticsListener;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.listener.StatisticsPoint;
import com.stabilit.scm.common.log.Level;
import com.stabilit.scm.common.log.impl.ConnectionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.log.impl.LoggerFactory;
import com.stabilit.scm.common.log.impl.PerformanceLogger;
import com.stabilit.scm.common.log.impl.SessionLogger;
import com.stabilit.scm.common.log.impl.TopLogger;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.sc.cmd.factory.impl.ServiceConnectorCommandFactory;
import com.stabilit.scm.sc.registry.ServerRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.ServiceLoader;

/**
 * The Class SC. Starts the core (responders) of the Service Connector.
 * 
 * @author JTraber
 */
public final class SC {

	public static IStatisticsListener statisticsListener = new DefaultStatisticsListener();

	/**
	 * Instantiates a new service connector.
	 */
	private SC() {
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		SC.run();
	}

	/**
	 * Run SC responders.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run() throws Exception {
		ResponderConfigPool config = new ResponderConfigPool();
		config.load("sc.properties");
		SC.initLogStuff(config);

		// load services
		ServiceLoader.load("sc.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());
		}

		SC.initializeJMXStuff();

		List<ICommunicatorConfig> respConfigList = config.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			IResponder resp = new Responder(respConfig);
			try {
				resp.create();
				LoggerPoint.getInstance().fireInfo(
						SC.class,
						"Run server " + respConfig.getCommunicatorName() + " on " + respConfig.getHost() + ":"
								+ respConfig.getPort());
				resp.runAsync();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(SC.class, e);
			}
		}
	}

	/**
	 * Initialize the log stuff.
	 * 
	 * @param config
	 *            the configuration
	 */
	private static void initLogStuff(CommunicatorConfigPool config) {
		LoggerFactory loggerFactory = LoggerFactory.getCurrentLoggerFactory(config.getLoggerKey());
		ConnectionPoint.getInstance().addListener(
				(IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class));
		ExceptionPoint.getInstance().addListener((IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class));
		LoggerPoint.getInstance().addListener((ILoggerListener) loggerFactory.newInstance(TopLogger.class));
		LoggerPoint.getInstance().setLevel(Level.DEBUG);
		PerformancePoint.getInstance().addListener(
				(IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class));
		PerformancePoint.getInstance().setOn(true);
		SessionPoint.getInstance().addListener((ISessionListener) loggerFactory.newInstance(SessionLogger.class));
		StatisticsPoint.getInstance().addListener(statisticsListener);
	}

	/**
	 * Initialize jmx stuff.
	 */
	private static void initializeJMXStuff() {
		try {
			// Necessary to make access for JMX client available
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mxbeanNameSessReg = new ObjectName("com.stabilit.scm.registry:type=SessionRegistry");
			ObjectName mxbeanNameServiceReg = new ObjectName("com.stabilit.scm.registry:type=ServiceRegistry");
			ObjectName mxbeanNameServerReg = new ObjectName("com.stabilit.scm.registry:type=ServerRegistry");

			// Register the Queue Sampler MXBean
			mbs.registerMBean(SessionRegistry.getCurrentInstance(), mxbeanNameSessReg);
			mbs.registerMBean(ServiceRegistry.getCurrentInstance(), mxbeanNameServiceReg);
			mbs.registerMBean(ServerRegistry.getCurrentInstance(), mxbeanNameServerReg);
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(SC.class, th);
		}
	}
}
