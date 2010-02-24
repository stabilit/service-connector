package com.stabilit.sc.pool;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.client.IConnectionCallback;
import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.job.impl.AsyncCallMessage;
import com.stabilit.sc.job.impl.SubscribeMessage;
import com.stabilit.sc.job.impl.UnSubscribeMessage;
import com.stabilit.sc.msg.ICallback;

public class PoolConnection implements IConnection, ISubscribe {

	private boolean available;
	private boolean connected;

	private IConnection con;
	private ICallback callback;

	public PoolConnection(IConnection con) {
		this.con = con;
		this.available = true;
		this.connected = false;

	}

	@Override
	public void closeSession() throws IOException {
		this.con.closeSession();
	}

	@Override
	public void connect() throws Exception {
		if (this.connected == false) {
			this.con.connect();
			this.connected = true;
		}
		this.available = false;
	}

	@Override
	public void destroy() throws Exception {
		this.con.destroy();
	}

	@Override
	public void disconnect() throws Exception {
		// connection pool ???
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

	@Override
	public void openSession() throws IOException {
		this.con.openSession();
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		this.con.send(scmp);
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		return this.con.sendAndReceive(scmp);
	}

	@Override
	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public void setEndpoint(URL url) {
		this.con.setEndpoint(url);
	}

	@Override
	public String subscribe(ICallback callback) throws Exception {
		SCMP request = new SCMP();
		SubscribeMessage subscribeMessage = new SubscribeMessage();
		request.setBody(subscribeMessage);
		SCMP result = con.sendAndReceive(request);
		String subscribeId = result.getSubscribeId();
		callback.setSubscribeId(subscribeId);
		this.callback = callback;
		if (con instanceof IConnectionCallback) {
			((IConnectionCallback) con).setCallback(callback);
		} else {
			throw new UnsupportedOperationException();
		}
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
