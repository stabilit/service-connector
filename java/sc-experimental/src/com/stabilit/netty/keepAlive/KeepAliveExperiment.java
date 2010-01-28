/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.netty.keepAlive;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * @author JTraber
 * 
 */
public class KeepAliveExperiment {

	public static void main(String[] args) {
		KeepAliveExperiment experiment = new KeepAliveExperiment();
		experiment.run();
	}

	public void run() {
		setupServerNetty();

		// Configure the client.
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool());

		DefaultChannelPipeline pipe = new DefaultChannelPipeline();
		pipe.addLast("timeout", new ClientTimeoutHandler(new HashedWheelTimer(), 0, 0, 10));
		pipe.addLast("handler", new ClientHandlerNetty());
		Channel channel = factory.newChannel(pipe);

		// Start the connection attempt.
		ChannelFuture future = channel.connect(new InetSocketAddress("localhost", 9999));

		// Wait until the connection attempt succeeds or fails.
		future.awaitUninterruptibly();
		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			return;
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(2);
		buffer.writeChar('M');
		channel.write(buffer);
		channel.write(buffer);
	}

	private void setupServerNetty() {
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(1), Executors
				.newFixedThreadPool(2));
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.getPipeline()
				.addLast("timeout", new ServerTimeoutHandler(new HashedWheelTimer(), 0, 0, 20));
		bootstrap.getPipeline().addLast("ServerKeepAliveHandler", new ServerMsgHandler());
		bootstrap.bind(new InetSocketAddress(9999));
	}

	@ChannelPipelineCoverage("all")
	private class ServerMsgHandler extends SimpleChannelHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			char msgType = 0;
			while (buf.readable())
				msgType = (char) buf.readByte();

			if (msgType == 'A')
				System.out.println("Keep Alive received on Server");
			else {
				System.out.println("Message received on Server");
				ChannelBuffer bufS = ChannelBuffers.buffer(2);
				bufS.writeChar('M');
				ctx.getChannel().write(bufS);
			}
		}
	}

	private class ServerTimeoutHandler extends IdleStateHandler {

		public ServerTimeoutHandler(Timer timer, int readerIdleTimeSeconds, int writerIdleTimeSeconds,
				int allIdleTimeSeconds) {
			super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		}

		@Override
		protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
				throws Exception {
			System.out.println("server: nothing happens for 20 sec. :" + lastActivityTimeMillis);
			ChannelBuffer buf = ChannelBuffers.buffer(2);
			buf.writeChar('A');
			ctx.getChannel().write(buf);
		}
	}

	@ChannelPipelineCoverage("all")
	private class ClientHandlerNetty extends SimpleChannelHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			super.messageReceived(ctx, e);
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			char msgType = 0;
			while (buf.readable())
				msgType = (char) buf.readByte();
			
			if (msgType == 'A')
				System.out.println("Keep Alive received on Client");
			else {
				System.out.println("Message received on Client");				
			}
		}
	}

	private class ClientTimeoutHandler extends IdleStateHandler {

		public ClientTimeoutHandler(Timer timer, int readerIdleTimeSeconds, int writerIdleTimeSeconds,
				int allIdleTimeSeconds) {
			super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		}

		@Override
		protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
				throws Exception {
			System.out.println("client: nothing happens for 10 sec. :" + lastActivityTimeMillis / 1000);
			ChannelBuffer buf = ChannelBuffers.buffer(2);
			buf.writeChar('A');
			ctx.getChannel().write(buf);
		}
	}
}
