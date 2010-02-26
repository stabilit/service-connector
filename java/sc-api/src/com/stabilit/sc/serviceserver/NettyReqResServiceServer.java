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
package com.stabilit.sc.serviceserver;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.sc.app.server.IServiceServerConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.registrar.RegistrarResponseHandler;
import com.stabilit.sc.registrar.SCRegistrar;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.ITimeoutHandler;
import com.stabilit.sc.serviceserver.handler.NettyResponseHandler;
import com.stabilit.sc.serviceserver.handler.SCKeepAliveHandler;

/**
 * @author JTraber
 * 
 */
class NettyReqResServiceServer implements IServiceServer, IServiceServerConnection {

	private ServerBootstrap bootstrap;
	private Channel channel;
	private String host;
	private int port;
	private String serviceName;

	public NettyReqResServiceServer(String host, int port) {
		this.bootstrap = null;
		this.channel = null;
		this.host = host;
		this.port = port;
	}

	public void start(String serviceName, Class<? extends IResponseHandler<IServiceServerConnection>> responseHandlerClass,
			Class<? extends ITimeoutHandler> timeoutHandlerClass, int keepAliveTimeout, int readTimeout, int writeTimeout) throws ServiceServerException {
		this.serviceName = serviceName;
		// Configure the server.
		this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.

		try {
			bootstrap.setPipelineFactory(new HttpServerPipelineFactory(responseHandlerClass, timeoutHandlerClass , SCKeepAliveHandler.class, 0, 0, 0, this));
		} catch (InstantiationException e) {
			throw new ServiceServerException("A parameter of type class is not instantiable.");
		} catch (Exception e) {
			throw new ServiceServerException(e);
		}
		
		this.channel = this.bootstrap.bind(new InetSocketAddress(host, port));
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new ServiceServerException("Error occured when Server stays in wait().", e);
			}
		}		
		
		SCRegistrar registrar = new SCRegistrar(serviceName,host,port, "localhost", 80, RegistrarResponseHandler.class);
		registrar.registerToSC(500);		
	}
	
	public IResponseHandler<?> getResponseHandler() {
		return channel.getPipeline().get(NettyResponseHandler.class).getCallback();
	}

	public void unregisterFromSC() {
		// TODO how?
	}

	public void destroy() throws Exception {
		// TODO how?
		this.channel.close();
	}

	@Override
	public void send(SCMP scmp) {
		throw new UnsupportedOperationException();		
	}
}
