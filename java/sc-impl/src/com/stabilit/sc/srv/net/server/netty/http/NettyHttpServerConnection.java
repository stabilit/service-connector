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
package com.stabilit.sc.srv.net.server.netty.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.srv.registry.ServerRegistry.ServerRegistryItem;
import com.stabilit.sc.srv.server.ServerConnectionAdapter;

/**
 * The Class NettyHttpServerConnection. Concrete server connection implementation with JBoss Netty for Http.
 * 
 * @author JTraber
 */
public class NettyHttpServerConnection extends ServerConnectionAdapter implements Runnable {

	/** The bootstrap. */
	private ServerBootstrap bootstrap;
	/** The channel. */
	private Channel channel;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The numberOfThreads. */
	private int numberOfThreads;
	/** The channel factory. */
	NioServerSocketChannelFactory channelFactory;

	/**
	 * Instantiates a new netty http server connection.
	 */
	public NettyHttpServerConnection() {
		this.bootstrap = null;
		this.channel = null;
		this.host = null;
		this.port = 0;
		this.numberOfThreads = 10;
		this.channelFactory = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#create()
	 */
	@Override
	public void create() {
		// Configure the server.
		channelFactory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(numberOfThreads),
				Executors.newFixedThreadPool(numberOfThreads / 4));
		this.bootstrap = new ServerBootstrap(channelFactory);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyHttpServerPipelineFactory());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#runAsync()
	 */
	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#runSync()
	 */
	@Override
	public void runSync() throws InterruptedException {
		this.channel = this.bootstrap.bind(new InetSocketAddress(host, this.port));
		// adds server to registry
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		serverRegistry.add(this.channel.getId(), new ServerRegistryItem(this.server));
		synchronized (this) {
			wait();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			runSync();
		} catch (Exception e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
			try {
				this.destroy();
			} catch (Throwable e1) {
				ExceptionListenerSupport.getInstance().fireException(this, e1);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.channel.close();
		this.bootstrap.releaseExternalResources();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return new NettyHttpServerConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setNumberOfThreads(int)
	 */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setPort(int)
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}
}
