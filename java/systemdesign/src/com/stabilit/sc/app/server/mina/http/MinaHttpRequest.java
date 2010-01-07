package com.stabilit.sc.app.server.mina.http;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;

public class MinaHttpRequest implements IRequest {

	private HttpRequestMessage message;
	private SCOP scop;
	private IRequestContext requestContext;

	public MinaHttpRequest(HttpRequestMessage message) {
		this.message = message;
		this.scop = (SCOP)message.getObj();
		this.requestContext = new RequestContext();
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

}
