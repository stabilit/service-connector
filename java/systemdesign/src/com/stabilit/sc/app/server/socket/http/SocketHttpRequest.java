package com.stabilit.sc.app.server.socket.http;

import java.io.InputStream;
import java.net.Socket;

import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpRequest implements IRequest {

	private Socket socket;
	private SCOP scop;

	public SocketHttpRequest(Socket socket) throws Exception {
		this.socket = socket;
		this.scop = null;
		this.load();
	}

	@Override
	public IJob getJob() {
		if (scop == null) {
			return null;
		}
		return (IJob) scop.getBody();
	}

	@Override
	public String getKey() {
		IJob job = this.getJob();
		if (job != null) {
			return job.getKey();
		}
		return null;
	}

	private void load() throws Exception {
			InputStream is = socket.getInputStream();
			Object obj = ObjectStreamHttpUtil.readObject(is);
			if (obj instanceof SCOP) {
				this.scop = (SCOP) obj;
			}
	}

}
