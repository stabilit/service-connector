/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.common.net.req.netty.http;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.ExternalResourceReleasable;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.ctx.IContext;
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
 * The Class NettyHttpClientConnection. Concrete connection implementation with JBoss Netty for Http.
 * 
 * @author JTraber
 */
public class NettyHttpConnection implements IConnection {

	/** The url. */
	private URL url;
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
	private boolean connected;
	protected int idleTimeout;
	private int nrOfIdles;

	/**
	 * Instantiates a new netty http connection.
	 */
	public NettyHttpConnection() {
		this.url = null;
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.numberOfThreads = IConstants.DEFAULT_NR_OF_THREADS;
		this.host = null;
		this.operationListener = null;
		this.channelFactory = null;
		this.encoderDecoder = null;
		this.localSocketAddress = null;
		this.connected = false;
		this.idleTimeout = 0; // default 0 -> inactive
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
		// this.bootstrap.setOption("connectTimeoutMillis", 1000000); TODO
		this.pipelineFactory = new NettyHttpRequesterPipelineFactory(this.connectionContext);
		this.bootstrap.setPipelineFactory(this.pipelineFactory);
		// Starts the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		this.operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		try {
			// waits until operation is done
			this.channel = operationListener.awaitUninterruptibly().getChannel();
			this.localSocketAddress = (InetSocketAddress) this.channel.getLocalAddress();
		} catch (CommunicationException ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireConnect(this, this.localSocketAddress.getPort());
		this.connected = true;
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
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(this, th);
		}
		this.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		NettyHttpSCMPCallback callback = new NettyHttpSCMPCallback();
		this.send(scmp, callback);
		SCMPMessage ret = callback.join();
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		encoderDecoder.encode(baos, scmp);
		url = new URL(IConstants.HTTP, host, port, IConstants.HTTP_FILE);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, this.url.getPath());
		byte[] buffer = baos.toByteArray();
		// Http header fields
		request.addHeader(HttpHeaders.Names.USER_AGENT, System.getProperty("java.runtime.version"));
		request.addHeader(HttpHeaders.Names.HOST, host);
		request.addHeader(HttpHeaders.Names.ACCEPT, IConstants.ACCEPT_PARAMS);
		request.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		request.addHeader(HttpHeaders.Names.CONTENT_TYPE, scmp.getBodyType().getMimeType());
		request.addHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.length));

		NettyHttpRequesterResponseHandler handler = channel.getPipeline().get(NettyHttpRequesterResponseHandler.class);
		handler.setCallback(callback);

		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly();
		} catch (CommunicationException ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		ConnectionPoint.getInstance().fireWrite(this, this.localSocketAddress.getPort(), buffer, 0, buffer.length); // logs
		return;
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/** {@inheritDoc} */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public IConnection newInstance() {
		return new NettyHttpConnection();
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
		return this.connected;
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

	class NettyHttpSCMPCallback implements ISCMPCallback {
		private SCMPMessage reply;
		private Throwable th;
		/** Queue to store the answer. */
		private final BlockingQueue<SCMPMessage> answer = new LinkedBlockingQueue<SCMPMessage>();

		public NettyHttpSCMPCallback() {
			this.reply = null;
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			answer.offer(scmpReply);
		}

		@Override
		public void callback(Throwable th) {
			this.th = th;
		}

		@Override
		public IContext getContext() {
			return null;
		}

		@Override
		public void setContext(IContext context) {
		}

		public SCMPMessage join() throws Exception {
			this.reply = answer.take();
			return this.reply;
		}
	}
}
