package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.srv.IIdleCallback;

public class ConnectionContext implements IConnectionContext {

	private IConnection connection;
	private int idleTimeout;
	private IIdleCallback idleCallback;

	public ConnectionContext(IConnection connection, IIdleCallback idleCallback, int idleTimeout) {
		this.connection = connection;
		this.idleTimeout = idleTimeout;
		this.idleCallback = idleCallback;
	}

	@Override
	public IConnection getConnection() {
		return this.connection;
	}

	@Override
	public int getIdleTimeout() {
		return this.idleTimeout;
	}

	@Override
	public int getOperationTimeoutMillis() {
		// operation timeout & observation is used to detect operation timeout
		return Constants.getIdleTimeoutMillis();
	}

	@Override
	public IIdleCallback getIdleCallback() {
		return this.idleCallback;
	}
}
