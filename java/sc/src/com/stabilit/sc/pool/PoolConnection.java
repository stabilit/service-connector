package com.stabilit.sc.pool;

import java.io.IOException;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.msg.impl.AsyncCallMessage;
import com.stabilit.sc.msg.impl.SubscribeMessage;

abstract class PoolConnection implements IPoolConnection, ISubscribe {

	private boolean lendable;
	private boolean subscript;

	private IClientConnection con;

	public PoolConnection(IClientConnection con, Class<? extends IClientListener> scListener) {
		this.con = con;
		con.setDecorator(this);
		this.lendable = true;
		try {
			con.connect(scListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO wann muss dies geschehen!
	public void closeSession() throws IOException {
		this.con.deleteSession();
	}

	@Override
	public void destroy() throws Exception {
		this.con.destroy();
	}

	@Override
	public String getSessionId() {
		return this.con.getSessionId();
	}

	@Override
	public boolean isLendable() {
		return this.lendable;
	}

	@Override
	public void lend() {
		this.lendable = false;
	}

	@Override
	public void releaseConnection() {
		this.lendable = true;
	}

	// Wann aufrufen eventuell im constructor
	public void openSession() throws IOException {
		this.con.createSession();
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
	public String subscribe() throws Exception {
		this.subscript = true;
		SCMP request = new SCMP();
		SubscribeMessage subscribeMessage = new SubscribeMessage();
		request.setBody(subscribeMessage);
		SCMP result = con.sendAndReceive(request);
		String subscribeId = result.getSubscribeId();

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
	public void unsubscribe(String subscribeId) {
		// TODO unsubscribe client!
		return;
	}

	@Override
	public boolean continueSubscriptionOnConnection() {
		return subscript;
	}

	@Override
	public void stopSubscriptionActionOnConnection() {
		this.subscript = false;
	}

	@Override
	public abstract boolean isWritable();
}
