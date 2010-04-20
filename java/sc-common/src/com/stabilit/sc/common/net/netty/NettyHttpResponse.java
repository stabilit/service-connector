package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.ISession;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;

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
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		}
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
		HttpRequest httpRequest = (HttpRequest) event.getMessage();

		// Decide whether to close the connection or not.
		boolean close = !httpRequest.isKeepAlive();
		// TODO ?? keepAlive close?

		// Build the response object.
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ChannelBuffer buffer = getBuffer();

		httpResponse.setContent(buffer);

		if (!close) {
			// There's no need to add 'Content-Length' header
			// if this is the last response.
			httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		}
		// Write the response.
		ChannelFuture future = event.getChannel().write(httpResponse);

		// Close the connection after the write operation is done if necessary.
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
		// ChannelBuffer buffer = this.getBuffer();
		// // Write the response.
		// ChannelFuture future = event.getChannel().write(buffer);
		ConnectionListenerSupport.fireWrite(this, buffer.toByteBuffer().array()); // logs inside if registered
	}
}
