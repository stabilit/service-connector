package com.stabilit.sc.msg;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.AsyncCallMessage;
import com.stabilit.sc.msg.impl.UnSubscribeMessage;
import com.stabilit.sc.pool.BlockingPoolConnection;
import com.stabilit.sc.pool.IPoolConnection;

public abstract class SCListener implements ISCListener {
	private IPoolConnection conn = null;
	private String subscribeId = null;
	private boolean released = false;

	//important for instancing by .newInstance() method.
	public SCListener() {
	}
	
	@Override
	public void setConnection(IPoolConnection conn) {
		this.conn = conn;		
	}
	
	@Override
	public String getSubscribeId() {
		return this.subscribeId;
	}
	
	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}

	@Override
	public void sendAsyncRequest() throws Exception {
		if (this.isReleased()) {
			SCMP request = new SCMP();
			request.setSubsribeId(this.subscribeId);
			UnSubscribeMessage unsubscribeMessage = new UnSubscribeMessage();
			request.setBody(unsubscribeMessage);
			System.out.println("Callback.sendAsyncRequest() send unsubscribe request");
			conn.send(request);
			return;
		}		
		SCMP request = new SCMP();
		request.setSubsribeId(this.subscribeId);
		request.setMessageId(AsyncCallMessage.ID);
		conn.send(request);
	}

	@Override
	public boolean isReleased() {
		return released;
	}
	
	@Override
	public void release() {
	    this.released = true;	
	}

	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		if(conn instanceof BlockingPoolConnection) {
			((BlockingPoolConnection) conn).release();
		}		
	}
}
