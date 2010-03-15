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
package com.stabilit.sc.net.client.netty.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;

public class NettyTcpClientConnection implements IClientConnection {

	private String sessionId = null;
	private ClientBootstrap bootstrap = null;
	private Channel channel = null;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	public NettyTcpClientConnection() {
	}

	@Override
	public void deleteSession() {

	}

	@Override
	public void connect(String host, int port) throws ConnectionException {
		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new NettyTcpClientPipelineFactory());

		// Start the connection attempt.
		try {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

			// Wait until the connection attempt succeeds or fails.
			this.channel = future.awaitUninterruptibly().getChannel();
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
	public void connect(String host, int port, ChannelFutureListener listener) throws ConnectionException {
		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new NettyTcpClientPipelineFactory());

		// Start the connection attempt.
		try {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

			future.addListener(listener);

		} catch (ChannelPipelineException e) {
			throw new ConnectionException("Connection could not be established.", e);
		}
	}

	@Override
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void disconnect() {
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
	public void createSession() {
		// TODO schicke an SC CREATESESSION
		// TODO asynchron??????
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder.encode(baos, scmp);

		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());

		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);

		NettyTcpClientResponseHandler handler = channel.getPipeline()
				.get(NettyTcpClientResponseHandler.class);
		ChannelBuffer content = handler.getMessageSync();

		byte[] buffer = content.array();
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
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {

	}
}
