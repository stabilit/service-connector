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
package org.serviceconnector.net.res.netty.tcp.proxy;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.SocketChannel;

import org.serviceconnector.ctx.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NettyTcpProxyResponderRequestHandler.
 */
public class NettyTcpProxyResponderRequestHandler extends ChannelInboundHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpProxyResponderRequestHandler.class);

	private EventLoopGroup workerGroup;

	/** The remote host. */
	private final String remoteHost;

	/** The remote port. */
	private final int remotePort;

	/** The outbound channel. */
	private volatile Channel outboundChannel;

	/**
	 * Instantiates a new netty tcp proxy responder request handler.
	 *
	 * @param remoteHost the remote host
	 * @param remotePort the remote port
	 */
	public NettyTcpProxyResponderRequestHandler(String remoteHost, int remotePort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.workerGroup =  new NioEventLoopGroup(AppContext.getBasicConfiguration().getMaxIOThreads());
	}

	/** {@inheritDoc} */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Suspend incoming traffic until connected to the remote host.
		final Channel inboundChannel = ctx.channel();
		inboundChannel.config().setAutoRead(false);

		// Start the connection attempt.
		Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class).group(this.workerGroup).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(workerGroup, new OutboundHandler(ctx.channel()));
            }
        });
		 // Start the client.
        ChannelFuture f = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort));
        f.await(); // avoids writing before channel connect is completed

        outboundChannel = f.channel();
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					// Connection attempt succeeded:
					// Begin to accept incoming traffic.
					inboundChannel.config().setAutoRead(true);
					inboundChannel.read();
				} else {
					// Close the connection if the connection attempt has
					// failed.
					inboundChannel.close();
				}
			}
		});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (this.outboundChannel != null) {
			try {
				this.outboundChannel.close();
			} catch (Exception ex) {
				LOGGER.error("outboundChannel close", ex);
			}
		}
		super.channelInactive(ctx);
	}

	/**
	 * Message received.
	 *
	 * @param ctx the ctx
	 * @throws Exception the exception {@inheritDoc}
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		outboundChannel.writeAndFlush(msg);
	}

	/**
	 * Exception caught.
	 *
	 * @param ctx the ctx
	 * @param th the th
	 * @throws Exception the exception {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof java.io.IOException) {
			LOGGER.info("regular disconnect", th); // regular disconnect causes this expected exception
			return;
		} else {
			LOGGER.error("Responder error", th);
		}
	}

	/**
	 * The Class OutboundHandler.
	 */
	private static class OutboundHandler extends ChannelInboundHandlerAdapter  {

		/** The inbound channel. */
		private final Channel inboundChannel;

		/**
		 * Instantiates a new outbound handler.
		 *
		 * @param inboundChannel the inbound channel
		 */
		OutboundHandler(Channel inboundChannel) {
			this.inboundChannel = inboundChannel;
		}

		/** {@inheritDoc} */
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			inboundChannel.writeAndFlush(msg);
		}

		/** {@inheritDoc} */
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			closeOnFlush(inboundChannel);
		}

		/** {@inheritDoc} */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
			if (th instanceof ClosedChannelException) {
				// nothing special - just ignore.
				return;
			}
			if (th instanceof java.io.IOException) {
				// regular disconnect, nothing special - just ignore.
				return;
			} else {
				LOGGER.error("Responder error", th);
			}
			closeOnFlush(ctx.channel());
		}

		/**
		 * Closes the specified channel after all queued write requests are flushed.
		 *
		 * @param ch the ch
		 */
		static void closeOnFlush(Channel ch) {
			if (ch.isActive()) {
				ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}
}
