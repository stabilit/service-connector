package org.serviceconnector.ctx;

import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;

public class ServiceConnectorContext extends AppContext {

	private static final ServerRegistry serverRegistry = new ServerRegistry();
	private static final ServiceRegistry serviceRegistry = new ServiceRegistry();
	private static final SessionRegistry sessionRegistry = new SessionRegistry();
	private static final SubscriptionRegistry subscriptionRegistry = new SubscriptionRegistry();

	private ServiceConnectorContext() {
	}

	public static ServiceConnectorContext getCurrentContext() {
		if (AppContext.instance == null) {
			AppContext.instance = new ServiceConnectorContext();
		}
		return (ServiceConnectorContext) AppContext.instance;
	}

	public ServerRegistry getServerRegistry() {
		return ServiceConnectorContext.serverRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return ServiceConnectorContext.serviceRegistry;
	}

	public SessionRegistry getSessionRegistry() {
		return ServiceConnectorContext.sessionRegistry;
	}

	public SubscriptionRegistry getSubscriptionRegistry() {
		return ServiceConnectorContext.subscriptionRegistry;
	}
}
