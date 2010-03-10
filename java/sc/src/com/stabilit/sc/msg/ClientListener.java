package com.stabilit.sc.msg;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.pool.BlockingPoolConnection;
import com.stabilit.sc.pool.IPoolConnection;

public abstract class ClientListener implements IClientListener {

	private String subscribeId = null;

	// important for instancing by .newInstance() method.
	public ClientListener() {
	}

	@Override
	public String getSubscribeId() {
		return this.subscribeId;
	}

	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}

	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		if (conn instanceof BlockingPoolConnection) {
			((BlockingPoolConnection) conn).setWritable(true);
		}
	}
}
