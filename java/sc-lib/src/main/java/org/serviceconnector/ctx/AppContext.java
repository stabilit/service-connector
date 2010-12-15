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

/**
 * The Class AppContext. The AppContext is singelton and holds all factories and registries. Its the top context in a service
 * connector, server or even in clients. Its a superset of the specific contexts and unifies the data.
 */
public final class AppContext {

	// indicates that AppContext is running in a SC environment
	private static boolean scEnvironment = false;

	// communicator lock
	public static Object communicatorsLock = new Object();
	// current attached communicators
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
	private static FlyweightCommandFactory commandFactory;
	private static final ResponderRegistry responderRegistry = new ResponderRegistry();
	private static final ConnectionFactory connectionFactory = new ConnectionFactory();
	private static final EndpointFactory endpointFactory = new EndpointFactory();
	private static final FlyweightFrameDecoderFactory frameDecoderFactory = new FlyweightFrameDecoderFactory();
	private static final FlyweightEncoderDecoderFactory encoderDecoderFactory = new FlyweightEncoderDecoderFactory();

	// Registries
	private static ServerRegistry serverRegistry = null;
	private static ServiceRegistry serviceRegistry = null;
	private static SessionRegistry sessionRegistry = null;
	private static SubscriptionRegistry subscriptionRegistry = null;
	private static SrvServiceRegistry srvServiceRegistry = new SrvServiceRegistry();
	private static final SCMPSessionCompositeRegistry scmpSessionCompositeRegistry = new SCMPSessionCompositeRegistry();

	// scmp cache
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

	public static void initCommands(FlyweightCommandFactory commandFactory) {
		if (AppContext.commandFactory != null) {
			// set only one time
			return;
		}
		AppContext.commandFactory = commandFactory;
	}

	public static FlyweightCommandFactory getCommandFactory() {
		return AppContext.commandFactory;
	}

	public static ConnectionFactory getConnectionFactory() {
		return AppContext.connectionFactory;
	}

	public static FlyweightEncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.encoderDecoderFactory;
	}

	public static FlyweightFrameDecoderFactory getFrameDecoderFactory() {
		return AppContext.frameDecoderFactory;
	}

	public static EndpointFactory getEndpointFactory() {
		return AppContext.endpointFactory;
	}

	public static ResponderRegistry getResponderRegistry() {
		return AppContext.responderRegistry;
	}

	public static SrvServiceRegistry getSrvServiceRegistry() {
		return AppContext.srvServiceRegistry;
	}

	public static ServerRegistry getServerRegistry() {
		return AppContext.serverRegistry;
	}

	public static ServiceRegistry getServiceRegistry() {
		return AppContext.serviceRegistry;
	}

	public static SessionRegistry getSessionRegistry() {
		return AppContext.sessionRegistry;
	}

	public static SubscriptionRegistry getSubscriptionRegistry() {
		return AppContext.subscriptionRegistry;
	}

	public static SCMPSessionCompositeRegistry getSCMPSessionCompositeRegistry() {
		return AppContext.scmpSessionCompositeRegistry;
	}

	public static CacheManager getCacheManager() {
		return AppContext.cacheManager;
	}

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

	public static CompositeConfiguration getApacheCompositeConfig() {
		return apacheCompositeConfig;
	}

	public static BasicConfiguration getBasicConfiguration() {
		return basicConfiguration;
	}

	public static CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

	public static ResponderConfiguration getResponderConfiguration() {
		return responderConfiguration;
	}

	public static RequesterConfiguration getRequesterConfiguration() {
		return requesterConfiguration;
	}

	public static void setSCEnvironment(boolean scEnvironment) {
		AppContext.scEnvironment = scEnvironment;
		if (AppContext.scEnvironment) {
			AppContext.serverRegistry = new ServerRegistry();
			AppContext.serviceRegistry = new ServiceRegistry();
			AppContext.sessionRegistry = new SessionRegistry();
			AppContext.subscriptionRegistry = new SubscriptionRegistry();
		}
	}

	public static boolean isScEnvironment() {
		return AppContext.scEnvironment;
	}

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

	public static void destroy() {
		synchronized (AppContext.communicatorsLock) {
			// got lock to complete destroy
			if (attachedCommunicators.get() == 0) {
				// no communicator active shutdown thread pool
				ConnectionFactory.shutdownConnectionFactory();
				if (AppContext.otiScheduler != null && AppContext.isScEnvironment() == false) {
					// cancel operation timeout thread
					AppContext.otiScheduler.shutdown();
					AppContext.otiScheduler = null;
				}
				if (AppContext.eciScheduler != null) {
					AppContext.eciScheduler.shutdown();
					AppContext.eciScheduler = null;
				}
			}
		}
	}
}