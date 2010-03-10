package com.stabilit.sc.pool;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;

public class BlockingPoolConnection extends PoolConnection {

	private boolean writable;

	public BlockingPoolConnection(IClientConnection con, Class<? extends IClientListener> scListener) {
		super(con, scListener);
		writable = true;
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		if (writable)
			new ConnectionException("Connection blocked, not possible to complete send.");
		writable = true;
		super.send(scmp);
	}

	public boolean isWritable() {
		return this.writable;
	}

	@Override
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
}
