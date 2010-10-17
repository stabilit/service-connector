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
package org.serviceconnector.net.res.netty.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class NettyTcpEndpoint. Concrete responder implementation with JBoss
 * Netty for Tcp.
 * 
 * @author JTraber
 */
public class NettyTcpEndpoint extends EndpointAdapter implements Runnable {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyTcpEndpoint.class);
	/** Queue to store the answer. */
	private ArrayBlockingQueue<Boolean> answer;
	private Thread serverThread;

	/** The bootstrap. */
	private ServerBootstrap bootstrap;
	/** The channel. */
	private Channel channel;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The channel factory. */
	private NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());

	/**
	 * Instantiates a new NettyTcpEndpoint.
	 */
	public NettyTcpEndpoint() {
		this.bootstrap = null;
		this.channel = null;
		this.port = 0;
		this.host = null;
		this.answer = new ArrayBlockingQueue<Boolean>(1);
		this.serverThread = new Thread(this);
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		this.bootstrap = new ServerBootstrap(channelFactory);
		// Set up the event pipeline factory.
		this.bootstrap.setPipelineFactory(new NettyTcpResponderPipelineFactory());
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
			// adds server to registry
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
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.stopListening();
		this.bootstrap.releaseExternalResources();
		this.channelFactory.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
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
				} catch (CommunicationException ex) { //TODO TRN should this exception be handled or signaled?
					logger.error("disconnect", ex);
				}
			}
			if (this.serverThread != null) {
				this.serverThread.interrupt();
			}
		} catch (Exception ex) {
			logger.error("stop listening", ex);
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
