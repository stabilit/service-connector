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
package com.stabilit.sc.cln.net.client.netty.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.listener.ExceptionListenerSupport;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.impl.EncoderDecoderFactory;

public class NettyHttpClientConnection extends ClientConnectionAdapter {

	private URL url = null;
	private ClientBootstrap bootstrap = null;
	private Channel channel = null;
	private int port;
	private String host;
	private NettyOperationListener operationListener;
	NioClientSocketChannelFactory channelFactory = null;

	public NettyHttpClientConnection() {
		channelFactory = new NioClientSocketChannelFactory(Executors.newFixedThreadPool(50), Executors
				.newFixedThreadPool(10));
	}

	@Override
	public void connect() throws ConnectionException {

		// Configure the client.
		this.bootstrap = new ClientBootstrap(channelFactory);
		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new NettyHttpClientPipelineFactory());

		// Start the connection attempt.
		try {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
			operationListener = new NettyOperationListener();
			future.addListener(operationListener);

			// Wait until the connection attempt succeeds or fails.
			this.channel = operationListener.awaitUninterruptibly().getChannel();
			if (!future.isSuccess()) {
				Exception e = (Exception) future.getCause();
				future.getCause().printStackTrace();
				this.bootstrap.releaseExternalResources();
				throw new ConnectionException("Connection could not be established.", e);
			}
		} catch (ChannelPipelineException e) {
			ExceptionListenerSupport.fireException(this, e);
			throw new ConnectionException("Connection could not be established.", e);
		}
	}

	@Override
	public void disconnect() {
		// Wait for the server to close the connection.
		ChannelFuture future = this.channel.disconnect();
		future.addListener(operationListener);
		operationListener.awaitUninterruptibly();
	}

	@Override
	public void destroy() {
		this.channel.close();
		this.bootstrap.releaseExternalResources();
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		encoderDecoder.encode(baos, scmp);
		url = new URL("http", host, port, "/");
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, this.url
				.getPath());
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		future.addListener(operationListener);
		operationListener.awaitUninterruptibly();
		ConnectionListenerSupport.fireWrite(this, buffer); // logs inside if registered

		NettyHttpClientResponseHandler handler = channel.getPipeline().get(
				NettyHttpClientResponseHandler.class);
		ChannelBuffer content = handler.getMessageSync().getContent();

		buffer = new byte[content.readableBytes()];
		content.readBytes(buffer);
		ConnectionListenerSupport.fireRead(this, buffer); // logs inside if registered
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		SCMP ret = (SCMP) encoderDecoder.decode(bais);
		return ret;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public IFactoryable newInstance() {
		return new NettyHttpClientConnection();
	}
}
