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
package org.serviceconnector.net.req.netty.tcp;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.Timer;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyConnectionAdpater;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class NettyTcpConnection. Concrete connection implementation with JBoss Netty for Tcp.
 */
public class NettyTcpConnection extends NettyConnectionAdpater {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpConnection.class);

	/**
	 * Instantiates a new NettyTcpConnection.
	 */
	public NettyTcpConnection(NioClientSocketChannelFactory channelFactory, Timer timer) {
		super(channelFactory, timer);

	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		this.bootstrap = new ClientBootstrap(NettyTcpConnection.channelFactory);
		this.pipelineFactory = new NettyTcpRequesterPipelineFactory(this.connectionContext, NettyTcpConnection.timer);
		this.bootstrap.setPipelineFactory(this.pipelineFactory);
		this.bootstrap.setOption("connectTimeoutMillis", baseConf.getConnectionTimeoutMillis());
		// Start the connection attempt.
		this.localSocketAddress = new InetSocketAddress(host, port);
		ChannelFuture future = bootstrap.connect(this.localSocketAddress);
		operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		try {
			this.channel = operationListener.awaitUninterruptibly(baseConf.getConnectionTimeoutMillis()).getChannel();
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
	public void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception {
		//logger.info("send cache id = " + scmp.getCacheId());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scmp);
		encoderDecoder.encode(baos, scmp);

		NettyTcpRequesterResponseHandler handler = channel.getPipeline().get(NettyTcpRequesterResponseHandler.class);
		handler.setCallback(callback);

		ChannelBuffer chBuffer = ChannelBuffers.buffer(baos.size());
		chBuffer.writeBytes(baos.toByteArray());
		ChannelFuture future = channel.write(chBuffer);
		future.addListener(operationListener);
		try {
			operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
		} catch (CommunicationException ex) {
			logger.error("send", ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "send failed on " + this.localSocketAddress);
		}
		if (connectionLogger.isEnabledFull()) {
			connectionLogger.logWriteBuffer(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort(), chBuffer.toByteBuffer().array(), 0, chBuffer.toByteBuffer().array().length);
		}
	}
}
