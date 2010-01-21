package com.stabilit.netty.perf.http.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.sun.net.httpserver.HttpServer;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpServer}.
 * 
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1$, $Date: 2009-10-20:28:+0(Thu, Oct 2009) $
 */
public class NettyHttpClient implements Runnable {

	public static int numberOfMsg;

	public static void main(String[] args) throws Exception {
		NettyHttpClient client = new NettyHttpClient(10000);
		client.run();
	}

	public NettyHttpClient(int numberOfMsg) {
		NettyHttpClient.numberOfMsg = numberOfMsg;
	}

	public void run() {

		String host = "localhost";
		int port = 8066;

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors
						.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory());

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));

		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}
		sendMessage(channel);
	}

	/**
	 * @param bootstrap
	 * @param channel
	 */
	private static void sendMessage(Channel channel) {
		String path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/ThePro.html";
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, path);

		// Write the response.
		ChannelFuture future = channel.write(request);
	}
}