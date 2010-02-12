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
package com.stabilit.sc.pool;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.stabilit.sc.app.client.netty.http.ClientPoolPipelineFactory;

public class NettyPoolImpl implements IPool {
	private ClientBootstrap bootstrap;
	private URL url = null;

	/**
	 * @param numOfConn
	 */
	public NettyPoolImpl(URL url, Class<? extends IResponseHandler> respHandlerClass,
			Class<? extends IKeepAliveHandler> kAHandlerClass) {
		this.url = url;
		// Configure the client.
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new ClientPoolPipelineFactory(respHandlerClass, kAHandlerClass));
	}

	public Connection createConnection() throws Exception {
		String host = url.getHost();
		int port = url.getPort();

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

		NettyConnection conn = new NettyConnection();
		// Wait until the connection attempt succeeds or fails.
		conn.setChannel(future.awaitUninterruptibly().getChannel());
		ResponseHandlerWrapper resHandWrapper = ((ResponseHandlerWrapper) future.getChannel().getPipeline()
				.get("handler"));
		resHandWrapper.setConn(conn);
		conn.setRespHandler(resHandWrapper.getHandler());

		ClientKeepAliveHandlerWrapper kAHandWrapper = ((ClientKeepAliveHandlerWrapper) future.getChannel()
				.getPipeline().get("timeout"));
		kAHandWrapper.setConn(conn);
		conn.setKAHandler(kAHandWrapper.getHandler());
		
		if (!future.isSuccess()) {
			Exception e = (Exception) future.getCause();
			future.getCause().printStackTrace();
			this.bootstrap.releaseExternalResources();
			throw e;
		}
		return conn;
	}

	@Override
	public void setEndpoint(URL url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.pool.IPool#closeConnection(com.stabilit.sc.pool.Connection)
	 */
	@Override
	public void closeConnection(Connection conn) throws Exception {
		((NettyConnection)conn).getChannel().disconnect().awaitUninterruptibly();
	}
}
