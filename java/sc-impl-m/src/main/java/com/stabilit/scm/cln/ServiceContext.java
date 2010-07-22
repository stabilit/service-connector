package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCContext;

public class ServiceContext implements IServiceContext {

	private ISCContext serviceConnectorContext;
	private Service service;

	public ServiceContext(ISCContext serviceConnectorContext, Service service) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.service = service;
	}

	@Override
	public ISC getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

	@Override
	public Service getService() {
		return service;
	}
}
