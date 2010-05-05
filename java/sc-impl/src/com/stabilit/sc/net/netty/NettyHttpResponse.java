/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.net.netty;

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

import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.ResponseAdapter;
import com.stabilit.sc.scmp.SCMP;

public class NettyHttpResponse extends ResponseAdapter {

	private MessageEvent event;
	private IEncoderDecoder encoderDecoder;

	public NettyHttpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
	}

	public MessageEvent getEvent() {
		return event;
	}

	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EncoderDecoderFactory encoderDecoderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();
		if (this.encoderDecoder == null) {
			encoderDecoder = encoderDecoderFactory.newInstance(this.scmp);
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
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		scmp.setIsReply(true);
		this.scmp = scmp;
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
		ConnectionListenerSupport.fireWrite(this, buffer.toByteBuffer().array()); // logs inside if registered
		// Close the connection after the write operation is done if necessary.
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
