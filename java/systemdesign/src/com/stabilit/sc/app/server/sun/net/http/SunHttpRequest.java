package com.stabilit.sc.app.server.sun.net.http;

import java.io.InputStream;
import java.io.ObjectInputStream;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.sun.net.httpserver.HttpExchange;

public class SunHttpRequest implements IRequest {

	private HttpExchange httpExchange;
	private SCOP scop;
	private IRequestContext requestContext;

	public SunHttpRequest(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
		this.scop = null;
		this.requestContext = new RequestContext();
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public IJob getJob() {
		if (scop == null) {
			load();
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

	private void load() {
		try {
			InputStream is = httpExchange.getRequestBody();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			if (obj instanceof SCOP) {
				this.scop = (SCOP) obj;
			}
		} catch (Exception e) {
		}
	}

}
