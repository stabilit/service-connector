package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.ISession;
import com.stabilit.sc.common.io.SCMP;

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

	
	public ByteBuffer getBuffer() throws Exception {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   this.encoderDecoder = EncoderDecoderFactory.newInstance(this.scmp);
	   encoderDecoder.encode(baos, scmp);
	   byte[] buf = baos.toByteArray();
	   return ByteBuffer.wrap(buf);
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
}
