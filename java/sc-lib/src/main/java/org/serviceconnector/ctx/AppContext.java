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
package org.serviceconnector.ctx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cmd.FlyweightCommandFactory;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.CacheConfiguration;
import org.serviceconnector.conf.ListenerListConfiguration;
import org.serviceconnector.conf.RemoteNodeListConfiguration;
import org.serviceconnector.conf.ServiceListConfiguration;
import org.serviceconnector.net.FlyweightEncoderDecoderFactory;
import org.serviceconnector.net.FlyweightFrameDecoderFactory;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.res.EndpointFactory;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class AppContext. The AppContext is singelton and holds all factories and registries. Its the top context in a service
 * connector, server or even in clients. Its a superset of the specific contexts and unifies the data.
 */
public final class AppContext {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(AppContext.class);

	/** The Constant DUMP_FILE_SDF. */
	private static final SimpleDateFormat DUMP_FILE_SDF = new SimpleDateFormat(Constants.DUMP_FILE_NAME_FORMAT);
	/** The SC environment. Indicates that AppContext is running in a SC environment */
	private static boolean scEnvironment = false;

	/** The communicators lock. communicator lock. */
	public static Object communicatorsLock = new Object();
	/** The attached communicators. current attached communicators. */
	public static AtomicInteger attachedCommunicators = new AtomicInteger();
	/** The executer, triggers all operation timeout for sending. */
	public static ScheduledThreadPoolExecutor otiScheduler;
	/** The executer observes the session timeout of service. */
	public static ScheduledThreadPoolExecutor eciScheduler;

	// configurations
	/** The composite configuration. */
	private static CompositeConfiguration apacheCompositeConfig;
	/** The basic configuration. */
	private static BasicConfiguration basicConfiguration = new BasicConfiguration();
	/** The cache configuration. */
	private static CacheConfiguration cacheConfiguration = new CacheConfiguration();
	/** The responder configuration. */
	private static ListenerListConfiguration responderConfiguration = new ListenerListConfiguration();
	/** The requester configuration. */
	private static RemoteNodeListConfiguration requesterConfiguration = new RemoteNodeListConfiguration();
	/** The service configuration. */
	private static ServiceListConfiguration serviceConfiguration = new ServiceListConfiguration();

	// Factories
	/** The command factory. */
	private static FlyweightCommandFactory commandFactory;
	/** The Constant responderRegistry. */
	private static final ResponderRegistry RESPONDER_REGISTRY = new ResponderRegistry();
	/** The Constant connectionFactory. */
	private static final ConnectionFactory CONNECTION_FACTORY = new ConnectionFactory();
	/** The Constant endpointFactory. */
	private static final EndpointFactory ENDPOINT_FACTORY = new EndpointFactory();
	/** The Constant frameDecoderFactory. */
	private static final FlyweightFrameDecoderFactory FRAME_DECODER_FACTORY = new FlyweightFrameDecoderFactory();
	/** The Constant encoderDecoderFactory. */
	private static final FlyweightEncoderDecoderFactory ENCODER_DECODER_FACTORY = new FlyweightEncoderDecoderFactory();

	// Registries
	/** The server registry. */
	private static ServerRegistry serverRegistry = null;
	/** The service registry. */
	private static ServiceRegistry serviceRegistry = null;
	/** The session registry. */
	private static SessionRegistry sessionRegistry = null;
	/** The subscription registry. */
	private static SubscriptionRegistry subscriptionRegistry = null;
	/** The srv service registry. */
	private static SrvServiceRegistry srvServiceRegistry = new SrvServiceRegistry();
	/** The Constant scmpSessionCompositeRegistry. */
	private static final SCMPSessionCompositeRegistry SCMP_COMPOSITE_REGISTRY = new SCMPSessionCompositeRegistry();
	/** The Constant cacheManager. */
	private static final CacheManager CACHE_MANAGER = new CacheManager();
	/** The executor to submit runnable objects. */
	private static ExecutorService executor; // TODO JOT which runnable objects are managed by this executor?

	// initialize configurations in every case
	static {
		// configures NETTY logging to use log4j framework
		InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
		AppContext.basicConfiguration = new BasicConfiguration();
		AppContext.cacheConfiguration = new CacheConfiguration();
		AppContext.responderConfiguration = new ListenerListConfiguration();
		AppContext.requesterConfiguration = new RemoteNodeListConfiguration();
		init();
	}

	/**
	 * Instantiates a new AppContext. Singelton.
	 */
	private AppContext() {
	}

