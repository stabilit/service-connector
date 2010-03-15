package com.stabilit.sc.app.server;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.ctx.RequestContext;
import com.stabilit.sc.ctx.SCMPSessionContext;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class NettyHttpRequest implements IRequest {

	private HttpRequest request;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

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
		return SCMPSessionContext.getSession(scmp, fCreate);
	}

	private void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = channelBuffer.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = new SCMP();
		encoderDecoder.decode(bais, scmp);
		this.scmp = scmp;
	}
}
