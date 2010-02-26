package com.stabilit.sc.app.server.netty.http;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.context.IRequestContext;
import com.stabilit.sc.context.RequestContext;
import com.stabilit.sc.context.SCOPSessionContext;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpRequest implements IRequest {

	private HttpRequest request;
	private SCMP scmp;
	private IRequestContext requestContext;

	public NettyHttpRequest(HttpRequest request) {
		this.request = request;
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
			try {
				load();
			} catch (Exception e) {
				return null;
			}
		}
		return scmp;
	}

	@Override
	public String getKey() {
		SCMP scmp = this.getSCMP();
		if (scmp == null) {
			return null;
		}
		String messageId = scmp.getMessageId();
		return messageId;
	}

	@Override
	public ISession getSession(boolean fCreate) {
		return SCOPSessionContext.getSession(scmp, fCreate);
	}

	private void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = channelBuffer.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
        if (obj instanceof SCMP) {
        	this.scmp = (SCMP)obj;
        }
	}
}
