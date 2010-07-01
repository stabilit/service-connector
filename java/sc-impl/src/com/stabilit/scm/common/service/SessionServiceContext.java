package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.ISessionContext;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.net.req.IConnectionPool;

public class SessionServiceContext implements ISessionContext {

	private IServiceConnectorContext serviceConnectorContext;
	private ISessionService sessionService;

	public SessionServiceContext(
			IServiceConnectorContext serviceConnectorContext,
			ISessionService sessionService) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.sessionService = sessionService;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.serviceConnectorContext.getConnectionPool();
	}

	@Override
	public ISessionService getSessionService() {
		return this.sessionService;
	}

	@Override
	public IServiceConnector getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

}
