package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.net.netty.tcp.SCMPBasedFrameDecoder;
import com.stabilit.sc.common.net.nio.http.SCMPHttpFrameDecoder;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ObjectStreamHttpUtil;

public class NioHttpRequest implements IRequest {

	private SocketChannel socketChannel;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder;
	private MapBean<Object> mapBean;
	private int headLineSize = 0;
	private SocketAddress socketAddress;

	public NioHttpRequest(SocketChannel socketChannel) {
		this.mapBean = new MapBean<Object>();
		this.socketChannel = socketChannel;
		this.socketAddress = socketChannel.socket().getLocalSocketAddress();
		this.scmp = null;
		this.requestContext = new RequestContext(socketChannel.socket().getRemoteSocketAddress());
	}

	@Override
	public SCMP getSCMP() throws Exception {
		if (scmp == null) {
			load();
		}
		return scmp;
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
		int httpFrameSize = SCMPHttpFrameDecoder.parseHttpFrameSize(byteBuffer.array());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array());
		while (httpFrameSize > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			bytesRead += read;
			baos.write(byteBuffer.array());
		}
		baos.flush();
		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) ObjectStreamHttpUtil.readObject(bais);
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
