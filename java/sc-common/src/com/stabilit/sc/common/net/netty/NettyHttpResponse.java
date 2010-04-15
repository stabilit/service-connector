package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.ISession;
import com.stabilit.sc.common.io.SCMP;

public class NettyHttpResponse implements IResponse {

	private MessageEvent event;
	private SCMP scmp;
	private ISession session;
	private IEncoderDecoder encoderDecoder;	
	
	public NettyHttpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
		this.session = null;
	}

	public MessageEvent getEvent() {
		return event;
	}
	
	public ChannelBuffer getBuffer() throws Exception {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   encoderDecoder = EncoderDecoderFactory.newInstance(this.scmp);
	   encoderDecoder.encode(baos, this.scmp);
	   byte[] buf = baos.toByteArray();
	   return ChannelBuffers.copiedBuffer(buf);
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
		ChannelBuffer buffer = this.getBuffer();
		// Write the response.
		ChannelFuture future = event.getChannel().write(buffer);
	}

}
