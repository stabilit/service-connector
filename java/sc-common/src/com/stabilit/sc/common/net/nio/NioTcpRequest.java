package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.net.FrameDecoderFactory;
import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.util.MapBean;

public class NioTcpRequest implements IRequest {

	private SocketChannel socketChannel;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder;
	private MapBean<Object> mapBean;
	private SocketAddress socketAddress;

	public NioTcpRequest(SocketChannel socketChannel) {
		this.mapBean = new MapBean<Object>();
		this.socketChannel = socketChannel;
		this.socketAddress = socketChannel.socket().getLocalSocketAddress();
		this.scmp = null;
		this.requestContext = new RequestContext(socketChannel.socket().getRemoteSocketAddress());
	}

	public void read() throws Exception {
		load();
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
			throw new NioTcpDisconnectException("line disconnected");
		}
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
		// warning, returns always the same instance, singleton
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionListenerSupport.fireRead(this, byteReadBuffer, 0, bytesRead); // logs inside if registered
		int scmpLengthHeadlineInc = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
		while (scmpLengthHeadlineInc > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			if (read < 0) {
				throw new IOException("read failed (<0)");
			}
			bytesRead += read;
			baos.write(byteBuffer.array(), 0, read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
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
}
