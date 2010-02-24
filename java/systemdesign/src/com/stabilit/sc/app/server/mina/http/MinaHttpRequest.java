package com.stabilit.sc.app.server.mina.http;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class MinaHttpRequest implements IRequest {

	private HttpRequestMessage message;
	private SCMP scmp;
	private IRequestContext requestContext;

	public MinaHttpRequest(HttpRequestMessage message) {
		this.message = message;
		this.scmp = (SCMP)message.getObj();
		this.requestContext = new RequestContext();
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public SCMP getSCMP() {
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

}
