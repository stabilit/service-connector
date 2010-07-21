package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.conf.IConstants;
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
	public int getReadTimeout() {
		return IConstants.READ_TIMEOUT_MILLIS;
	}
	
	@Override
	public int getWriteTimeout() {
		return IConstants.WRITE_TIMEOUT_MILLIS;
	}

	@Override
	public IIdleCallback getIdleCallback() {
		return this.idleCallback;
	}
}
