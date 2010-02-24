package com.stabilit.sc.app.server.sun.net.http;

import java.io.InputStream;
import java.io.ObjectInputStream;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.sun.net.httpserver.HttpExchange;

public class SunHttpRequest implements IRequest {

	private HttpExchange httpExchange;
	private SCMP scmp;
	private IRequestContext requestContext;

	public SunHttpRequest(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
		this.scmp = null;
		this.requestContext = new RequestContext();
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public SCMP getSCMP() {
		if (scmp == null) {
			load();
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

	private void load() {
		try {
			InputStream is = httpExchange.getRequestBody();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			if (obj instanceof SCMP) {
				this.scmp = (SCMP) obj;
			}
		} catch (Exception e) {
		}
	}
}
