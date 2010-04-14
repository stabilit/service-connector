package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.util.MapBean;

public class NioTcpRequest implements IRequest {

	private SocketChannel socketChannel;
	private SCMP scmp;
	private IRequestContext requestContext;
	private IEncoderDecoder encoderDecoder;
	private MapBean<Object> mapBean;
	private int headLineSize = 0;
	private SocketAddress socketAddress;

	public NioTcpRequest(SocketChannel socketChannel) {
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
		// parse headline
		int scmpSize = parseHeadline(byteBuffer.array());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array());
		while (scmpSize + this.headLineSize > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			bytesRead += read;
			baos.write(byteBuffer.array());
		}
		baos.flush();
		byte[] buffer = baos.toByteArray();
		encoderDecoder = EncoderDecoderFactory.newInstance(buffer);
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

	private int parseHeadline(byte[] buffer) throws SCMPNioDecoderException {
		if (buffer == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < buffer.length; i++) {
			byte b = buffer[i];
			if (b == '\n') {
				if (i <= 2) {
					throw new SCMPNioDecoderException("invalid scmp header line");
				}
				SCMPHeaderKey headerKey = SCMPHeaderKey.getMsgHeaderKey(buffer);
				if (headerKey == SCMPHeaderKey.UNDEF) {
					throw new SCMPNioDecoderException("invalid scmp header line");
				}
				int startIndex = 0;
				int endIndex = 0;
				label: for (startIndex = 0; startIndex < i; startIndex++) {
					if (buffer[startIndex] == '/' || buffer[startIndex] == '&') {
						if (buffer[startIndex + 1] == 's' && buffer[startIndex + 2] == '=') {
							startIndex += 3;
							for (endIndex = startIndex; endIndex < buffer.length; endIndex++) {
								if (buffer[endIndex] == '&' || buffer[endIndex] == ' ')
									break label;
							}
						}
					}
				}
				// parse scmpLength
				int scmpLength = readInt(buffer, startIndex, endIndex - 1);
				this.headLineSize = i + 1;
				System.out.println(" return scmp length = " + scmpLength);
				return scmpLength;
			}

		}
		System.out.println("NioTcpRequest invalid scmp header line buffer = " + new String(buffer, 0, 100));
		throw new SCMPNioDecoderException("invalid scmp header line");
	}

	private int readInt(byte[] b, int startOffset, int endOffset) throws SCMPNioDecoderException {

		if (b == null) {
			throw new SCMPNioDecoderException("invalid scmp message length");
		}

		if (endOffset <= 0 || endOffset <= startOffset) {
			throw new SCMPNioDecoderException("invalid scmp message length");
		}

		if ((endOffset - startOffset) > 5) {
			throw new SCMPNioDecoderException("invalid scmp message length");
		}

		int scmpLength = 0;
		int factor = 1;
		for (int i = endOffset; i >= startOffset; i--) {
			if (b[i] >= '0' && b[i] <= '9') {
				scmpLength += ((int) b[i] - 0x30) * factor;
				factor *= 10;
			} else {
				throw new SCMPNioDecoderException("invalid scmp message length");
			}
		}
		return scmpLength;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
}
