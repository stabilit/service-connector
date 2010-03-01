package com.stabilit.sc.pool;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ISCClientListener;

public class BlockingPoolConnection extends PoolConnection {

	public BlockingPoolConnection(IClientConnection con, Class<? extends ISCClientListener> scListener) {
		super(con, scListener);
	}

	private boolean blocked;

	@Override
	public void send(SCMP scmp) throws Exception {
		if (blocked)
			new ConnectionException("Connection blocked, not possible to complete send.");
		blocked = true;
		super.send(scmp);
	}

	public void release() {
		blocked = false;
	}
}
