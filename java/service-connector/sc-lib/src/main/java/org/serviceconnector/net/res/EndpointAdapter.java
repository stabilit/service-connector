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
package org.serviceconnector.net.res;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.serviceconnector.Constants;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.req.netty.NettyOperationListener;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.XMLDumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * The Class EndpointAdapter. Provides basic functionality for endpoints.
 *
 * @author JTraber
 */
public abstract class EndpointAdapter implements IEndpoint, Runnable {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(EndpointAdapter.class);
	/** The base conf. */
	protected final BasicConfiguration baseConf = AppContext.getBasicConfiguration();

	/** The host. */
	protected String host;
	/** The port. */
	protected int port;
	/** The server thread. */
	protected Thread serverThread;
	/** Queue to store the answer. */
	protected ArrayBlockingQueue<Boolean> answer;
	/** The bootstrap. */
	protected ServerBootstrap bootstrap;
	/** The channel. */
	protected ChannelFuture channel;
	/** The responder. */
	protected IResponder resp;

	protected EventLoopGroup bossGroup;
	protected EventLoopGroup workerGroup;

	/**
	 * Instantiates a new EndpointAdapter.
	 */
	public EndpointAdapter() {
		this.resp = null;
		this.port = 0;
		this.host = null;
		this.bootstrap = null;
		this.channel = null;
		this.answer = new ArrayBlockingQueue<Boolean>(1);
		this.serverThread = new Thread(this, this.getClass().getSimpleName());
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		this.bootstrap = new ServerBootstrap();

		this.bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.TCP_NODELAY, true);

		if (baseConf.getTcpKeepAliveListener() != null) {
			// TCP keep alive for incoming connections is configured - set it!
			this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, baseConf.getTcpKeepAliveListener());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			this.startListenSync();
		} catch (Exception ex) {
			LOGGER.error("start listening", ex);
			this.destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListenSync() throws Exception {
		try {
			// Start the server.
			this.channel = this.bootstrap.bind(new InetSocketAddress(this.host, this.port));
		} catch (Exception ex) {
			this.answer.add(Boolean.FALSE);
			throw ex;
		}
		this.answer.add(Boolean.TRUE);
		synchronized (this) {
			try {
				LOGGER.info("Endpoint started host=" + host + ":" + port);
				wait();
			} catch (InterruptedException e) {
				LOGGER.warn("Interrupt exception in Listener, host:port=" + host + ":" + port + " " + e.toString());
			}
			LOGGER.info("Endpoint stopped host=" + host + ":" + port);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startsListenAsync() throws Exception {
		this.serverThread.start();
		Boolean bool = null;
		try {
			bool = this.answer.poll(baseConf.getConnectionTimeoutMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"cannot start listener on port=" + this.port);
		}
		if (bool == null) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"startup listener timed out on port=" + this.port);
		}
		if (bool == false) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"cannot start listener on port=" + this.port);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
		try {
			if (this.channel != null) {
				ChannelFuture future = this.channel.channel().close();
				NettyOperationListener operationListener = new NettyOperationListener();
				future.addListener(operationListener);
				try {
					operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
				} catch (CommunicationException ex) {
					LOGGER.error("disconnect", ex); // stopListening must continue
				}
			}
			if (this.serverThread != null) {
				this.serverThread.interrupt();
			}
		} catch (Exception ex) {
			LOGGER.error("stop listening", ex); // stopListening must continue
			return;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.stopListening();
		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();

		// Wait until all threads are terminated.
		try {
			this.bossGroup.terminationFuture().sync();
			this.workerGroup.terminationFuture().sync();
		} catch (InterruptedException e) {
			LOGGER.error("Endpoint destroy error", e); // stopListening must continue
		}
	}

	/** {@inheritDoc} */
	@Override
	public IResponder getResponder() {
		return resp;
	}

	/** {@inheritDoc} */
	@Override
	public void setResponder(IResponder resp) {
		this.resp = resp;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	@Override
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("end-point");
		writer.writeAttribute("host", this.host);
		writer.writeAttribute("port", this.port);
		writer.writeElement("thread-id", this.serverThread.getId());
		writer.writeElement("thread", this.serverThread.toString());
		writer.writeElement("channel", this.channel.toString());
		writer.writeEndElement(); // end of endpoint
	}
}
