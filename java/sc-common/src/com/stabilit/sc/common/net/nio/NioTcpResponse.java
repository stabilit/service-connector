package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.ResponseAdapter;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;

public class NioTcpResponse extends ResponseAdapter {

	private SocketChannel socketChannel;
	private IEncoderDecoder encoderDecoder;

	public NioTcpResponse(SocketChannel socketChannel) {
		this.scmp = null;
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
		ConnectionListenerSupport.fireWrite(this, byteWriteBuffer);  // logs inside if registered
		this.socketChannel.write(buffer);
	}
}
