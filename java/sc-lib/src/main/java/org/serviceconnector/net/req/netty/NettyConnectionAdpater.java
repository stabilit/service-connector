package org.serviceconnector.net.req.netty;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.Timer;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.CommunicationException;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;

public abstract class NettyConnectionAdpater implements IConnection {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyConnectionAdpater.class);
	/** The Constant connectionLogger. */
	protected final static ConnectionLogger connectionLogger = ConnectionLogger.getInstance();
	/** The base conf. */
	protected final BasicConfiguration baseConf = AppContext.getBasicConfiguration();

	/** The connection context. */
	protected ConnectionContext connectionContext;
	/** The number of idles, counts idle states. */
	private int nrOfIdles;
	/** The port. */
	protected int port;
	/** The host. */
	protected String host;
	/** The local socket address. */
	protected InetSocketAddress localSocketAddress;
	/** The encoder decoder. */
	protected IEncoderDecoder encoderDecoder;
	/** The operation listener. */
	protected NettyOperationListener operationListener;
	/** The channel. */
	protected Channel channel;
	/** The bootstrap. */
	protected ClientBootstrap bootstrap;
	/** The channel pipeline factory. */
	protected ChannelPipelineFactory pipelineFactory;
	/** The idle timeout. */
	protected int idleTimeout;
	/** The timer to observe timeouts, static because should be shared. */
	protected static Timer timer;
	/*
	 * The channel factory. Configures client with Thread Pool, Boss Threads and Worker Threads. A boss thread accepts incoming
	 * connections on a socket. A worker thread performs non-blocking read and write on a channel.
	 */
	protected static NioClientSocketChannelFactory channelFactory;

	public NettyConnectionAdpater(NioClientSocketChannelFactory channelFactory, Timer timer) {
		this.port = 0;
		this.host = null;
		this.operationListener = null;
		this.connectionContext = null;
		this.encoderDecoder = null;
		this.localSocketAddress = null;
		this.channel = null;
		this.bootstrap = null;
		this.pipelineFactory = null;
		this.idleTimeout = 0; // default 0 -> inactive
		NettyConnectionAdpater.channelFactory = channelFactory;
		NettyConnectionAdpater.timer = timer;
	}

	/** {@inheritDoc} */
	@Override
	public abstract void connect() throws Exception;

	/** {@inheritDoc} */
	@Override
	public abstract void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception;

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		ChannelFuture future = this.channel.close();
		future.addListener(this.operationListener);
		try {
			this.operationListener.awaitUninterruptibly(Constants.TECH_LEVEL_OPERATION_TIMEOUT_MILLIS);
		} catch (Exception ex) {
			logger.error("destroy", ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ChannelFuture future = this.channel.disconnect();
		future.addListener(this.operationListener);
		try {
			this.operationListener.awaitUninterruptibly(baseConf.getConnectionTimeoutMillis());
		} catch (CommunicationException ex) {
			logger.error("disconnect", ex);
			throw new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION, "disconnect failed from "
					+ this.localSocketAddress.toString());
		}
		if (connectionLogger.isEnabled()) {
			connectionLogger.logDisconnect(this.getClass().getSimpleName(), this.localSocketAddress.getHostName(),
					this.localSocketAddress.getPort());
		}
	}

	/** {@inheritDoc} */
	@Override
	public ConnectionContext getContext() {
		return this.connectionContext;
	}

	/** {@inheritDoc} */
	@Override
	public void setContext(ConnectionContext connectionContext) {
		this.connectionContext = connectionContext;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConnected() {
		if (this.channel == null) {
			return false;
		}
		return this.channel.isConnected();
	}

	/** {@inheritDoc} */
	@Override
	public int getNrOfIdlesInSequence() {
		return nrOfIdles;
	}

	/** {@inheritDoc} */
	@Override
	public void incrementNrOfIdles() {
		this.nrOfIdles++;
	}

	/** {@inheritDoc} */
	@Override
	public void resetNrOfIdles() {
		this.nrOfIdles = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/** {@inheritDoc} */
	@Override
	public void setIdleTimeoutSeconds(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}
}
