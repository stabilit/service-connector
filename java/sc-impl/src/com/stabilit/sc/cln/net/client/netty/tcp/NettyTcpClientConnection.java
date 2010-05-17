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

import com.stabilit.sc.cln.client.IClientConnection;
import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.cln.net.client.netty.NettyOperationListener;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPError;
import com.stabilit.sc.srv.net.SCMPCommunicationException;

/**
 * The Class NettyTcpClientConnection. Concrete client connection implementation with JBoss Netty for Tcp.
 */
public class NettyTcpClientConnection implements IClientConnection {

	/** The bootstrap. */
	private ClientBootstrap bootstrap;
	/** The channel. */
	private Channel channel;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The numberOfThreads. */
	private int numberOfThreads;
	/** The operation listener. */
	private NettyOperationListener operationListener;
	/** The channel factory. */
	private NioClientSocketChannelFactory channelFactory;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty Tcp client connection.
	 */
	public NettyTcpClientConnection() {
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
		this.numberOfThreads = 10;
		this.operationListener = null;
		this.channelFactory = null;
		this.encoderDecoder = null;
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		/*
		 * Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming
		 * connections on a socket. A worker thread performs non-blocking read and write on a channel.
		 */
		channelFactory = new NioClientSocketChannelFactory(Executors.newFixedThreadPool(numberOfThreads),
				Executors.newFixedThreadPool(numberOfThreads / 4));
		this.bootstrap = new ClientBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(new NettyTcpClientPipelineFactory());
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		try {
			this.channel = operationListener.awaitUninterruptibly().getChannel();
		} catch (CommunicationException ex) {
			ExceptionListenerSupport.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionListenerSupport.getInstance().fireConnect(this, this.port);
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ChannelFuture future = this.channel.disconnect();
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionListenerSupport.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionListenerSupport.getInstance().fireDisconnect(this, this.port);
		this.bootstrap.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() throws Exception {
		ChannelFuture future = this.channel.close();
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionListenerSupport.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		this.bootstrap.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		encoderDecoder.encode(baos, scmp);

		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());
		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionListenerSupport.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionListenerSupport.getInstance().fireWrite(this, this.port, chBuffer.toByteBuffer().array());

		NettyTcpClientResponseHandler handler = channel.getPipeline().get(NettyTcpClientResponseHandler.class);
		ChannelBuffer content = (ChannelBuffer) handler.getMessageSync();
		byte[] buffer = new byte[content.readableBytes()];
		content.readBytes(buffer);
		ConnectionListenerSupport.getInstance().fireRead(this, this.port, buffer); // logs inside if registered
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		SCMPMessage ret = (SCMPMessage) encoderDecoder.decode(bais);
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NettyTcpClientConnection();
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}
}
