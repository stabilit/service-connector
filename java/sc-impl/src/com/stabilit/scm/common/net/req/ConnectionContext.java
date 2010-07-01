package com.stabilit.scm.common.net.req;

public class ConnectionContext implements IConnectionContext {

	private IConnectionPool connectionPool;
	private IConnection connection;

	public ConnectionContext(IConnectionPool connectionPool,
			IConnection connection) {
		this.connection = connection;
		this.connectionPool = connectionPool;
	}

	@Override
	public IConnection getConnection() {
		return this.connection;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

}
