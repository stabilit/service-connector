package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.ResponseAdapter;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;

public class NettyTcpResponse extends ResponseAdapter {

	private MessageEvent event;
	private IEncoderDecoder encoderDecoder;

	public NettyTcpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
	}

	public MessageEvent getEvent() {
		return event;
	}

	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		}
		encoderDecoder.encode(baos, scmp);
		byte[] buf = baos.toByteArray();
		return ChannelBuffers.copiedBuffer(buf);
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
		ChannelBuffer buffer = this.getBuffer();
		// Write the response.
		ChannelFuture future = event.getChannel().write(buffer);
		ConnectionListenerSupport.fireWrite(this, buffer.toByteBuffer().array());  // logs inside if registered
	}
}
