package com.stabilit.sc.app.server.socket.http;

import java.io.InputStream;
import java.net.Socket;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpRequest implements IRequest {

	private Socket socket;
	private SCMP scmp;
	private IRequestContext requestContext;

	public SocketHttpRequest(Socket socket) throws Exception {
		this.socket = socket;
		this.scmp = null;
		this.requestContext = new RequestContext();
		this.load();
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public SCMP getSCMP() {
		if (scmp == null) {
			return null;
		}
		return scmp;
	}

	@Override
	public String getKey() {
		if (scmp != null) {
			return scmp.getMessageId();
		}
		return null;
	}

	@Override
	public ISession getSession(boolean fCreate) {
		return SCOPSessionContext.getSession(scmp, fCreate);
	}

	private void load() throws Exception {
		InputStream is = socket.getInputStream();
		Object obj = ObjectStreamHttpUtil.readObject(is);
		if (obj instanceof SCMP) {
			this.scmp = (SCMP) obj;
		}
	}

}
