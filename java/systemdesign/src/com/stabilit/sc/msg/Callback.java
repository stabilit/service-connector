package com.stabilit.sc.msg;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.AsyncCallMessage;
import com.stabilit.sc.msg.impl.UnSubscribeMessage;

public abstract class Callback implements ICallback {
	private IConnection con;
	private String subscribeId;
	private boolean released;

	public Callback(IConnection con) {
		this.con = con;
		this.subscribeId = null;
		this.released = false;
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
			con.send(request);
			return;
		}		
		SCMP request = new SCMP();
		request.setSubsribeId(this.subscribeId);
		request.setMessageId(AsyncCallMessage.ID);
		con.send(request);
	}

	@Override
	public boolean isReleased() {
		return released;
	}
	
	@Override
	public void release() {
	    this.released = true;	
	}
}
