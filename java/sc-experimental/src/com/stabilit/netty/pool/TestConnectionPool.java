package com.stabilit.netty.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.junit.Assert;
import org.junit.Test;

public class TestConnectionPool {
	@Test
	public void testConnectionPoolNetty() {
		try {
			setupServerNetty();
			ConnectionPoolNetty.reuse = false;
			for (int i = 0; i < 100000; ++i) {
				final Channel ch = ConnectionPoolNetty.borrowSocketConnector(new EmptyHandlerNetty());
				ch.connect(new InetSocketAddress("127.0.0.1", 8080)).awaitUninterruptibly();
				ChannelBuffer buf = ChannelBuffers.buffer(1);
				buf.writeByte((byte) '.');
				ch.write(buf).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture arg0) throws Exception {
						ch.disconnect();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void setupServerNetty() {
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.getPipeline().addLast("handler", new ServerHandlerNetty());
		bootstrap.bind(new InetSocketAddress(8080));
	}

	@ChannelPipelineCoverage("all")
	private class ServerHandlerNetty extends SimpleChannelHandler {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			while (buf.readable())
				System.out.print((char) buf.readByte());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
		}
	}

	@ChannelPipelineCoverage("all")
	private class EmptyHandlerNetty extends SimpleChannelHandler {
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
		}
	}
}
