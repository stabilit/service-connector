package com.stabilit.sc.msg;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.pool.IPoolConnection;

public interface ISCListener {

	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception;
	
	public void setConnection(IPoolConnection conn);

	public String getSubscribeId();
	
	public void setSubscribeId(String subscribeId);

	public void sendAsyncRequest() throws Exception;
	
	public void release();
	
	public boolean isReleased();
}
