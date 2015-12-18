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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.cache.SCCache;
import org.serviceconnector.cmd.FlyweightCommandFactory;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.ListenerListConfiguration;
import org.serviceconnector.conf.RemoteNodeListConfiguration;
import org.serviceconnector.conf.SCCacheConfiguration;
import org.serviceconnector.conf.ServiceListConfiguration;
import org.serviceconnector.net.FlyweightEncoderDecoderFactory;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.res.EndpointFactory;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.registry.CacheModuleRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.NamedPriorityThreadFactory;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class AppContext. The AppContext is singleton and holds all factories and registries. Its the top context in a service
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
	/** The executer observes timeouts of services(ECI)/servers(CRI). */
	public static ScheduledThreadPoolExecutor eci_cri_Scheduler;

	// configurations
	/** The composite configuration. */
	private static CompositeConfiguration apacheCompositeConfig;
	/** The basic configuration. */
	private static BasicConfiguration basicConfiguration = new BasicConfiguration();
	/** The cache configuration. */
	private static SCCacheConfiguration scCacheConfiguration = new SCCacheConfiguration();
	/** The responder configuration. */
	private static ListenerListConfiguration responderConfiguration = new ListenerListConfiguration();
	/** The requester configuration. */
	private static RemoteNodeListConfiguration requesterConfiguration = new RemoteNodeListConfiguration();
	/** The service configuration. */
	private static ServiceListConfiguration serviceConfiguration = new ServiceListConfiguration();

	// factories
	/** The command factory. */
	private static FlyweightCommandFactory commandFactory;
	/** The Constant responderRegistry. */
	private static final ResponderRegistry RESPONDER_REGISTRY = new ResponderRegistry();
	/** The Constant connectionFactory. */
	private static final ConnectionFactory CONNECTION_FACTORY = new ConnectionFactory();
	/** The Constant endpointFactory. */
	private static final EndpointFactory ENDPOINT_FACTORY = new EndpointFactory();
	/** The Constant encoderDecoderFactory. */
	private static final FlyweightEncoderDecoderFactory ENCODER_DECODER_FACTORY = new FlyweightEncoderDecoderFactory();

	// registries
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
	/** The Constant SC_CACHE. */
	private static final SCCache SC_CACHE = new SCCache();
	/** The cache modules registry. */
	private static CacheModuleRegistry cacheModuleRegistry = null;

	/**
	 * The executor to submit runnable objects. Provides threads for handling NETTY events and processing AJAX requests from web UI.
	 */
	private static ExecutorService scWorkerThreadPool;
	/**
	 * The executor to submit runnable objects for Proxy handler of Netty. Ordered pool needed because of pipe logic.
	 */
	private static ExecutorService orderedSCWorkerThreadPool;

	// initialize configurations in every case
	static {
		// configures NETTY logging to use log4j framework
		InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
		AppContext.basicConfiguration = new BasicConfiguration();
		AppContext.scCacheConfiguration = new SCCacheConfiguration();
		AppContext.responderConfiguration = new ListenerListConfiguration();
		AppContext.requesterConfiguration = new RemoteNodeListConfiguration();
		init();
	}

	/**
	 * Instantiates a new AppContext. Singleton.
	 */
	private AppContext() {
	}

	/**
	 * Initialize the application context.
	 */
	public static void init() {
		synchronized (AppContext.communicatorsLock) {
			ConnectionFactory.init();
			if (AppContext.otiScheduler == null) {
				// set up new scheduler with high priority threads
				AppContext.otiScheduler = new ScheduledThreadPoolExecutor(1, new NamedPriorityThreadFactory("OTI",
						Thread.MAX_PRIORITY));
			}
			if (AppContext.eci_cri_Scheduler == null) {
				// set up new scheduler with high priority threads
				AppContext.eci_cri_Scheduler = new ScheduledThreadPoolExecutor(1, new NamedPriorityThreadFactory("ECI_CRI",
						Thread.MAX_PRIORITY));
			}
			if (AppContext.scWorkerThreadPool == null) {
				AppContext.scWorkerThreadPool = Executors.newCachedThreadPool(new NamedPriorityThreadFactory("SC_WORKER"));
			}

			if (AppContext.orderedSCWorkerThreadPool == null) {
				AppContext.orderedSCWorkerThreadPool = new OrderedMemoryAwareThreadPoolExecutor(
						Constants.DEFAULT_MAX_ORDERED_IO_THREADS, 0, 0, 10, TimeUnit.SECONDS, new NamedPriorityThreadFactory(
								"ORDERED_SC_WORKER"));
			}
		}
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
	 * Gets the cache modules registry.
	 * 
	 * @return the cache modules registry
	 */
	public static CacheModuleRegistry getCacheModuleRegistry() {
		return AppContext.cacheModuleRegistry;
	}

	/**
	 * Gets the SC cache.
	 * 
	 * @return the SC cache
	 */
	public static SCCache getSCCache() {
		return AppContext.SC_CACHE;
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
	 * Gets the SC cache configuration.
	 * 
	 * @return the SC cache configuration
	 */
	public static SCCacheConfiguration getSCCacheConfiguration() {
		return scCacheConfiguration;
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
			AppContext.cacheModuleRegistry = new CacheModuleRegistry();
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
	 * Gets the SC worker thread pool.
	 * 
	 * @return the SC worker thread pool
	 */
	public static ExecutorService getSCWorkerThreadPool() {
		return AppContext.scWorkerThreadPool;
	}

	/**
	 * Gets the ordered SC worker thread pool.
	 * 
	 * @return the ordered SC worker thread pool
	 */
	public static ExecutorService getOrderedSCWorkerThreadPool() {
		return AppContext.orderedSCWorkerThreadPool;
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
				if (AppContext.eci_cri_Scheduler != null) {
					AppContext.eci_cri_Scheduler.shutdownNow();
					AppContext.eci_cri_Scheduler = null;
				}
				if (AppContext.scWorkerThreadPool != null) {
					AppContext.scWorkerThreadPool.shutdownNow();
					AppContext.scWorkerThreadPool = null;
				}
				if (AppContext.orderedSCWorkerThreadPool != null) {
					AppContext.orderedSCWorkerThreadPool.shutdownNow();
					AppContext.orderedSCWorkerThreadPool = null;
				}
			} else {
				LOGGER.debug("resources can not be released - pending communicators active");
			}
		}
	}

	/**
	 * Initialize after configuration load. Some things we need to set up new. Loaded configuration may have affects.
	 */
	public static void initAfterConfigurationLoad() {
		// Set up connection factory again
		ConnectionFactory.shutdownConnectionFactory();
		ConnectionFactory.init();
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
				writer.writeComment(" *************** APPCONTEXT INFOS ************ ");
				AppContext.dumpAppContextInfos(writer);
				writer.writeComment(" *************** CONFIGURATION *************** ");
				AppContext.getBasicConfiguration().dump(writer);
				writer.writeComment(" *************** RESPONDERS ****************** ");
				AppContext.getResponderRegistry().dump(writer);
				writer.writeComment(" *************** SERVICES ******************** ");
				AppContext.getServiceRegistry().dump(writer);
				writer.writeComment(" *************** SESSIONS ******************** ");
				AppContext.getSessionRegistry().dump(writer);
				writer.writeComment(" *************** SUBSCRIPTIONS *************** ");
				AppContext.getSubscriptionRegistry().dump(writer);
				writer.writeComment(" *************** SC CACHE ******************** ");
				AppContext.getSCCache().dump(writer);
				writer.writeComment(" *************** CACHE MODULES *************** ");
				AppContext.getCacheModuleRegistry().dump(writer);
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

	/**
	 * Dump app context infos.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	private static void dumpAppContextInfos(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("sc-worker-threadpool");
		if (AppContext.scWorkerThreadPool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor threadPoolEx = (ThreadPoolExecutor) AppContext.scWorkerThreadPool;
			writer.writeAttribute("scworker_poolSize", threadPoolEx.getPoolSize());
			writer.writeAttribute("scworker_maximumPoolSize", threadPoolEx.getMaximumPoolSize());
			writer.writeAttribute("scworker_corePoolSize", threadPoolEx.getCorePoolSize());
			writer.writeAttribute("scworker_largestPoolSize", threadPoolEx.getLargestPoolSize());
			writer.writeAttribute("scworker_activeCount", threadPoolEx.getActiveCount());
		}
		writer.writeEndElement(); // end of sc-worker-threadpool

		writer.writeStartElement("ordered-sc-worker-threadpool");
		if (AppContext.orderedSCWorkerThreadPool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor threadPoolEx = (ThreadPoolExecutor) AppContext.orderedSCWorkerThreadPool;
			writer.writeAttribute("orderedscworker_poolSize", threadPoolEx.getPoolSize());
			writer.writeAttribute("orderedscworker_maximumPoolSize", threadPoolEx.getMaximumPoolSize());
			writer.writeAttribute("orderedscworker_corePoolSize", threadPoolEx.getCorePoolSize());
			writer.writeAttribute("orderedscworker_largestPoolSize", threadPoolEx.getLargestPoolSize());
			writer.writeAttribute("orderedscworker_activeCount", threadPoolEx.getActiveCount());
		}
		writer.writeEndElement(); // end of ordered-sc-worker-threadpool
	}
}