	/**
	 * Initializes the commands.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public static void initCommands(FlyweightCommandFactory commandFactory) {
		if (AppContext.commandFactory != null) {
			// set only one time
			return;
		}
		AppContext.commandFactory = commandFactory;
	}

	/**
	 * Gets the command factory.
	 * 
	 * @return the command factory
	 */
	public static FlyweightCommandFactory getCommandFactory() {
		return AppContext.commandFactory;
	}

	/**
	 * Gets the connection factory.
	 * 
	 * @return the connection factory
	 */
	public static ConnectionFactory getConnectionFactory() {
		return AppContext.CONNECTION_FACTORY;
	}

	/**
	 * Gets the encoder decoder factory.
	 * 
	 * @return the encoder decoder factory
	 */
	public static FlyweightEncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.ENCODER_DECODER_FACTORY;
	}

	/**
	 * Gets the frame decoder factory.
	 * 
	 * @return the frame decoder factory
	 */
	public static FlyweightFrameDecoderFactory getFrameDecoderFactory() {
		return AppContext.FRAME_DECODER_FACTORY;
	}

	/**
	 * Gets the endpoint factory.
	 * 
	 * @return the endpoint factory
	 */
	public static EndpointFactory getEndpointFactory() {
		return AppContext.ENDPOINT_FACTORY;
	}

	/**
	 * Gets the responder registry.
	 * 
	 * @return the responder registry
	 */
	public static ResponderRegistry getResponderRegistry() {
		return AppContext.RESPONDER_REGISTRY;
	}

	/**
	 * Gets the srv service registry.
	 * 
	 * @return the srv service registry
	 */
	public static SrvServiceRegistry getSrvServiceRegistry() {
		return AppContext.srvServiceRegistry;
	}

	/**
	 * Gets the server registry.
	 * 
	 * @return the server registry
	 */
	public static ServerRegistry getServerRegistry() {
		return AppContext.serverRegistry;
	}

	/**
	 * Gets the service registry.
	 * 
	 * @return the service registry
	 */
	public static ServiceRegistry getServiceRegistry() {
		return AppContext.serviceRegistry;
	}

	/**
	 * Gets the session registry.
	 * 
	 * @return the session registry
	 */
	public static SessionRegistry getSessionRegistry() {
		return AppContext.sessionRegistry;
	}

	/**
	 * Gets the subscription registry.
	 * 
	 * @return the subscription registry
	 */
	public static SubscriptionRegistry getSubscriptionRegistry() {
		return AppContext.subscriptionRegistry;
	}

	/**
	 * Gets the SCMP session composite registry.
	 * 
	 * @return the SCMP session composite registry
	 */
	public static SCMPSessionCompositeRegistry getSCMPSessionCompositeRegistry() {
		return AppContext.SCMP_COMPOSITE_REGISTRY;
	}

	/**
	 * Gets the cache manager.
	 * 
	 * @return the cache manager
	 */
	public static CacheManager getCacheManager() {
		return AppContext.CACHE_MANAGER;
	}

	/**
	 * Initializes the configuration.
	 * 
	 * @param configFile
	 *            the configuration file
	 * @throws Exception
	 *             the exception
	 */
	public static void initConfiguration(String configFile) throws Exception {
		AppContext.apacheCompositeConfig = new CompositeConfiguration();
		// system properties override every setting

		try {
			// add environment variables to configuration
			AppContext.apacheCompositeConfig.addConfiguration(new EnvironmentConfiguration());
			AppContext.apacheCompositeConfig.addConfiguration(new PropertiesConfiguration(configFile));
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.toString());
		}
	}

	/**
	 * Gets the apache composite config.
	 * 
	 * @return the apache composite config
	 */
	public static CompositeConfiguration getApacheCompositeConfig() {
		return apacheCompositeConfig;
	}

	/**
	 * Gets the basic configuration.
	 * 
	 * @return the basic configuration
	 */
	public static BasicConfiguration getBasicConfiguration() {
		return basicConfiguration;
	}

	/**
	 * Gets the cache configuration.
	 * 
	 * @return the cache configuration
	 */
	public static CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

	/**
	 * Gets the responder configuration.
	 * 
	 * @return the responder configuration
	 */
	public static ListenerListConfiguration getResponderConfiguration() {
		return responderConfiguration;
	}

	/**
	 * Gets the requester configuration.
	 * 
	 * @return the requester configuration
	 */
	public static RemoteNodeListConfiguration getRequesterConfiguration() {
		return requesterConfiguration;
	}

	/**
	 * Gets the service configuration.
	 * 
	 * @return the service configuration
	 */
	public static ServiceListConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}

	/**
	 * Sets the sC environment.
	 * 
	 * @param scEnvironment
	 *            the new sC environment
	 */
	public static void setSCEnvironment(boolean scEnvironment) {
		AppContext.scEnvironment = scEnvironment;
		if (AppContext.scEnvironment) {
			AppContext.serverRegistry = new ServerRegistry();
			AppContext.serviceRegistry = new ServiceRegistry();
			AppContext.sessionRegistry = new SessionRegistry();
			AppContext.subscriptionRegistry = new SubscriptionRegistry();
		}
	}

	/**
	 * Checks if is sc environment.
	 * 
	 * @return true, if is sc environment
	 */
	public static boolean isScEnvironment() {
		return AppContext.scEnvironment;
	}

	/**
	 * Gets the executor.
	 * 
	 * @return the executor
	 */
	public static ExecutorService getExecutor() {
		return AppContext.executor;
	}

	/**
	 * Initialize the application context.
	 */
	public static void init() {
		synchronized (AppContext.communicatorsLock) {
			ConnectionFactory.init();
			if (AppContext.otiScheduler == null) {
				AppContext.otiScheduler = new ScheduledThreadPoolExecutor(1);
			}
			if (AppContext.eciScheduler == null) {
				AppContext.eciScheduler = new ScheduledThreadPoolExecutor(1);
			}
			if (AppContext.executor == null) {
				AppContext.executor = Executors.newCachedThreadPool();
			}
		}
	}

	/**
	 * Destroy the whole application context and release resources.
	 */
	public static void destroy() {
		if (AppContext.isScEnvironment() == true) {
			// AppContext gets never destroyed in SC environment
			return;
		}
		synchronized (AppContext.communicatorsLock) {
			// got lock to complete destroy
			if (attachedCommunicators.get() == 0) {
				// no communicator active shutdown thread pool
				ConnectionFactory.shutdownConnectionFactory();
				if (AppContext.otiScheduler != null) {
					// cancel operation timeout thread
					AppContext.otiScheduler.shutdownNow();
					AppContext.otiScheduler = null;
				}
				if (AppContext.eciScheduler != null) {
					AppContext.eciScheduler.shutdownNow();
					AppContext.eciScheduler = null;
				}
				if (AppContext.executor != null) {
					AppContext.executor.shutdownNow();
					AppContext.executor = null;
				}
			} else {
				LOGGER.debug("resources can not be released - pending communicators active");
			}
		}
	}

	/**
	 * dumps the entire application context.
	 * 
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public static String dump() throws Exception {
		String dumpPath = AppContext.getBasicConfiguration().getDumpPath();
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		String fs = System.getProperty("file.separator");
		String dumpFileName = null;
		String dateTimeString = null;
		synchronized (DUMP_FILE_SDF) { // DUMP_FILE_SDF is not thread safe
			dateTimeString = DUMP_FILE_SDF.format(now);
			dumpFileName = Constants.DUMP_FILE_NAME + dateTimeString + Constants.DUMP_FILE_EXTENSION;
		}
		File dumpDir = new File(dumpPath);
		try {
			// create directory if non existent
			if (dumpDir.exists() == true || dumpDir.mkdirs()) {
				String dumpCacheFile = dumpDir + fs + dumpFileName;
				// create file
				FileOutputStream fos = new FileOutputStream(dumpDir + fs + dumpFileName);
				// create xml writer and start dump
				XMLDumpWriter writer = new XMLDumpWriter(fos);
				writer.startDocument();
				writer.writeStartElement("sc-dump");
				AppContext.getBasicConfiguration().dump(writer);
				AppContext.getResponderRegistry().dump(writer);
				AppContext.getServerRegistry().dump(writer);
				AppContext.getCacheManager().dump(writer);
				// end dump
				writer.writeEndElement(); // end of sc-dump
				writer.endDocument();
				fos.close();
				LOGGER.info("SC dump created into file=" + dumpCacheFile);
				return dumpFileName;
			} else {
				LOGGER.error("Creating SC dump file =" + dumpPath + " failed, can not create directory");
				throw new IOException("Creating SC dump file =" + dumpPath + " failed, can not create directory");
			}
		} catch (Exception e) {
			LOGGER.error("Creating SC dump file =" + dumpPath + " failed.", e);
			throw e;
		}
	}
}