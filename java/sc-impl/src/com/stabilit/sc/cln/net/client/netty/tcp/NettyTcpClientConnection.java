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
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.cln.net.TransportException;
import com.stabilit.sc.cln.net.client.netty.NettyOperationListener;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.scmp.SCMP;

public class NettyTcpClientConnection extends ClientConnectionAdapter {

	private ClientBootstrap bootstrap;
	private Channel channel;
	private int port;
	private String host;
	private NettyOperationListener operationListener;
	private NioClientSocketChannelFactory channelFactory;

	public NettyTcpClientConnection() {
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
		this.operationListener = null;
		this.channelFactory = null;
		channelFactory = new NioClientSocketChannelFactory(Executors.newFixedThreadPool(20), Executors
				.newFixedThreadPool(5));
	}

	@Override
	public void connect() throws Exception {
		this.bootstrap = new ClientBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(new NettyTcpClientPipelineFactory());

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		this.channel = operationListener.awaitUninterruptibly().getChannel();
	}

	@Override
	public void disconnect() throws Exception {
		ChannelFuture future = this.channel.disconnect();
		future.addListener(operationListener);
		operationListener.awaitUninterruptibly();
		this.bootstrap.releaseExternalResources();
	}

	@Override
	public void destroy() throws TransportException {
		ChannelFuture future = this.channel.close();
		future.addListener(operationListener);
		operationListener.awaitUninterruptibly();
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
		ConnectionListenerSupport.fireWrite(this, chBuffer.toByteBuffer().array());

		NettyTcpClientResponseHandler handler = channel.getPipeline()
				.get(NettyTcpClientResponseHandler.class);
		ChannelBuffer content = (ChannelBuffer) handler.getMessageSync();
		byte[] buffer = new byte[content.readableBytes()];
		content.readBytes(buffer);
		ConnectionListenerSupport.fireRead(this, buffer); // logs inside if registered
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
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
