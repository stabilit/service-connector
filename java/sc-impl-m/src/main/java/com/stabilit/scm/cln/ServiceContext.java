package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCContext;

public class ServiceContext implements IServiceContext {

	private ISCContext serviceConnectorContext;
	private IService service;

	public ServiceContext(ISCContext serviceConnectorContext, IService service) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.service = service;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.serviceConnectorContext.getConnectionPool();
	}

	@Override
	public ISC getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

	@Override
	public IService getService() {
		return service;
	}
}
