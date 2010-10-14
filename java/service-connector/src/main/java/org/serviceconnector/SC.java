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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.serviceconnector.cmd.sc.ServiceConnectorCommandFactory;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.ResponderConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ILoggingManagerMXBean;
import org.serviceconnector.log.JMXLoggingManager;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.service.ServiceLoader;
import org.serviceconnector.util.CommandLineUtil;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.cmd.sc.ServiceConnectorWebCommandFactory;
import org.serviceconnector.web.ctx.WebContext;

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
		String configFileName = CommandLineUtil.getArg(args, Constants.CLI_CONFIG_ARG);
		try {
			SC.run(configFileName);
		} catch (Exception ex) {
			logger.fatal(ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * Run SC responders.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run(String configFileName) throws Exception {

		if (configFileName == null) {
			throw new SCServiceException("Configuration file is missing");
		}

		SystemInfo.setConfigFileName(configFileName);

		ResponderConfiguration config = new ResponderConfiguration();
		config.load(configFileName);

		// Initialize service connector command factory
		AppContext appContext = AppContext.getCurrentContext();
		appContext.initContext(new ServiceConnectorCommandFactory());
		WebContext webContext = WebContext.getCurrentContext();
		webContext.initContext(new ServiceConnectorWebCommandFactory());
		
		// initialize JMX
		SC.initializeJMX();

		// initialize statistics
		Statistics statistics = Statistics.getInstance();
		statistics.setStartupDateTime(new Timestamp(Calendar.getInstance().getTime().getTime()));

		// load services
		ServiceLoader.load(configFileName);

		// clean up and initialize cache
		// Cache cache = Cache.initialize();

		// TODO write system info into log logger.log(priority, message) 
		
		List<CommunicatorConfig> respConfigList = config.getResponderConfigList();

		for (CommunicatorConfig respConfig : respConfigList) {
			IResponder responder = new Responder(respConfig);
			try {
				responder.create();
				logger.info("Start listener " + respConfig.getCommunicatorName() + " on " + respConfig.getHost() + ":"
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

			AppContext appContext = AppContext.getCurrentContext();
			// Necessary to make access for JMX client available
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mxbeanNameSessReg = new ObjectName("org.serviceconnector.registry:type=SessionRegistry");
			ObjectName mxbeanNameServiceReg = new ObjectName("org.serviceconnector.registry:type=ServiceRegistry");
			ObjectName mxbeanNameServerReg = new ObjectName("org.serviceconnector.registry:type=ServerRegistry");
			ObjectName mxbeanNameLoggingManager = new ObjectName("org.serviceconnector.logging:type=LoggingManager");

			// Register the Queue Sampler MXBean
			mbs.registerMBean(appContext.getSessionRegistry(), mxbeanNameSessReg);
			mbs.registerMBean(appContext.getServiceRegistry(), mxbeanNameServiceReg);
			mbs.registerMBean(appContext.getServerRegistry(), mxbeanNameServerReg);
			ILoggingManagerMXBean loggingManager = new JMXLoggingManager();
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
