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
package com.stabilit.netty.pool;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author JTraber
 * 
 */
public class NettyExperiment {

	public static void main(String[] args) {
		NettyExperiment experiment = new NettyExperiment();
		experiment.run();
	}

	public void run() {
		setupServerNetty();

		// Configure the client.
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool());

		DefaultChannelPipeline pipe = new DefaultChannelPipeline();
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
		ChannelBuffer buffer;

		for (int i = 1; i < 1000000; i++) {
			buffer = ChannelBuffers.buffer(1);
			buffer.writeByte(new Integer(i).byteValue());			
			channel.write(buffer);
		}

		/**************************************************************************************************/

		DefaultChannelPipeline pipe1 = new DefaultChannelPipeline();
		pipe1.addLast("handler1", new ClientHandlerNetty());
		Channel channel1 = factory.newChannel(pipe1);

		ChannelFuture future1 = channel1.connect(new InetSocketAddress("localhost", 9999));

		future1.awaitUninterruptibly();
		if (!future1.isSuccess()) {
			future1.getCause().printStackTrace();
			return;
		}
		ChannelBuffer buffer1 = ChannelBuffers.buffer(128);
		buffer1.writeInt(3535);
		channel1.write(buffer1);
		// for (int i = 0; i < 100000; ++i) {
		// final Channel ch = ConnectionPoolNetty.borrowSocketConnector(new ClientHandlerNetty());
		// ch.connect(new InetSocketAddress("127.0.0.1", 8080)).awaitUninterruptibly();
		// ChannelBuffer buf = ChannelBuffers.buffer(1);
		// buf.writeByte((byte) '.');
		// ch.write(buf).addListener(new ChannelFutureListener() {
		// @Override
		// public void operationComplete(ChannelFuture arg0) throws Exception {
		// ch.disconnect();
		// }
		// });
		// }
	}

	private void setupServerNetty() {
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(1), Executors
				.newFixedThreadPool(2));
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.getPipeline().addLast("handler", new ServerHandlerNetty());
		bootstrap.bind(new InetSocketAddress(9999));
	}

	@ChannelPipelineCoverage("all")
	private class ServerHandlerNetty extends SimpleChannelHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			while (buf.readable())
				System.out.print(new Integer(buf.readByte()));
			System.out.println(e.getRemoteAddress());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
		}
	}

	@ChannelPipelineCoverage("all")
	private class ClientHandlerNetty extends SimpleChannelHandler {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
		}
	}
}
