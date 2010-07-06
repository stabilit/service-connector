package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IPublishContext;
import com.stabilit.scm.common.net.req.IConnectionPool;

public class PublishServiceContext implements IPublishContext {

	private IServiceConnectorContext serviceConnectorContext;
	private IPublishService publishService;

	public PublishServiceContext(
			IServiceConnectorContext serviceConnectorContext,
			IPublishService publishService) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.publishService = publishService;
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
	public IPublishService getPublishService() {
		return publishService;
	}
}
