package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.ctx.IContext;

public class ConnectionContext implements IConnectionContext {

	private IContext outerContext;
	private IConnectionPool connectionPool;
	private IConnection connection;

	public ConnectionContext(IConnection connection, IConnectionPool connectionPool) {
		this.connection = connection;
		this.connectionPool = connectionPool;
		this.outerContext = null;
	}

	@Override
	public IContext getOuterContext() {
		return outerContext;
	}
	
	@Override
	public void setOuterContext(IContext outerContext) {
	   this.outerContext = outerContext;	
	}
	
	@Override
	public IConnection getConnection() {
		return this.connection;
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

	@Override
	public int getIdleTimeout() {
		return this.connectionPool.getKeepAliveInterval();
	}
	
	@Override
	public int getReadTimeout() {
		return IConstants.READ_TIMEOUT_MILLIS;
	}
	
	@Override
	public int getWriteTimeout() {
		return IConstants.WRITE_TIMEOUT_MILLIS;
	}
}
