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

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyConnectionAdpater;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Timer;

/**
 * The Class NettyTcpConnection. Concrete connection implementation with JBoss
 * Netty for Tcp.
 */
public class NettyTcpConnection extends NettyConnectionAdpater {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpConnection.class);

	/**
	 * Instantiates a new NettyTcpConnection.
	 *
	 * @param channelFactory the channel factory
	 * @param timer          the timer
	 */
	public NettyTcpConnection(EventLoopGroup workerGroup, Timer timer) {
		super(workerGroup, timer);

	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(NettyConnectionAdpater.workerGroup);
		this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, baseConf.getConnectionTimeoutMillis());
		this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
		this.bootstrap.channel(NioSocketChannel.class);
		if (baseConf.getTcpKeepAliveInitiator() != null) {
			// TCP keep alive is configured - set it!
			this.bootstrap.option(ChannelOption.SO_KEEPALIVE, baseConf.getTcpKeepAliveInitiator());
		}
		this.bootstrap.handler(new NettyTcpRequesterPipelineFactory(this.connectionContext));

		// Start the connection attempt.
		this.remotSocketAddress = new InetSocketAddress(host, port);
		ChannelFuture future = bootstrap.connect(this.remotSocketAddress);
		operationListener = new NettyOperationListener();
		future.addListener(operationListener);
		try {
			this.channel = future.channel(); 
			operationListener.awaitUninterruptibly(baseConf.getConnectionTimeoutMillis());
			// complete remotSocketAddress
			this.remotSocketAddress = (InetSocketAddress) this.channel.remoteAddress();
		} catch (CommunicationException ex) {
			LOGGER.error("connect failed to " + this.remotSocketAddress.toString(), ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "connect to IP=" + this.remotSocketAddress.toString());
		}
		if (ConnectionLogger.isEnabled()) {
			ConnectionLogger.logConnect(this.getClass().getSimpleName(), this.remotSocketAddress.getHostName(), this.remotSocketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage scmp, ISCMPMessageCallback callback) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scmp);
		encoderDecoder.encode(baos, scmp);

		NettyTcpRequesterResponseHandler handler = channel.pipeline().get(NettyTcpRequesterResponseHandler.class);
		handler.setCallback(callback);

		ByteBuf chBuffer = Unpooled.buffer(baos.size());
		chBuffer.writeBytes(baos.toByteArray());
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logWriteBuffer(this.getClass().getSimpleName(), this.remotSocketAddress.getHostName(), this.remotSocketAddress.getPort(),
					chBuffer.array(), 0, chBuffer.array().length);
		}
		channel.writeAndFlush(chBuffer);
	}

	@Override
	public void setQuietDisconnect() throws Exception {
		// this avoids receiving messages (outstanding replies) in disconnecting procedure
		ChannelPipeline pipeline = channel.pipeline();
		
		// following null checks are necessary. In case the connection is already destroyed everything is nulled under the hood
		if(pipeline!= null) {
			NettyTcpRequesterResponseHandler handler = channel.pipeline().get(NettyTcpRequesterResponseHandler.class);
			
			if(handler != null) {
				handler.connectionDisconnect();
			}
		}		
	}
}
