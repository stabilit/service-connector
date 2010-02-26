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
package com.stabilit.sc.registrar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.RegisterMessage;
import com.stabilit.sc.serviceserver.HttpClientPipelineFactory;
import com.stabilit.sc.serviceserver.ServiceServerException;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.NettyResponseHandler;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SCRegistrar implements IClientConnection {

	private URL url;
	private ClientBootstrap bootstrap;
	private Channel channel;
	private String scHost;
	private int scPort;
	private String serviceHost;
	private int servicePort;

	public SCRegistrar(String servicName, String scHost, int scPort, String serviceHost, int servicePort,
			Class<? extends IResponseHandler<IClientConnection>> responseHandlerClass) throws ServiceServerException {
		try {
			this.url = new URL("http", scHost, scPort, "/");
		} catch (MalformedURLException e1) {
			throw new ServiceServerException("Given SCHost or SCPort must be mal formed.", e1);
		}
		this.channel = null;
		this.scHost = scHost;
		this.scPort = scPort;
		this.serviceHost = serviceHost;
		this.servicePort = servicePort;

		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		try {
			this.bootstrap.setPipelineFactory(new HttpClientPipelineFactory(responseHandlerClass, this));
		} catch (InstantiationException e) {
			throw new ServiceServerException("A parameter of type class is not instantiable.");
		} catch (Exception e) {
			throw new ServiceServerException(e);
		}
	}

	public void registerToSC(long timeout) throws ServiceServerException {
		try {
			connect(timeout);
			register();
			disconnect();
		} catch (Exception e) {
			throw new ServiceServerException("Registrar not able to connect SC", e);
		}
	}

	private void connect(long timeout) throws Exception {
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.scHost, this.scPort));

		// Wait the connection attempt succeeds or fails.
		if (!future.awaitUninterruptibly(timeout)) {
			throw new ServiceServerException("Registrar not able to connect SC");
		}

		this.channel = future.getChannel();
		if (!future.isSuccess()) {
			Exception e = (Exception) future.getCause();
			future.getCause().printStackTrace();
			this.bootstrap.releaseExternalResources();
			throw e;
		}
	}

	public void disconnect() throws Exception {
		// Wait for the server to close the connection.
		this.channel.disconnect().awaitUninterruptibly();
	}

	private void register() throws Exception {
		// TODO correct msg verwenden
		IMessage msg = new RegisterMessage();
		SCMP scmp = new SCMP(msg);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scmp);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, this.url
				.getPath());
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		future.awaitUninterruptibly();

		NettyResponseHandler nettyHandler = channel.getPipeline().get(NettyResponseHandler.class);
		IResponseHandler handler = nettyHandler.getCallback();
		Object scopRec = handler.getMessageSync();
		// TODO if scop msg = registered! alles ok!
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IConnection#connect()
	 */
	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IConnection#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IConnection#send(com.stabilit.sc.io.SCMP)
	 */
	@Override
	public void send(SCMP scmp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IConnection#sendAndReceive(com.stabilit.sc.io.SCMP)
	 */
	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#closeSession()
	 */
	@Override
	public void closeSession() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#getSessionId()
	 */
	@Override
	public String getSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#openSession()
	 */
	@Override
	public void openSession() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#setAvailable(boolean)
	 */
	@Override
	public void setAvailable(boolean available) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClientConnection#setEndpoint(java.net.URL)
	 */
	@Override
	public void setEndpoint(URL url) {
		// TODO Auto-generated method stub
		
	}
}
