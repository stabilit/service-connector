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
package org.serviceconnector.net.res.netty.web;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.res.EndpointAdapter;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.scmp.SCMPError;


/**
 * The Class NettyWebEndpoint.
 */
public class NettyWebEndpoint extends EndpointAdapter implements Runnable {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyWebEndpoint.class);

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
	/** The channel factory. */
	private NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors
			.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_SERVER), Executors
			.newFixedThreadPool(Constants.DEFAULT_NR_OF_THREADS_SERVER));;

	/**
	 * Instantiates a new netty web endpoint.
	 */
	public NettyWebEndpoint() {
		this.bootstrap = null;
		this.channel = null;
		this.host = null;
		this.port = 0;
		this.answer = new ArrayBlockingQueue<Boolean>(1);
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		this.bootstrap = new ServerBootstrap(channelFactory);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyWebResponderPipelineFactory());
	}

	/** {@inheritDoc} */
	@Override
	public void startsListenAsync() throws Exception {
		Thread serverThread = new Thread(this);
		serverThread.start();
		Boolean bool = null;
		try {
			bool = this.answer.poll(Constants.CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"listener could not start up succesfully");
		}
		if (bool == null) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "startup listener timed out");
		}
		if (bool == false) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"listener could not start up succesfully");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListenSync() throws Exception {
		try {
			this.channel = this.bootstrap.bind(new InetSocketAddress(this.host, this.port));
			// adds responder to registry
			ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
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
			logger.error("run", ex);
			this.destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.stopListening();
		this.channelFactory.releaseExternalResources();
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
		try {
			if (this.channel != null) {
				this.channel.close();
			}
		} catch (Exception ex) {
			logger.error("stoppListening", ex);
			return;
		}
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NettyWebEndpoint();
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
