package com.stabilit.netty.http.client;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.queue.Request;
import com.stabilit.sc.util.ObjectStreamHttpUtil;
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
	public String clientName;
	public int count;

	public static void main(String[] args) throws Exception {
		NettyHttpClient client;
		
		if(args.length != 0) {
			client = new NettyHttpClient(Integer.valueOf(args[0]), args[1]);
		} else {
			client = new NettyHttpClient(10000, "NettyClient");
		}
		
		client.run();
	}

	public NettyHttpClient(int numberOfMsg, String clientName) {
		NettyHttpClient.numberOfMsg = numberOfMsg;
		this.clientName = clientName;
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
		try {
			for (count = 0; count < numberOfMsg; count++) {
				sendMessage(channel);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param bootstrap
	 * @param channel
	 * @throws Exception
	 */
	private void sendMessage(Channel channel) throws Exception {

		Request request = new Request(clientName, "msg", count, new Date(), null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, request);
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, "/");

		byte[] buffer = baos.toByteArray();
		httpRequest.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		httpRequest.setContent(channelBuffer);
		channel.write(httpRequest);
	}
}