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

import org.apache.commons.configuration.CompositeConfiguration;
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
	private static final SrvServiceRegistry srvServiceRegistry = new SrvServiceRegistry();
	private static final ServerRegistry serverRegistry = new ServerRegistry();
	private static final ServiceRegistry serviceRegistry = new ServiceRegistry();
	private static final SessionRegistry sessionRegistry = new SessionRegistry();
	private static final SubscriptionRegistry subscriptionRegistry = new SubscriptionRegistry();
	private static final SCMPSessionCompositeRegistry scmpSessionCompositeRegistry = new SCMPSessionCompositeRegistry();

	// scmp cache
	private static final CacheManager cacheManager = new CacheManager();

	// initialize configurations in every case
	static {
		AppContext.basicConfiguration = new BasicConfiguration();
		AppContext.cacheConfiguration = new CacheConfiguration();
		AppContext.responderConfiguration = new ResponderConfiguration();
		AppContext.requesterConfiguration = new RequesterConfiguration();
	}

	/**
	 * Instantiates a new AppContext. Singelton.
	 */
	private AppContext() {
	}

	public static void initContext(FlyweightCommandFactory commandFactory) {
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
		try {
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
}