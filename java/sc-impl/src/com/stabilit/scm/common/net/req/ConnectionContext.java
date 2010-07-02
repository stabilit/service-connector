package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;

public class ConnectionContext implements IConnectionContext {

	private IContext outerContext;
	private IConnection connection;

	public ConnectionContext(IContext outerContext,
			IConnection connection) {
		this.connection = connection;
		this.outerContext = outerContext;
	}

	@Override
	public IContext getOuterContext() {
		return outerContext;
	}
	
	@Override
	public IConnection getConnection() {
		return this.connection;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.outerContext.getConnectionPool();
	}

}
