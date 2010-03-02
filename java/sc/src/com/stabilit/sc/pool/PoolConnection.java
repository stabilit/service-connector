package com.stabilit.sc.pool;

import java.io.IOException;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ISCClientListener;
import com.stabilit.sc.msg.impl.AsyncCallMessage;
import com.stabilit.sc.msg.impl.SubscribeMessage;

class PoolConnection implements IPoolConnection, ISubscribe {

	private boolean available;
	private boolean connected;

	private IClientConnection con;
	private ISCClientListener callback;

	public PoolConnection(IClientConnection con, Class<? extends ISCClientListener> scListener) {
		this.con = con;
		con.setDecorator(this);
		this.available = true;
		this.connected = false;
		try {
			con.connect(scListener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.connected = true;
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
		callback.setSubscribeId(subscribeId);
		this.callback = callback;
		// if (con instanceof IConnectionCallback) {
		// ((IConnectionCallback) con).setCallback(callback);
		// } else {
		// throw new UnsupportedOperationException();
		// }
		// send initial async call
		request = new SCMP();
		request.setSubsribeId(subscribeId);
		request.setMessageId(AsyncCallMessage.ID);
		con.send(request);
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
