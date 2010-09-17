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
package org.serviceconnector;

import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.util.Enumeration;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.serviceconnector.cmd.factory.CommandFactory;
import org.serviceconnector.conf.Constants;
import org.serviceconnector.conf.ICommunicatorConfig;
import org.serviceconnector.conf.ResponderConfigPool;
import org.serviceconnector.log.JMXLoggingManager;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.res.IResponder;
import org.serviceconnector.sc.cmd.ServiceConnectorCommandFactory;
import org.serviceconnector.sc.registry.ServerRegistry;
import org.serviceconnector.sc.registry.ServiceRegistry;
import org.serviceconnector.sc.registry.SessionRegistry;
import org.serviceconnector.sc.service.ServiceLoader;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.CommandLineUtil;
import org.serviceconnector.util.Statistics;


/**
 * The Class SC. Starts the core (responders) of the Service Connector.
 * 
 * @author JTraber
 */
public final class SC {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SC.class);

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
		String fileName = CommandLineUtil.getArg(args, Constants.CLI_CONFIG_ARG);
		if (fileName == null) {
			throw new SCServiceException("Configuration file missing");
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

		// initialize JMX
		SC.initializeJMX();

		// initialize statistics
		Statistics statistics =  Statistics.getInstance();
		
		// load services
		ServiceLoader.load(fileName);
		
		// clean up and initialize cache
		//Cache cache = Cache.initialize();

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());
		}

		List<ICommunicatorConfig> respConfigList = config.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			IResponder responder = new Responder(respConfig);
			try {
				responder.create();
				logger.info("Run server " + respConfig.getCommunicatorName() + " on " + respConfig.getHost() + ":"
						+ respConfig.getPort());
				responder.startListenAsync();
			} catch (Exception ex) {
				logger.error("run", ex);
				throw ex;
			}
		}

		if (config.writePID()) {
			SC.writePIDFile();
		}
	}

	/**
	 * Initialize java management interface stuff.
	 */
	private static void initializeJMX() {
		try {
			// Necessary to make access for JMX client available
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mxbeanNameSessReg = new ObjectName("org.serviceconnector.registry:type=SessionRegistry");
			ObjectName mxbeanNameServiceReg = new ObjectName("org.serviceconnector.registry:type=ServiceRegistry");
			ObjectName mxbeanNameServerReg = new ObjectName("org.serviceconnector.registry:type=ServerRegistry");
			ObjectName mxbeanNameLoggingManager = new ObjectName("org.serviceconnector.logging:type=LoggingManager");

			// Register the Queue Sampler MXBean
			mbs.registerMBean(SessionRegistry.getCurrentInstance(), mxbeanNameSessReg);
			mbs.registerMBean(ServiceRegistry.getCurrentInstance(), mxbeanNameServiceReg);
			mbs.registerMBean(ServerRegistry.getCurrentInstance(), mxbeanNameServerReg);
			JMXLoggingManager loggingManager = new JMXLoggingManager();
			mbs.registerMBean(loggingManager, mxbeanNameLoggingManager);
		} catch (Throwable th) {
			logger.error("initializeJMX", th);
		}
	}

	/**
	 * Writes a file. PID of SC gets written in. Is used for testing purpose to verify that SC is running properly.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void writePIDFile() throws Exception {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		long pid = Long.parseLong(processName.split("@")[0]);
		FileWriter fw = null;
		try {
			Category rootLogger = logger.getParent();
			Enumeration<?> appenders = rootLogger.getAllAppenders();
			FileAppender fileAppender = null;
			while (appenders.hasMoreElements()) {
				Appender appender = (Appender) appenders.nextElement();
				if (appender instanceof FileAppender) {
					fileAppender = (FileAppender) appender;
					break;
				}
			}
			String fileName = fileAppender.getFile();
			String path = fileName.substring(0, fileName.lastIndexOf("/"));

			File pidFile = new File(path + "/pid.log");
			fw = new FileWriter(pidFile);
			fw.write("pid: " + pid);
			fw.flush();
			fw.close();
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}
}
