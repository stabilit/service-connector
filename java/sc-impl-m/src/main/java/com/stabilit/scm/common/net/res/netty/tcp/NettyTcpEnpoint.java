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
package com.stabilit.scm.common.net.res.netty.tcp;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.res.ResponderRegistry;
import com.stabilit.scm.common.res.EndpointAdapter;

/**
 * The Class NettyTcpEnpoint. Concrete responder implementation with JBoss Netty for Tcp.
 * 
 * @author JTraber
 */
public class NettyTcpEnpoint extends EndpointAdapter implements Runnable {

	/** The bootstrap. */
	private ServerBootstrap bootstrap;
	/** The channel. */
	private Channel channel;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The channel factory. */
	private static NioServerSocketChannelFactory channelFactory;

	/**
	 * Instantiates a new NettyTcpEnpoint.
	 */
	public NettyTcpEnpoint() {
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
	}
	
	/**
	 * Instantiates a new NettyTcpEnpoint.
	 */
	public NettyTcpEnpoint(NioServerSocketChannelFactory channelFactory) {
		this();
		NettyTcpEnpoint.channelFactory = channelFactory;
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		this.bootstrap = new ServerBootstrap(channelFactory);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpResponderPipelineFactory());
	}

	/** {@inheritDoc} */
	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	/** {@inheritDoc} */
	@Override
	public void runSync() throws InterruptedException {
		this.channel = this.bootstrap.bind(new InetSocketAddress(this.host, this.port));
		// adds server to registry
		ResponderRegistry serverRegistry = ResponderRegistry.getCurrentInstance();
		serverRegistry.addResponder(this.channel.getId(), this.resp);
		synchronized (this) {
			wait();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			runSync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
			this.destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		try {
			this.channel.close();
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(this, th);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NettyTcpEnpoint();
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}
}
