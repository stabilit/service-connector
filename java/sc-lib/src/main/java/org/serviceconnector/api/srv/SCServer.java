package org.serviceconnector.api.srv;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

public class SCServer {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCServer.class);

	private SCServerContext scServerContext;
	/** The responder. */
	private IResponder responder;

	public SCServer(String scHost, int scPort, int listenerPort) {
		this(scHost, scPort, listenerPort, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);
	}

	public SCServer(String scHost, int scPort, int listenerPort, ConnectionType connectionType) {
		this.scServerContext = new SCServerContext(scHost, scPort, listenerPort, connectionType);
		this.responder = null;
	}

	public SCServerContext getSCServerContext() {
		return scServerContext;
	}

	public ConnectionType getConnectionType() {
		return this.scServerContext.getConnectionType();
	}

	public String getSCHost() {
		return this.scServerContext.getSCHost();
	}

	public int getSCPort() {
		return this.scServerContext.getSCPort();
	}

	public int getListenerPort() {
		return this.scServerContext.getListenerPort();
	}

	public void setKeepAliveIntervalInSeconds(int keepAliveIntervalSeconds) {
		this.scServerContext.setKeepAliveIntervalSeconds(keepAliveIntervalSeconds);
	}

	public void setImmediateConnect(boolean immediateConnect) {
		this.scServerContext.setImmediateConnect(immediateConnect);
	}

	public boolean isListening() {
		return this.scServerContext.isListening();
	}

	public int getKeepAliveIntervalSeconds() {
		return this.scServerContext.getKeepAliveIntervalSeconds();
	}

	/**
	 * Start server.
	 * 
	 * @param host
	 *            the host to bind the listener
	 * @param port
	 *            the port to bin the listener
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset<br>
	 *             keepAliveIntervalInSeconds not within limits 0 to 3600
	 */
	public synchronized void startListener() throws Exception {
		if (this.scServerContext.isListening() == true) {
			throw new InvalidActivityException("listener is already started not allowed to start again.");
		}
		CommunicatorConfig respConfig = new CommunicatorConfig(SCSessionServer.class.getSimpleName());
		respConfig.setConnectionType(this.scServerContext.getConnectionType().getValue());

		int port = this.scServerContext.getListenerPort();
		String host = "localhost";

		if (host == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(0, this.scServerContext.getKeepAliveIntervalSeconds(), 3600,
				SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);

		respConfig.setHost(host);
		respConfig.setPort(port);

		responder = new Responder(respConfig);
		try {
			responder.create();
			responder.startListenAsync();
		} catch (Exception ex) {
			this.scServerContext.setKeepAliveIntervalSeconds(0);
			this.scServerContext.setListening(false);
			logger.error("unable to start listener :" + respConfig.getName(), ex);
			throw ex;
		}
		this.scServerContext.setListening(true);
	}

	/**
	 * StopListener. Stop listening and clean up.
	 */
	public void stopListener() {
		if (this.scServerContext.isListening() == false) {
			// server is not listening
			return;

		}
		this.scServerContext.setListening(false);
		this.responder.stopListening();
		this.responder.destroy();
	}

	public SCSessionServer newSessionServer(String serviceName) {
		return new SCSessionServer(this.scServerContext, serviceName);
	}

	public SCPublishServer newPublishServer(String serviceName) {
		return new SCPublishServer(this.scServerContext, serviceName);
	}
}
