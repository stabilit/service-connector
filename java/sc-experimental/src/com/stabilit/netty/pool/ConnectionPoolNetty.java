package com.stabilit.netty.pool;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class ConnectionPoolNetty
{
	final private static ChannelFactory tcpChannelFactory = new NioClientSocketChannelFactory(
			Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
	final private static NavigableSet<Channel> cacheTCP = new TreeSet<Channel>();
	private static int tcpConnectionCount = 20;
	public static boolean reuse = false;

	public static Channel borrowSocketConnector(ChannelHandler ioHandler)
	{
		while (true)
		{
			if (tcpConnectionCount > 0)
			{
				Channel tcpChannel = createTCPChannel(ioHandler);
				tcpConnectionCount--;
				return tcpChannel;
			}
			// Connection limit reached
			else
			{
				Channel ch;
				synchronized (cacheTCP)
				{
					ch = cacheTCP.pollFirst();
				}
				if (ch != null)
				{
					if (!reuse)
						ch = createTCPChannel(ioHandler);
					// reopen and somehow we need to add a handler
					return ch;
				}
				else
				{
					try
					{
						synchronized (cacheTCP)
						{
							cacheTCP.wait();
						}
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						return null;
					}
				}
			}
		}
	}

	private static Channel createTCPChannel(ChannelHandler handler)
	{
		ChannelPipeline pipe = createPipeline(handler);
		Channel tcpChannel = tcpChannelFactory.newChannel(pipe);
		
		tcpChannel.getConfig().setOption("reuseAddress", true);
		tcpChannel.getCloseFuture().addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
			{
				synchronized (cacheTCP)
				{
					cacheTCP.add(future.getChannel());
					cacheTCP.notify();
				}
			}
		});
		return tcpChannel;
	}

	private static ChannelPipeline createPipeline(ChannelHandler handler)
	{
		ChannelPipeline pipe = new DefaultChannelPipeline();
		pipe.addLast("handler", handler);
		return pipe;
	}
}
