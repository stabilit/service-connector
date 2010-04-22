package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.ResponseAdapter;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.util.SCMPStreamHttpUtil;

public class NioHttpResponse extends ResponseAdapter {

	private SocketChannel socketChannel;
	private SCMPStreamHttpUtil streamHttpUtil;

	public NioHttpResponse(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.streamHttpUtil = new SCMPStreamHttpUtil();
	}

	public byte[] getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.streamHttpUtil.writeResponseSCMP(baos, scmp);
		baos.close();
		byte[] buf = baos.toByteArray();
		return buf;
	}

	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.streamHttpUtil.setEncoderDecoder(encoderDecoder);
	}

	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		this.scmp = scmp;
	}

	@Override
	public void write() throws Exception {
		byte[] byteWriteBuffer = this.getBuffer();
		ByteBuffer buffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionListenerSupport.fireWrite(this, byteWriteBuffer); // logs inside if registered
		this.socketChannel.write(buffer);
	}
}
