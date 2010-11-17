package org.serviceconnector.ctx;

import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cmd.FlyweightCommandFactory;
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

/**
 * The Class AppContext. The AppContext is singelton and holds all factories and registries. Its the top context in a
 * service connector, server or even in clients. Its a superset of the specific contexts and unifies the data.
 */
public class AppContext {

	private static final AppContext instance = new AppContext();

	// configuration context
	private static ConfigurationContext configurationContext = ConfigurationContext.getCurrentContext();
	
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
	/**
	 * Instantiates a new AppContext. Singelton.
	 */
	private AppContext() {
	}

	public void initContext(FlyweightCommandFactory commandFactory) {
		if (AppContext.commandFactory != null) {
			// set only one time
			return;
		}
		AppContext.commandFactory = commandFactory;
	}

	public static AppContext getCurrentContext() {
		return AppContext.instance;
	}

	public static ConfigurationContext getConfigurationContext() {
		return AppContext.configurationContext;
	}
	
	public FlyweightCommandFactory getCommandFactory() {
		return AppContext.commandFactory;
	}

	public ConnectionFactory getConnectionFactory() {
		return AppContext.connectionFactory;
	}

	public FlyweightEncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.encoderDecoderFactory;
	}

	public FlyweightFrameDecoderFactory getFrameDecoderFactory() {
		return AppContext.frameDecoderFactory;
	}

	public EndpointFactory getEndpointFactory() {
		return AppContext.endpointFactory;
	}

	public ResponderRegistry getResponderRegistry() {
		return AppContext.responderRegistry;
	}

	public SrvServiceRegistry getSrvServiceRegistry() {
		return AppContext.srvServiceRegistry;
	}

	public ServerRegistry getServerRegistry() {
		return AppContext.serverRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return AppContext.serviceRegistry;
	}

	public SessionRegistry getSessionRegistry() {
		return AppContext.sessionRegistry;
	}

	public SubscriptionRegistry getSubscriptionRegistry() {
		return AppContext.subscriptionRegistry;
	}

	public SCMPSessionCompositeRegistry getSCMPSessionCompositeRegistry() {
		return AppContext.scmpSessionCompositeRegistry;
	}
	
	public CacheManager getCacheManager() {
		return AppContext.cacheManager;
	}
	
}