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
package com.stabilit.scm.common.net.req.netty.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.ExternalResourceReleasable;

import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.CommunicationException;
import com.stabilit.scm.common.net.EncoderDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.net.req.IConnection;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.netty.NettyIdleHandler;
import com.stabilit.scm.common.net.req.netty.NettyOperationListener;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class NettyTcpConnection. Concrete connection implementation with JBoss Netty for Tcp.
 */
public class NettyTcpConnection implements IConnection {

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
	/** The local socket address. */
	private InetSocketAddress localSocketAddress;
	/** The channel pipeline factory. */
	private ChannelPipelineFactory pipelineFactory;
	private IConnectionContext connectionContext;
	/** state of connection. */
	private boolean isConnected;
	protected int idleTimeout;
	private int nrOfIdles;

	/**
	 * Instantiates a new NettyTcpConnection.
	 */
	public NettyTcpConnection() {
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
		this.numberOfThreads = 10;
		this.operationListener = null;
		this.channelFactory = null;
		this.encoderDecoder = null;
		this.localSocketAddress = null;
		this.isConnected = false;
		this.pipelineFactory = null;
		this.connectionContext = null;
	}

	@Override
	public IConnectionContext getContext() {
		return this.connectionContext;
	}
		
	@Override
	public void setContext(IConnectionContext connectionContext) {
		this.connectionContext = connectionContext;
	}
	
	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		/*
		 * Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming
		 * connections on a socket. A worker thread performs non-blocking read and write on a channel.
		 */
		channelFactory = new NioClientSocketChannelFactory(Executors.newFixedThreadPool(numberOfThreads), Executors
				.newFixedThreadPool(numberOfThreads / 4));
		this.bootstrap = new ClientBootstrap(channelFactory);
		this.pipelineFactory = new NettyTcpRequesterPipelineFactory(this.connectionContext);
		this.bootstrap.setPipelineFactory(this.pipelineFactory);
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		try {
			this.channel = operationListener.awaitUninterruptibly().getChannel();
			this.localSocketAddress = (InetSocketAddress) this.channel.getLocalAddress();
		} catch (CommunicationException ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireConnect(this, this.localSocketAddress.getPort());
		this.isConnected = true;
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ChannelFuture future = this.channel.disconnect();
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireDisconnect(this, this.localSocketAddress.getPort());
		this.bootstrap.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		ChannelFuture future = this.channel.close();
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (Exception ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
		}
		this.releaseExternalResources();
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
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireWrite(this, this.localSocketAddress.getPort(),
				chBuffer.toByteBuffer().array());

		NettyTcpRequesterResponseHandler handler = channel.getPipeline().get(NettyTcpRequesterResponseHandler.class);
		ChannelBuffer content = (ChannelBuffer) handler.getMessageSync();
		byte[] buffer = new byte[content.readableBytes()];
		content.readBytes(buffer);
		ConnectionPoint.getInstance().fireRead(this, this.localSocketAddress.getPort(), buffer); // logs inside if
		// registered
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		SCMPMessage ret = (SCMPMessage) encoderDecoder.decode(bais);
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		encoderDecoder.encode(baos, scmp);

		NettyTcpRequesterResponseHandler handler = channel.getPipeline().get(NettyTcpRequesterResponseHandler.class);
		handler.setCallback(callback);

		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());
		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireWrite(this, this.localSocketAddress.getPort(),
				chBuffer.toByteBuffer().array());
		return;
	}

	/** {@inheritDoc} */
	@Override
	public IConnection newInstance() {
		return new NettyTcpConnection();
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

	/**
	 * Release external resources.
	 */
	private void releaseExternalResources() {
		ChannelPipeline pipeline = this.channel.getPipeline();
		// release resources in idle timeout handler
		ExternalResourceReleasable externalResourceReleasable = pipeline.get(NettyIdleHandler.class);
		externalResourceReleasable.releaseExternalResources();
		// release resources in write timeout handler
		externalResourceReleasable = pipeline.get(WriteTimeoutHandler.class);
		externalResourceReleasable.releaseExternalResources();
		// release resources in read timeout handler
		externalResourceReleasable = pipeline.get(ReadTimeoutHandler.class);
		externalResourceReleasable.releaseExternalResources();
		// release resources in client connection
		this.bootstrap.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConnected() {
		return this.isConnected;
	}

	@Override
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public int getNrOfIdlesInSequence() {
		return nrOfIdles;
	}

	@Override
	public void incrementNrOfIdles() {
		this.nrOfIdles++;
	}

	@Override
	public void resetNrOfIdles() {
		this.nrOfIdles = 0;
	}
}
