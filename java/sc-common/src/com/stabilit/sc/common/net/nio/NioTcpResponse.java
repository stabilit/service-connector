package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.ISession;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;

public class NioTcpResponse implements IResponse {

	private SocketChannel socketChannel;
	private SCMP scmp;
	private ISession session;
	private IEncoderDecoder encoderDecoder;

	public NioTcpResponse(SocketChannel socketChannel) {
		this.scmp = null;
		this.session = null;
		this.socketChannel = socketChannel;
	}

	public byte[] getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (this.encoderDecoder == null) {
		    this.encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		}
		encoderDecoder.encode(baos, scmp);
		baos.close();
		byte[] buf = baos.toByteArray();
		return buf;
	}

	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.encoderDecoder = encoderDecoder;
	}

	@Override
	public void setSession(ISession session) {
		this.session = session;
	}

	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		try {
			this.scmp = scmp;
			if (this.session != null) {
				this.scmp.setSessionId(this.session.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void write() throws Exception {
		byte[] byteWriteBuffer = this.getBuffer();
		ByteBuffer buffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionListenerSupport.fireWrite(this, byteWriteBuffer);  // logs inside if registered
		this.socketChannel.write(buffer);
	}
}
