package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.common.net.req.IConnectionPool;

public class ServiceContext implements IServiceContext {

	private IServiceConnectorContext serviceConnectorContext;
	private IService service;

	public ServiceContext(IServiceConnectorContext serviceConnectorContext, IService service) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.service = service;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.serviceConnectorContext.getConnectionPool();
	}

	@Override
	public IServiceConnector getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

	@Override
	public IService getService() {
		return service;
	}
}
