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

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

/**
 * The Class NettyTcpProxyResponderRequestHandler.
 */
public class NettyTcpProxyResponderRequestHandler extends SimpleChannelUpstreamHandler {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(NettyTcpProxyResponderRequestHandler.class);

	/** The cf. */
	private final ClientSocketChannelFactory cf;

	/** The remote host. */
	private final String remoteHost;

	/** The remote port. */
	private final int remotePort;

	/** The outbound channel. */
	private volatile Channel outboundChannel;

	/**
	 * Instantiates a new netty tcp proxy responder request handler.
	 * 
	 * @param cf
	 *            the cf
	 * @param remoteHost
	 *            the remote host
	 * @param remotePort
	 *            the remote port
	 */
	public NettyTcpProxyResponderRequestHandler(ClientSocketChannelFactory cf, String remoteHost, int remotePort) {
		this.cf = cf;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	/** {@inheritDoc} */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// Suspend incoming traffic until connected to the remote host.
		final Channel inboundChannel = e.getChannel();
		inboundChannel.setReadable(false);

		// Start the connection attempt.
		ClientBootstrap cb = new ClientBootstrap(cf);
		cb.getPipeline().addLast("handler", new OutboundHandler(e.getChannel()));
		ChannelFuture f = cb.connect(new InetSocketAddress(remoteHost, remotePort));

		outboundChannel = f.getChannel();
		f.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					// Connection attempt succeeded:
					// Begin to accept incoming traffic.
					inboundChannel.setReadable(true);
				} else {
					// Close the connection if the connection attempt has
					// failed.
					inboundChannel.close();
				}
			}
		});
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (this.outboundChannel != null) {
			try {
				this.outboundChannel.close();
			} catch (Exception ex) {
                LOGGER.error("outboundChannel close", ex);
			}
		}
		super.channelClosed(ctx, e);
	}

	/**
	 * Message received.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		// needs to set a key in thread local to identify thread later and get
		// access to the responder
		ChannelBuffer msg = (ChannelBuffer) event.getMessage();
		outboundChannel.write(msg);
	}

	/**
	 * Exception caught.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param e
	 *            the e
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		if (th instanceof ClosedChannelException) {
			// never reply in case of channel closed exception
			return;
		}
		if (th instanceof java.io.IOException) {
			LOGGER.warn(th); // regular disconnect causes this expected exception
			return;
		} else {
			LOGGER.error("Responder error", th);
		}
	}

	/**
	 * The Class OutboundHandler.
	 */
	private static class OutboundHandler extends SimpleChannelUpstreamHandler {

		/** The inbound channel. */
		private final Channel inboundChannel;

		/**
		 * Instantiates a new outbound handler.
		 * 
		 * @param inboundChannel
		 *            the inbound channel
		 */
		OutboundHandler(Channel inboundChannel) {
			this.inboundChannel = inboundChannel;
		}

		/** {@inheritDoc} */
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			ChannelBuffer msg = (ChannelBuffer) e.getMessage();
			inboundChannel.write(msg);
		}

		/** {@inheritDoc} */
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			closeOnFlush(inboundChannel);
		}

		/** {@inheritDoc} */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			e.getCause().printStackTrace();
			closeOnFlush(e.getChannel());
		}

		/**
		 * Closes the specified channel after all queued write requests are flushed.
		 * 
		 * @param ch
		 *            the ch
		 */
		static void closeOnFlush(Channel ch) {
			if (ch.isConnected()) {
				ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}
}