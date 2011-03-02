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

import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.cmd.sc.ServiceConnectorCommandFactory;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ILoggingManagerMXBean;
import org.serviceconnector.log.JMXLoggingManager;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.server.ServerLoader;
import org.serviceconnector.service.ServiceLoader;
import org.serviceconnector.util.CommandLineUtil;
import org.serviceconnector.util.FileUtility;
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

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(SC.class);

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
		// check arguments
		if (args == null || args.length <= 0) {
			showError("no argumments");
			System.exit(1);
		}
		LOGGER.log(Level.OFF, ">>>");
		LOGGER.log(Level.OFF, "Service Connector " + SCVersion.CURRENT.toString() + " is starting ...");
		String configFileName = CommandLineUtil.getArg(args, Constants.CLI_CONFIG_ARG);
		try {
			SC.run(configFileName);
		} catch (Exception ex) {
			LOGGER.fatal(ex.getMessage(), ex);
			showError(ex.toString());
			System.exit(1);
		}
	}

	/**
	 * Run SC responders.
	 * 
	 * @param configFileName
	 *            the config file name
	 * @throws Exception
	 *             the exception
	 */
	private static void run(String configFileName) throws Exception {
		if (configFileName == null) {
			throw new InvalidParameterException("Configuration file is missing");
		}
		// write system information to log
		SystemInfo.setConfigFileName(configFileName);
		SC.writeSystemInfoToLog();

		// indicates that AppContext is running in a SC environment
		AppContext.setSCEnvironment(true);
		AppContext.initConfiguration(configFileName);
		AppContext.getBasicConfiguration().load(AppContext.getApacheCompositeConfig());
		AppContext.getCacheConfiguration().load(AppContext.getApacheCompositeConfig());
		AppContext.getRequesterConfiguration().load(AppContext.getApacheCompositeConfig());
		AppContext.getResponderConfiguration().load(AppContext.getApacheCompositeConfig(), AppContext.getRequesterConfiguration());
		AppContext.getServiceConfiguration().load(AppContext.getApacheCompositeConfig());
		// load servers
		ServerLoader.load(AppContext.getRequesterConfiguration());
		// load services
		ServiceLoader.load(AppContext.getServiceConfiguration());
		// load cache configuration in cache manager after service are loaded
		AppContext.getCacheManager().load(AppContext.getCacheConfiguration());

		// Initialize service connector command factory
		AppContext.initCommands(new ServiceConnectorCommandFactory());
		// Initialize web command factory
		WebContext.getWebConfiguration().load(AppContext.getApacheCompositeConfig());
		WebContext.initCommands(new ServiceConnectorWebCommandFactory());
		// initialize JMX
		SC.initializeJMX();

		// initialize statistics
		Statistics statistics = Statistics.getInstance();
		statistics.setStartupDateTime(new Timestamp(Calendar.getInstance().getTime().getTime()));

		// start configured responders
		Map<String, ListenerConfiguration> listenerList = AppContext.getResponderConfiguration().getListenerConfigurations();
		for (ListenerConfiguration listenerConfig : listenerList.values()) {
			IResponder responder = new Responder(listenerConfig);
			responder.create();
			responder.startListenAsync();
		}
		// Write PID file
		if (AppContext.getBasicConfiguration().isWritePID()) {
			String fs = System.getProperty("file.separator");
			FileLock pidLock = FileUtility.createPIDfileAndLock(AppContext.getBasicConfiguration().getPidPath() + fs
					+ Constants.PID_FILE_NAME);
			SC.addExitHandler(pidLock);
		}
		LOGGER.log(Level.OFF, "Service Connector is running ...");
	}

	/**
	 * Initialize java management interface stuff.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void initializeJMX() throws Exception {
		// Necessary to make access for JMX client available
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mxbeanNameSubReg = new ObjectName("org.serviceconnector.registry:type=SubscriptionRegistry");
		ObjectName mxbeanNameSessReg = new ObjectName("org.serviceconnector.registry:type=SessionRegistry");
		ObjectName mxbeanNameServiceReg = new ObjectName("org.serviceconnector.registry:type=ServiceRegistry");
		ObjectName mxbeanNameServerReg = new ObjectName("org.serviceconnector.registry:type=ServerRegistry");
		ObjectName mxbeanNameLoggingManager = new ObjectName("org.serviceconnector.logging:type=LoggingManager");

		// Register the Queue Sampler MXBean
		mbs.registerMBean(AppContext.getSubscriptionRegistry(), mxbeanNameSubReg);
		mbs.registerMBean(AppContext.getSessionRegistry(), mxbeanNameSessReg);
		mbs.registerMBean(AppContext.getServiceRegistry(), mxbeanNameServiceReg);
		mbs.registerMBean(AppContext.getServerRegistry(), mxbeanNameServerReg);
		ILoggingManagerMXBean loggingManager = new JMXLoggingManager();
		mbs.registerMBean(loggingManager, mxbeanNameLoggingManager);
	}

	/**
	 * Write system info to log.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void writeSystemInfoToLog() throws Exception {
		LOGGER.info("SC configuration=" + SystemInfo.getConfigFileName());
		LOGGER.info("Java version=" + SystemInfo.getJavaVersion());
		LOGGER.info("VM version=" + SystemInfo.getVmVersion());
		LOGGER.info("Local host=" + SystemInfo.getLocalHostId());
		LOGGER.info("OS=" + SystemInfo.getOs());
		LOGGER.info("OS patch level=" + SystemInfo.getOsPatchLevel());
		LOGGER.info("CPU type=" + SystemInfo.getCpuType());
		LOGGER.info("User dir=" + SystemInfo.getUserDir());
		LOGGER.info("Country setting=" + SystemInfo.getCountrySetting());
		LOGGER.info("User timezone=" + SystemInfo.getUserTimezone());
		LOGGER.info("UTC Offset=" + SystemInfo.getUtcOffset());
		LOGGER.info("Startup date=" + SystemInfo.getStartupDateTime());
		LOGGER.info("Available processors=" + SystemInfo.getAvailableProcessors());
		LOGGER.info("Max memory=" + SystemInfo.getMaxMemory());
		LOGGER.info("Free memory=" + SystemInfo.getFreeMemory());
		LOGGER.info("Total memory=" + SystemInfo.getTotalMemory());
		LOGGER.info("Available disk memory=" + SystemInfo.getAvailableDiskSpace());
	}

	private static void showError(String msg) {
		System.err.println("error: " + msg);
		System.out.println("\nusage  : java -jar sc.jar -config <sc.properties file>");
		System.out.println("\nsamples: java -jar sc.jar -config sc.properties");
	}

	/**
	 * Adds the shutdown hook.
	 */
	private static void addExitHandler(FileLock pidLock) {
		Runtime.getRuntime().addShutdownHook(new SCExitHandler(pidLock));
	}

	/**
	 * The Class SCExitHandler.
	 */
	private static class SCExitHandler extends Thread {

		private FileLock pidLock;

		public SCExitHandler(FileLock pidLock) {
			this.pidLock = pidLock;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			String fs = System.getProperty("file.separator");
			AppContext.getCacheManager().destroy();
			try {
				if (this.pidLock != null) {
					// release the file lock
					this.pidLock.release();
				}
				if (AppContext.getBasicConfiguration() != null) {
					String pidFileNameFull = AppContext.getBasicConfiguration().getPidPath() + fs + Constants.PID_FILE_NAME;
					FileUtility.deleteFile(pidFileNameFull);
					LOGGER.info("Delete PID-file=" + pidFileNameFull);
				}
			} catch (Exception e) {
			}
			LOGGER.log(Level.OFF, "Service Connector exit");
			LOGGER.log(Level.OFF, "<<<");
		}
	}
}
