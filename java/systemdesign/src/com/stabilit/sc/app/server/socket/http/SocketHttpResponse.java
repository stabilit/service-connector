package com.stabilit.sc.app.server.socket.http;

import java.io.OutputStream;
import java.net.Socket;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpResponse implements IResponse {

	private Socket socket;
	private ISession session;

	public SocketHttpResponse(Socket socket) {
		this.socket = socket;
		this.session = null;
	}

	@Override
	public void setSession(ISession session) {
		this.session = session;
	}

	@Override
	public void setJobResult(IJobResult jobResult) throws Exception {
		if (jobResult == null) {
			return;
		}
		OutputStream os = socket.getOutputStream();
		SCOP scop = new SCOP(jobResult);
		if (this.session != null) {
		   scop.setSessionId(this.session.getId());
		}
		ObjectStreamHttpUtil.writeResponseObject(os, scop);

	}

}
