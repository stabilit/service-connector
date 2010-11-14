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
package org.serviceconnector.net.res.netty.tcp.proxy;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.SystemConfigurationException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class NettyTcpProxyEndpoint.
 */
public class NettyTcpProxyEndpoint extends EndpointAdapter implements Runnable {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpProxyEndpoint.class);

	/** Queue to store the answer. */
	private ArrayBlockingQueue<Boolean> answer;
	/** The bootstrap. */
	private ServerBootstrap bootstrap;
	/** The channel. */
	private Channel channel;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The host. */
	private String remoteHost;
	/** The port. */
	private int remotePort;
	/** The max connection pool size. */
	private int maxConnectionPoolSize;
	private Thread serverThread;
	/** The channel factory. */
	private NioServerSocketChannelFactory serverChannelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());

	private NioClientSocketChannelFactory clientChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());

	/**
	 * Instantiates a new NettyTcpProxyEndpoint.
	 */
	public NettyTcpProxyEndpoint() {
		this.bootstrap = null;
		this.channel = null;
		this.host = null;
		this.port = 0;
		this.remoteHost = null;
		this.remotePort = 0;
		this.maxConnectionPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
		this.answer = new ArrayBlockingQueue<Boolean>(1);
		this.serverThread = new Thread(this);
	}

	/** {@inheritDoc} */
	@Override
	// TODO TRN why is this necessary here or missing in the other end points?
	public void setResponder(IResponder resp) {
		super.setResponder(resp);
		CommunicatorConfig remoteHostConfig = null;
		try {
			CommunicatorConfig communicatorConfig = resp.getResponderConfig();
			remoteHostConfig = communicatorConfig.getRemoteHostConfiguration();
			if (remoteHostConfig == null) {
				throw new SystemConfigurationException("no remote host configuration");
			}
			String remoteHost = remoteHostConfig.getHost();
			int remotePort = remoteHostConfig.getPort();
			this.remoteHost = remoteHost;
			this.remotePort = remotePort;
			this.maxConnectionPoolSize = communicatorConfig.getMaxPoolSize();
			if (this.maxConnectionPoolSize < 1) {
				this.maxConnectionPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
				;
			}
			// limit threads
			serverChannelFactory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(this.maxConnectionPoolSize),
					Executors.newFixedThreadPool(this.maxConnectionPoolSize));
			// no thread limit required
			this.clientChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
					.newCachedThreadPool());

		} catch (Exception e) {
			logger.error("setResponder", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		this.bootstrap = new ServerBootstrap(serverChannelFactory);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpProxyResponderPipelineFactory(clientChannelFactory, remoteHost, remotePort));
	}

	/** {@inheritDoc} */
	@Override
	public void startsListenAsync() throws Exception {
		this.serverThread.start();
		Boolean bool = null;
		try {
			bool = this.answer.poll(Constants.CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "listener could not start up succesfully");
		}
		if (bool == null) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "startup listener timed out");
		}
		if (bool == false) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "listener could not start up succesfully");
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void startListenSync() throws Exception {
		try {
			this.channel = this.bootstrap.bind(new InetSocketAddress(this.host, this.port));
			// adds responder to registry
			ResponderRegistry responderRegistry = AppContext.getCurrentContext().getResponderRegistry();
			responderRegistry.addResponder(this.channel.getId(), this.resp);
		} catch (Exception ex) {
			this.answer.add(Boolean.FALSE);
			throw ex;
		}
		this.answer.add(Boolean.TRUE);
		synchronized (this) {
			wait();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			startListenSync();
		} catch (Exception ex) {
			logger.error("start listening", ex);
			this.destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.stopListening();
		this.bootstrap.releaseExternalResources();
		this.serverChannelFactory.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() { // TODO TRN slightly different code as NettyTcpEndpoint!!! Why?
		try {
			if (this.channel != null) {
				// removes responder to registry
				ResponderRegistry responderRegistry = AppContext.getCurrentContext().getResponderRegistry();
				responderRegistry.removeResponder(this.channel.getId());
				ChannelFuture future = this.channel.close();
				NettyOperationListener operationListener = new NettyOperationListener();
				future.addListener(operationListener);
				try {
					operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
				} catch (CommunicationException ex) {
					logger.error("disconnect", ex); // stopListening must continue
				}
			}
			if (this.serverThread != null) {
				this.serverThread.interrupt();
			}
		} catch (Exception ex) {
			logger.error("stop listening", ex); // stopListening must continue
			return;
		}
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
