package com.stabilit.sc.pool;

import java.io.IOException;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.msg.impl.AsyncCallMessage;
import com.stabilit.sc.msg.impl.SubscribeMessage;

class PoolConnection implements IPoolConnection, ISubscribe {

	private boolean available;

	private IClientConnection con;
	//TODO This callback is not right implemented, attention!
	private IClientListener callback;

	public PoolConnection(IClientConnection con, Class<? extends IClientListener> scListener) {
		this.con = con;
		con.setDecorator(this);
		this.available = true;
		try {
			con.connect(scListener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODOD wann muss dies geschehen!
	public void closeSession() throws IOException {
		this.con.deleteSession();
	}

	@Override
	public void destroy() throws Exception {
		this.con.destroy();
	}

	@Override
	public void releaseConnection() {
		this.available = true;
	}

	@Override
	public String getSessionId() {
		return this.con.getSessionId();
	}

	@Override
	public boolean isAvailable() {
		return this.available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	// Wann aufrufen eventuell im constructor
	public void openSession() throws IOException {
		this.con.createSession();
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		this.available = false;
		this.con.send(scmp);
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		return this.con.sendAndReceive(scmp);
	}

	@Override
	public String subscribe() throws Exception {
		SCMP request = new SCMP();
		SubscribeMessage subscribeMessage = new SubscribeMessage();
		request.setBody(subscribeMessage);
		SCMP result = con.sendAndReceive(request);
		String subscribeId = result.getSubscribeId();
		//callback.setSubscribeId(subscribeId);
		//this.callback = callback;
		// if (con instanceof IConnectionCallback) {
		// ((IConnectionCallback) con).setCallback(callback);
		// } else {
		// throw new UnsupportedOperationException();
		// }
		// send initial async call
		SCMP req = new SCMP();
		req.setSubsribeId(subscribeId);
		req.setMessageId(AsyncCallMessage.ID);
		AsyncCallMessage async = new AsyncCallMessage();
		req.setBody(async);
		con.send(req);
		return subscribeId;
	}

	@Override
	public void unsubscribe(String subscribeId) throws Exception {
		if (this.callback != null) {
			this.callback.release();
		}
		return;
	}
}
