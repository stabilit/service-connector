package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.util.MapBean;

public class NettyTcpRequest implements IRequest {

	private ChannelBuffer request;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder;
	private MapBean<Object> mapBean;
	private SocketAddress socketAddress;

	public NettyTcpRequest(MessageEvent e, SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
		this.mapBean = new MapBean<Object>();
		this.request = (ChannelBuffer) e.getMessage();
		this.scmp = null;
		this.requestContext = new RequestContext(e.getRemoteAddress());
	}

	@Override
	public SCMP getSCMP() throws Exception {
		if (scmp == null) {
			load();
		}
		return scmp;
	}

	@Override
	public void setSCMP(SCMP scmp) {
		this.scmp = scmp;
	}

	@Override
	public SCMPMsgType getKey() throws Exception {
		SCMP scmp = this.getSCMP();
		String messageType = scmp.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}

	private void load() throws Exception {
		byte[] buffer = new byte[request.readableBytes()];
		request.readBytes(buffer);
		ConnectionListenerSupport.fireRead(this, buffer); // logs inside if registered
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
		this.scmp = scmp;
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
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
		if (scmp == null) {
			load();
		}
	}
}
