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
package com.stabilit.sc.cln.net.client.netty.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.cln.net.client.netty.http.NettyOperationListener;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.SCMP;

public class NettyTcpClientConnection extends ClientConnectionAdapter {

	private ClientBootstrap bootstrap = null;
	private Channel channel = null;
	private int port;
	private String host;
	private NettyOperationListener operationListener;

	public NettyTcpClientConnection() {
	}

	@Override
	public void connect() throws ConnectionException {
		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new NettyTcpClientPipelineFactory());

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

		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());

		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);
		future.addListener(operationListener);
		operationListener.awaitUninterruptibly();

		NettyTcpClientResponseHandler handler = channel.getPipeline()
				.get(NettyTcpClientResponseHandler.class);
		ChannelBuffer content = handler.getMessageSync();
		byte[] buffer = new byte[content.readableBytes()];
		content.readBytes(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		//TODO encoder ?? large
		encoderDecoder = EncoderDecoderFactory.newInstance(buffer);
		SCMP ret = (SCMP) encoderDecoder.decode(bais);
		return ret;
	}

	@Override
	public IFactoryable newInstance() {
		return new NettyTcpClientConnection();
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}
}
