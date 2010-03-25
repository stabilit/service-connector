package com.stabilit.sc.net.netty;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

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
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.util.MapBean;

public class NettyHttpRequest implements IRequest {

	private HttpRequest request;
	private SocketAddress socketAddress;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();
	private MapBean<Object> mapBean;

	public NettyHttpRequest(HttpRequest request, SocketAddress socketAddress) {
		this.mapBean = new MapBean<Object>();
		this.request = request;
		this.scmp = null;
		this.socketAddress = socketAddress;
		this.requestContext = new RequestContext(this.socketAddress);
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
	public SCMPMsgType getKey() {
		SCMP scmp = this.getSCMP();
		if (scmp == null) {
			return null;
		}
		String messageType = scmp.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}

	@Override
	public ISession getSession(boolean fCreate) {
		return SCMPSessionContext.getSession(scmp, fCreate);
	}

	private void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = new byte[channelBuffer.readableBytes()];
		channelBuffer.readBytes(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
		this.scmp = scmp;
	}

	@Override
	public Object getAttribute(String key) {
		return mapBean.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		mapBean.setAttribute(key, value);
	}
	
	@Override
	public MapBean<Object> getAttributeMapBean() {
		return mapBean;
	}
}
