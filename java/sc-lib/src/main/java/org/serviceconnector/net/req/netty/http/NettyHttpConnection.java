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
package org.serviceconnector.net.req.netty.http;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.Timer;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class NettyHttpClientConnection. Concrete connection implementation with JBoss Netty for Http.
 * 
 * @author JTraber
 */
public class NettyHttpConnection implements IConnection {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpConnection.class);
	/** The Constant connectionLogger. */
	private final static ConnectionLogger connectionLogger = ConnectionLogger.getInstance();
	/** The base conf. */
	protected final BasicConfiguration baseConf = AppContext.getBasicConfiguration();

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
	/** The operation listener. */
	private NettyOperationListener operationListener;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;
	/** The local socket address. */
	private InetSocketAddress localSocketAddress;
	/** The channel pipeline factory. */
	private ChannelPipelineFactory pipelineFactory;
	/** The connection context. */
	private ConnectionContext connectionContext;
	/** The idle timeout. */
	protected int idleTimeout;
	/** The number of idles, counts idle states. */
	private int nrOfIdles;
	/** The timer to observe timeouts, static because should be shared. */
	private static Timer timer;
	/*
	 * The channel factory. Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming
	 * connections on a socket. A worker thread performs non-blocking read and write on a channel.
	 */
	private static NioClientSocketChannelFactory channelFactory;

	/**
	 * Instantiates a new netty http connection.
	 */
	public NettyHttpConnection() {
		this.url = null;
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
		this.operationListener = null;
		this.encoderDecoder = null;
		this.localSocketAddress = null;
		this.idleTimeout = 0; // default 0 -> inactive
		this.pipelineFactory = null;
		this.connectionContext = null;
	}

	/**
	 * Instantiates a new netty http connection.
	 */
	public NettyHttpConnection(NioClientSocketChannelFactory channelFactory, Timer timer) {
		this();
		NettyHttpConnection.channelFactory = channelFactory;
		NettyHttpConnection.timer = timer;
	}

	/** {@inheritDoc} */
	@Override
	public ConnectionContext getContext() {
		return this.connectionContext;
	}

	/** {@inheritDoc} */
	@Override
	public void setContext(ConnectionContext connectionContext) {
		this.connectionContext = connectionContext;
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		this.bootstrap = new ClientBootstrap(channelFactory);
		this.bootstrap.setOption("connectTimeoutMillis", baseConf.getConnectionTimeoutMillis());
		this.pipelineFactory = new NettyHttpRequesterPipelineFactory(this.connectionContext, NettyHttpConnection.timer);
		this.bootstrap.setPipelineFactory(this.pipelineFactory);
		// Starts the connection attempt.
		this.localSocketAddress = new InetSocketAddress(host, port);
		ChannelFuture future = bootstrap.connect(this.localSocketAddress);
		this.operationListener = new NettyOperationListener();
		future.addListener(this.operationListener);
		try {
			// waits until operation is done
			this.channel = this.operationListener.awaitUninterruptibly(baseConf.getConnectionTimeoutMillis()).getChannel();
			// complete localSocketAdress
			this.localSocketAddress = (InetSocketAddress) this.channel.getLocalAddress();
		} catch (CommunicationException ex) {
			logger.error("connect", ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "connect failed to "
					+ this.localSocketAddress.toString());
		}
		if (connectionLogger.isEnabled()) {
			connectionLogger.logConnect(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ChannelFuture future = this.channel.disconnect();
		future.addListener(this.operationListener);
		try {
			this.operationListener.awaitUninterruptibly(baseConf.getConnectionTimeoutMillis());
		} catch (CommunicationException ex) {
			logger.error("disconnect", ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "disconnect failed from "
					+ this.localSocketAddress.toString());
		}
		if (connectionLogger.isEnabled()) {
			connectionLogger.logDisconnect(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		ChannelFuture future = this.channel.close();
		future.addListener(this.operationListener);
		try {
			this.operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
		} catch (Exception ex) {
			logger.error("destroy", ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scmp);
		encoderDecoder.encode(baos, scmp);
		url = new URL(Constants.HTTP, host, port, Constants.HTTP_FILE);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, this.url.getPath());
		byte[] buffer = baos.toByteArray();
		// Http header fields
		request.addHeader(HttpHeaders.Names.USER_AGENT, System.getProperty("java.runtime.version"));
		request.addHeader(HttpHeaders.Names.HOST, host);
		request.addHeader(HttpHeaders.Names.ACCEPT, Constants.HTTP_ACCEPT_PARAMS);
		request.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		request.addHeader(HttpHeaders.Names.CONTENT_TYPE, scmp.getBodyType().getMimeType());
		request.addHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.length));

		NettyHttpRequesterResponseHandler handler = channel.getPipeline().get(NettyHttpRequesterResponseHandler.class);
		handler.setCallback(callback);

		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);
		ChannelFuture future = channel.write(request);
		future.addListener(this.operationListener);
		try {
			this.operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
		} catch (CommunicationException ex) {
			logger.error("send", ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "send failed on " + this.localSocketAddress);
		}
		if (connectionLogger.isEnabledFull()) {
			connectionLogger.logWriteBuffer(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort(), buffer, 0, buffer.length);
		}
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

	/** {@inheritDoc} */
	@Override
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConnected() {
		if (this.channel == null) {
			return false;
		}
		return this.channel.isConnected();
	}

	/** {@inheritDoc} */
	@Override
	public int getNrOfIdlesInSequence() {
		return nrOfIdles;
	}

	/** {@inheritDoc} */
	@Override
	public void incrementNrOfIdles() {
		this.nrOfIdles++;
	}

	/** {@inheritDoc} */
	@Override
	public void resetNrOfIdles() {
		this.nrOfIdles = 0;
	}
}
