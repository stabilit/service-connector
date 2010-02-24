package com.stabilit.sc.app.server.socket.http;

import java.io.OutputStream;
import java.net.Socket;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpResponse implements IResponse {

	private Socket socket;
	private ISession session;
	private SCMP scmp;

	public SocketHttpResponse(Socket socket) {
		this.socket = socket;
		this.session = null;
		this.scmp = null;
	}

	@Override
	public void setSession(ISession session) {
		this.session = session;
	}

	@Override
	public void setSCMP(SCMP scmp) throws Exception {
		if (scmp == null) {
			return;
		}
		OutputStream os = socket.getOutputStream();
		this.scmp = scmp;
		if (this.session != null) {
		   this.scmp.setSessionId(this.session.getId());
		}
		ObjectStreamHttpUtil.writeResponseObject(os, this.scmp);

	}

}
