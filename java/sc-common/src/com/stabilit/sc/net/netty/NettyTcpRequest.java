package com.stabilit.sc.net.netty;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.ctx.RequestContext;
import com.stabilit.sc.ctx.SCMPSessionContext;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.MsgType;

public class NettyTcpRequest implements IRequest {

	private ChannelBuffer request;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	public NettyTcpRequest(MessageEvent e) {
		this.request = (ChannelBuffer) e.getMessage();
		this.scmp = null;
		this.requestContext = new RequestContext(e.getRemoteAddress());
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
	public MsgType getKey() {
		SCMP scmp = this.getSCMP();
		if (scmp == null) {
			return null;
		}
		String messageId = scmp.getMessageId();
		return MsgType.getMsgType(messageId);
	}

	@Override
	public ISession getSession(boolean fCreate) {
		return SCMPSessionContext.getSession(scmp, fCreate);
	}

	private void load() throws Exception {
		byte[] buffer = new byte[request.readableBytes()];
		request.readBytes(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = new SCMP();
		encoderDecoder.decode(bais, scmp);
		this.scmp = scmp;
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}
}
