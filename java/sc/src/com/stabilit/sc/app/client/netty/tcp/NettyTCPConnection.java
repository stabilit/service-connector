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
package com.stabilit.sc.app.client.netty.tcp;

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

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ISCClientListener;
import com.stabilit.sc.pool.IPoolConnection;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyTCPConnection implements IClientConnection {

	private URL url = null;
	private String sessionId = null;
	private ClientBootstrap bootstrap = null;
	private Channel channel = null;
	private IPoolConnection decoratorConn = null;
	
	public NettyTCPConnection() {
	}
	
	@Override
	public void setDecorator(IPoolConnection dec) {
		this.decoratorConn = dec;
	}

	@Override
	public void deleteSession() {

	}

	@Override
	public void connect(Class<? extends ISCClientListener> scListenerClass) throws ConnectionException {

		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new TCPClientPipelineFactory(scListenerClass, decoratorConn));
		
		String host = url.getHost();
		int port = url.getPort();
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
	public void createSession() {
		// TODO schicke an SC CREATESESSION
		// TODO asynchron??????
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scmp);
		
		ChannelBuffer buffer = ChannelBuffers.buffer(baos.size());
		buffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(buffer);
		future.awaitUninterruptibly();
		return;
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scmp);
		
		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());
		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);
		future.awaitUninterruptibly();

		NettyClientTCPResponseHandler handler = channel.getPipeline().get(NettyClientTCPResponseHandler.class);
		ChannelBuffer content = handler.getMessageSync();

		byte[] buffer = content.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
		if (obj instanceof SCMP) {
			SCMP ret = (SCMP) obj;
			String retSessionID = ret.getSessionId();
			if (retSessionID != null) {
				this.sessionId = retSessionID;
			}
			return ret;
		}
		throw new Exception("not found");
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
}
