/*
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
 */
package org.serviceconnector.ctx;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.cache.CacheConfiguration;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cmd.FlyweightCommandFactory;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.RequesterConfiguration;
import org.serviceconnector.conf.ResponderConfiguration;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AppContext. The AppContext is singelton and holds all factories and registries. Its the top context in a service
 * connector, server or even in clients. Its a superset of the specific contexts and unifies the data.
 */
public final class AppContext {

	// indicates that AppContext is running in a SC environment
	/** The sc environment. */
	private static boolean scEnvironment = false;

	// communicator lock
	/** The communicators lock. */
	public static Object communicatorsLock = new Object();
	// current attached communicators
	/** The attached communicators. */
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
	private static ResponderConfiguration responderConfiguration = new ResponderConfiguration();
	/** The requester configuration. */
	private static RequesterConfiguration requesterConfiguration = new RequesterConfiguration();

	// Factories
	/** The command factory. */
	private static FlyweightCommandFactory commandFactory;
	
	/** The Constant responderRegistry. */
	private static final ResponderRegistry responderRegistry = new ResponderRegistry();
	
	/** The Constant connectionFactory. */
	private static final ConnectionFactory connectionFactory = new ConnectionFactory();
	
	/** The Constant endpointFactory. */
	private static final EndpointFactory endpointFactory = new EndpointFactory();
	
	/** The Constant frameDecoderFactory. */
	private static final FlyweightFrameDecoderFactory frameDecoderFactory = new FlyweightFrameDecoderFactory();
	
	/** The Constant encoderDecoderFactory. */
	private static final FlyweightEncoderDecoderFactory encoderDecoderFactory = new FlyweightEncoderDecoderFactory();

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
	private static final SCMPSessionCompositeRegistry scmpSessionCompositeRegistry = new SCMPSessionCompositeRegistry();

	// scmp cache
	/** The Constant cacheManager. */
	private static final CacheManager cacheManager = new CacheManager();

	// initialize configurations in every case
	static {
		AppContext.basicConfiguration = new BasicConfiguration();
		AppContext.cacheConfiguration = new CacheConfiguration();
		AppContext.responderConfiguration = new ResponderConfiguration();
		AppContext.requesterConfiguration = new RequesterConfiguration();
		init();
	}

	/**
	 * Instantiates a new AppContext. Singelton.
	 */
	private AppContext() {
	}

	/**
	 * Inits the commands.
	 *
	 * @param commandFactory the command factory
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
		return AppContext.connectionFactory;
	}

	/**
	 * Gets the encoder decoder factory.
	 *
	 * @return the encoder decoder factory
	 */
	public static FlyweightEncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.encoderDecoderFactory;
	}

	/**
	 * Gets the frame decoder factory.
	 *
	 * @return the frame decoder factory
	 */
	public static FlyweightFrameDecoderFactory getFrameDecoderFactory() {
		return AppContext.frameDecoderFactory;
	}

	/**
	 * Gets the endpoint factory.
	 *
	 * @return the endpoint factory
	 */
	public static EndpointFactory getEndpointFactory() {
		return AppContext.endpointFactory;
	}

	/**
	 * Gets the responder registry.
	 *
	 * @return the responder registry
	 */
	public static ResponderRegistry getResponderRegistry() {
		return AppContext.responderRegistry;
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
	 * Gets the sCMP session composite registry.
	 *
	 * @return the sCMP session composite registry
	 */
	public static SCMPSessionCompositeRegistry getSCMPSessionCompositeRegistry() {
		return AppContext.scmpSessionCompositeRegistry;
	}

	/**
	 * Gets the cache manager.
	 *
	 * @return the cache manager
	 */
	public static CacheManager getCacheManager() {
		return AppContext.cacheManager;
	}

	/**
	 * Inits the configuration.
	 *
	 * @param configFile the config file
	 * @throws Exception the exception
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
		AppContext.basicConfiguration.init(AppContext.apacheCompositeConfig);
		AppContext.cacheConfiguration.init(AppContext.apacheCompositeConfig);
		AppContext.responderConfiguration.init(AppContext.apacheCompositeConfig);
		AppContext.requesterConfiguration.init(AppContext.apacheCompositeConfig);
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
	public static ResponderConfiguration getResponderConfiguration() {
		return responderConfiguration;
	}

	/**
	 * Gets the requester configuration.
	 *
	 * @return the requester configuration
	 */
	public static RequesterConfiguration getRequesterConfiguration() {
		return requesterConfiguration;
	}

	/**
	 * Sets the sC environment.
	 *
	 * @param scEnvironment the new sC environment
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
	 * Inits the.
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
		}
	}

	/**
	 * Destroy.
	 */
	public static void destroy() {
		synchronized (AppContext.communicatorsLock) {
			// got lock to complete destroy
			if (attachedCommunicators.get() == 0) {
				// no communicator active shutdown thread pool
				ConnectionFactory.shutdownConnectionFactory();
				if (AppContext.otiScheduler != null && AppContext.isScEnvironment() == false) {
					// cancel operation timeout thread
					AppContext.otiScheduler.shutdownNow();
					AppContext.otiScheduler = null;
				}
				if (AppContext.eciScheduler != null) {
					AppContext.eciScheduler.shutdownNow();
					AppContext.eciScheduler = null;
				}
			}
		}
	}
}