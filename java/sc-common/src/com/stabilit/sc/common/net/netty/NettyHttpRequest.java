package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.util.MapBean;

public class NettyHttpRequest implements IRequest {

	private HttpRequest request;
	private SocketAddress socketAddress;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder;
	private MapBean<Object> mapBean;

	public NettyHttpRequest(HttpRequest request, SocketAddress socketAddress) {
		this.mapBean = new MapBean<Object>();
		this.request = request;
		this.scmp = null;
		this.socketAddress = socketAddress;
		this.requestContext = new RequestContext(this.socketAddress);
	}

	@Override
	public String getSessionId() {
		return scmp.getSessionId();
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
	public void setSCMP(SCMP scmp) {
		this.scmp = scmp;
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

	private void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = new byte[channelBuffer.readableBytes()];
		channelBuffer.readBytes(buffer);
		ConnectionListenerSupport.fireRead(this, buffer);  // logs inside if registered
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		}
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

	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	@Override
	public void read() throws Exception {
		load();
	}
}
