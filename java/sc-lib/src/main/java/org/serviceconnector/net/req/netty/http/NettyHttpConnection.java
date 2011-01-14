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
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.Timer;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyConnectionAdpater;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class NettyHttpClientConnection. Concrete connection implementation with JBoss Netty for Http.
 * 
 * @author JTraber
 */
public class NettyHttpConnection extends NettyConnectionAdpater {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyHttpConnection.class);

	/** The url. */
	private URL url;

	/**
	 * Instantiates a new netty http connection.
	 */
	public NettyHttpConnection(NioClientSocketChannelFactory channelFactory, Timer timer) {
		super(channelFactory, timer);
		this.url = null;
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		this.bootstrap = new ClientBootstrap(channelFactory);
		this.bootstrap.setOption("connectTimeoutMillis", baseConf.getConnectionTimeoutMillis());
		this.pipelineFactory = new NettyHttpRequesterPipelineFactory(this.connectionContext, NettyConnectionAdpater.timer);
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
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "connect to IP="
					+ this.localSocketAddress.toString());
		}
		if (ConnectionLogger.isEnabled()) {
			ConnectionLogger.logConnect(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage scmp, ISCMPMessageCallback callback) throws Exception {
		// logger.info("send cache id = " + scmp.getCacheId());
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
		channel.write(request);
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logWriteBuffer(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort(), buffer, 0, buffer.length);
		}
		return;
	}
}
