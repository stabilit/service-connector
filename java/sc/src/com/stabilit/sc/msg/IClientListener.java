package com.stabilit.sc.msg;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.pool.IPoolConnection;

public interface IClientListener {

	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception;

	public String getSubscribeId();

	public void setSubscribeId(String subscribeId);
}
