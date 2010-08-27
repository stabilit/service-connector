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

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.listener.DefaultStatisticsListener;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IStatisticsListener;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.util.ConsoleUtil;
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

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SC.class);
	
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
		String fileName = ConsoleUtil.getArg(args, "-filename");
		if (fileName == null) {
			// fileName not set - use default
			SC.run(Constants.DEFAULT_PROPERTY_FILE_NAME);
		} else {
			// fileName extracted from vm arguments
			SC.run(fileName);
		}
	}

	/**
	 * Run SC responders.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run(String fileName) throws Exception {
		ResponderConfigPool config = new ResponderConfigPool();
		config.load(fileName);

		LoggerConfigurator loggerConfigurator = new LoggerConfigurator(config);

		if (config.loggable()) {
			// logging enabled
			loggerConfigurator.addAllLoggers();
		}
		SC.initializeJMX(loggerConfigurator);

		// load services
		ServiceLoader.load(fileName);

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());
		}

		List<ICommunicatorConfig> respConfigList = config.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			IResponder resp = new Responder(respConfig);
			try {
				resp.create();
				logger.info("Run server " + respConfig.getCommunicatorName() + " on " + respConfig.getHost() + ":"
						+ respConfig.getPort());
				resp.startListenAsync();
			} catch (Exception ex) {
				logger.error("run "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(SC.class, ex);
			}
		}
	}

	/**
	 * Initialize java management interface stuff.
	 * 
	 * @param loggerConfigurator
	 *            the logger configurator
	 */
	private static void initializeJMX(LoggerConfigurator loggerConfigurator) {
		try {
			// Necessary to make access for JMX client available
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mxbeanNameSessReg = new ObjectName("com.stabilit.scm.registry:type=SessionRegistry");
			ObjectName mxbeanNameServiceReg = new ObjectName("com.stabilit.scm.registry:type=ServiceRegistry");
			ObjectName mxbeanNameServerReg = new ObjectName("com.stabilit.scm.registry:type=ServerRegistry");
			ObjectName mxbeanNameLogging = new ObjectName("com.stabilit.scm:type=LoggerConfigurator");

			// Register the Queue Sampler MXBean
			mbs.registerMBean(SessionRegistry.getCurrentInstance(), mxbeanNameSessReg);
			mbs.registerMBean(ServiceRegistry.getCurrentInstance(), mxbeanNameServiceReg);
			mbs.registerMBean(ServerRegistry.getCurrentInstance(), mxbeanNameServerReg);
			mbs.registerMBean(loggerConfigurator, mxbeanNameLogging);
		} catch (Throwable th) {
			logger.error("initializeJMX "+th.getMessage(), th);
			ExceptionPoint.getInstance().fireException(SC.class, th);
		}
	}
}
