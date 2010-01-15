package com.stabilit.sc.app.server.http.impl;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpRequest implements IRequest {

	private HttpRequest request;
	private SCOP scop;
	private IRequestContext requestContext;

	public NettyHttpRequest(HttpRequest request) {
		this.request = request;
		this.scop = null;
		this.requestContext = new RequestContext();
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public IMessage getJob() {
		if (scop == null) {
			try {
				load();
			} catch (Exception e) {
				return null;
			}
		}
		return (IMessage) scop.getBody();
	}

	@Override
	public String getKey() {
		IMessage job = this.getJob();
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
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = channelBuffer.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
        if (obj instanceof SCOP) {
        	this.scop = (SCOP)obj;
        }
	}
}
