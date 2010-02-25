/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.stabilit.sc.app.client.jboss.netty.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.client.IConnectionCallback;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ICallback;

public class NettyHttpConnection implements IConnection, IConnectionCallback {

	private URL url;
	private String sessionId;
	private ClientBootstrap bootstrap;
	private Channel channel;

	public NettyHttpConnection() {
		this.url = null;
		this.sessionId = null;
		this.channel = null;

		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors
						.newCachedThreadPool()));
		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new HttpClientPipelineFactory());
	}

	@Override
	public void closeSession() throws IOException {

	}

	@Override
	public void connect() throws Exception {
		String host = url.getHost();
		int port = url.getPort();
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));

		// Wait until the connection attempt succeeds or fails.
		this.channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			Exception e = (Exception) future.getCause();
			future.getCause().printStackTrace();
			this.bootstrap.releaseExternalResources();
			throw e;
		}

	}

	@Override
	public void disconnect() throws Exception {
		// Wait for the server to close the connection.
		this.channel.disconnect().awaitUninterruptibly();
	}

	@Override
	public void destroy() throws Exception {
		this.channel.close();
		this.bootstrap.releaseExternalResources();
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void openSession() throws IOException {

	}

	@Override
	public void send(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();
		encoderDecoder.encode(baos, scmp);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, this.url.getPath());
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		return;
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();
		encoderDecoder.encode(baos, scmp);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, this.url.getPath());
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		future.awaitUninterruptibly();

		HttpResponseHandler handler = channel.getPipeline().get(
				HttpResponseHandler.class);
		ChannelBuffer content = handler.getMessageSync().getContent();

		buffer = content.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP ret = new SCMP();
		encoderDecoder.decode(bais, ret);
		String retSessionID = ret.getSessionId();
		if (retSessionID != null) {
			this.sessionId = retSessionID;
		}
		return ret;
	}

	@Override
	public void setEndpoint(URL url) {
		this.url = url;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {

	}

	@Override
	public void setCallback(ICallback callback) {
		ChannelPipeline pipeline = this.channel.getPipeline();
		HttpResponseHandler handler = pipeline.get(HttpResponseHandler.class);
		handler.setCallback(callback);
	}
}
