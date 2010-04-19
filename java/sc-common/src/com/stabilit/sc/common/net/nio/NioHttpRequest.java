package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.net.FrameDecoderFactory;
import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.SCMPStreamHttpUtil;

public class NioHttpRequest implements IRequest {

	private SocketChannel socketChannel;
	private SCMP scmp;
	private IRequestContext requestContext;
	private SCMPStreamHttpUtil streamHttpUtil;
	private MapBean<Object> mapBean;
	private SocketAddress socketAddress;

	public NioHttpRequest(SocketChannel socketChannel) {
		this.mapBean = new MapBean<Object>();
		this.socketChannel = socketChannel;
		this.socketAddress = socketChannel.socket().getLocalSocketAddress();
		this.scmp = null;
		this.requestContext = new RequestContext(socketChannel.socket().getRemoteSocketAddress());
		this.streamHttpUtil = new SCMPStreamHttpUtil();
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
	public String getSessionId() {
		return scmp.getSessionId();
	}

	@Override
	public SCMPMsgType getKey() throws Exception {
		SCMP scmp = this.getSCMP();
		String messageType = scmp.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}

	private void load() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb
		int bytesRead = socketChannel.read(byteBuffer);
		if (bytesRead < 0) {
			throw new NioTcpException("no bytes read");
		}
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getFrameDecoder("http");
		// warning, returns always the same instance, singleton
		byte[] byteReadBuffer = byteBuffer.array();
		int httpFrameSize = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteReadBuffer, 0, bytesRead);
		while (httpFrameSize > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			if (read < 0) {
				throw new NioHttpException("read failed (<0)");
			}
			bytesRead += read;
			baos.write(byteBuffer.array(),0, read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		ConnectionListenerSupport.fireRead(this, buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) streamHttpUtil.readSCMP(bais);
		bais.close();
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
         load();		
	}
}
