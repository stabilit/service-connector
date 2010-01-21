package com.stabilit.netty.timeout.http.client;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.queue.Request;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpClient {

	public static void main(String[] args) throws Exception {
		NettyHttpClient client = new NettyHttpClient();
		client.run();
	}

	public void run() {

		String host = "localhost";
		int port = 8066;

		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors
						.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory());
		bootstrap.setOption("connectTime", 2);
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));

		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();
		ChannelConfig config = channel.getConfig();
		config.setConnectTimeoutMillis(2);
		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}
		try {
			sendMessage(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(Channel channel) throws Exception {

		Request request = new Request("clientName", "msg", 0, new Date(), null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, request);
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, "/");

		byte[] buffer = baos.toByteArray();
		httpRequest.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		httpRequest.setContent(channelBuffer);
		ChannelFuture future = channel.write(httpRequest);
		if (future.await(2))
			System.out.println("true");
	}
}