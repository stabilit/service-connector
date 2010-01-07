package com.stabilit.sc.app.server.socket.http;

import java.io.InputStream;
import java.net.Socket;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpRequest implements IRequest {

	private Socket socket;
	private SCOP scop;
	private IRequestContext requestContext;

	public SocketHttpRequest(Socket socket) throws Exception {
		this.socket = socket;
		this.scop = null;
		this.requestContext = new RequestContext();
		this.load();
	}
	
	@Override
	public IRequestContext getContext() {
		return requestContext;
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
	
	@Override
	public ISession getSession(boolean fCreate) {
		return SCOPSessionContext.getSession(scop, fCreate);
	}

	private void load() throws Exception {
			InputStream is = socket.getInputStream();
			Object obj = ObjectStreamHttpUtil.readObject(is);
			if (obj instanceof SCOP) {
				this.scop = (SCOP) obj;
			}
	}

}
