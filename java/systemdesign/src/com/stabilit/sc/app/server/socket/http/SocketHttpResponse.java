package com.stabilit.sc.app.server.socket.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpResponse implements IResponse {

	private Socket socket;

	public SocketHttpResponse(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void setJobResult(IJobResult jobResult) throws Exception {
		if (jobResult == null) {
			return;
		}
		OutputStream os = socket.getOutputStream();
		SCOP scop = new SCOP(jobResult);
		ObjectStreamHttpUtil.writeResponseObject(os, scop);

	}

}
