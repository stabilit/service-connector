/*
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
 */
package org.serviceconnector.api.srv;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

public class SCServer {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCServer.class);
	/** The sc host. */
	private String scHost;
	/** The sc port. */
	private int scPort;
	/** The listener port. */
	private int listenerPort;
	/** The connection type. */
	private ConnectionType connectionType;
	/** The server listening state. */
	private volatile boolean listening;
	/** The immediate connect. */
	private boolean immediateConnect;
	/** The keep alive interval seconds. */
	private int keepAliveIntervalSeconds;
	/** The responder. */
	private IResponder responder;
	private SCRequester requester;

	public SCServer(String scHost, int scPort, int listenerPort) {
		this(scHost, scPort, listenerPort, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);
	}

	public SCServer(String scHost, int scPort, int listenerPort, ConnectionType connectionType) {
		this.responder = null;
		this.requester = null;
		this.scHost = scHost;
		this.scPort = scPort;
		this.listenerPort = listenerPort;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.listening = false;
	}

	/**
	 * Gets the connection type.
	 * 
	 * @return the connection type
	 */
	public ConnectionType getConnectionType() {
		return connectionType;
	}

	/**
	 * Gets the sC host.
	 * 
	 * @return the sC host
	 */
	public String getSCHost() {
		return scHost;
	}

	/**
	 * Gets the sC port.
	 * 
	 * @return the sC port
	 */
	public int getSCPort() {
		return scPort;
	}

	/**
	 * Gets the listener port.
	 * 
	 * @return the listener port
	 */
	public int getListenerPort() {
		return listenerPort;
	}

	/**
	 * Sets the immediate connect. Affects connecting behavior from SC. If immediateConnect is set SC establishes connection to
	 * server at the time registerServer is received.
	 * 
	 * @param immediateConnect
	 *            the new immediate connect
	 */
	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	/**
	 * Checks if is immediate connect.
	 * 
	 * @return true, if is immediate connect
	 */
	public boolean isImmediateConnect() {
		return immediateConnect;
	}

	/**
	 * Checks if is listening.
	 * 
	 * @return true, if is listening
	 */
	public boolean isListening() {
		return listening;
	}

	/**
	 * Sets the listening.
	 * 
	 * @param listening
	 *            the new listening
	 */
	public void setListening(boolean listening) {
		this.listening = listening;
	}

	/**
	 * Gets the keep alive interval seconds.
	 * 
	 * @return the keep alive interval seconds
	 */
	public int getKeepAliveIntervalSeconds() {
		return keepAliveIntervalSeconds;
	}

	/**
	 * Sets the keep alive interval seconds.
	 * 
	 * @param keepAliveIntervalSeconds
	 *            the new keep alive interval seconds
	 * @throws SCMPValidatorException
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, this.keepAliveIntervalSeconds, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	public synchronized void startListener() throws Exception {
		if (this.listening == true) {
			throw new InvalidActivityException("listener is already started not allowed to start again.");
		}
		if (this.scHost == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, this.scPort, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(0, this.listenerPort, 0xFFFF, SCMPError.HV_WRONG_PORTNR);

		CommunicatorConfig respConfig = new CommunicatorConfig(SCSessionServer.class.getSimpleName());
		respConfig.setConnectionType(this.connectionType.getValue());

		// TODO MIT JAN !!! Liste oder was???
		List<String> hosts = new ArrayList<String>();
		hosts.add("localhost");

		if (hosts.size() == 0) {
			throw new InvalidParameterException("at least one host must be set.");
		}

		respConfig.setInterfaces(hosts);
		respConfig.setPort(this.listenerPort);

		responder = new Responder(respConfig);
		try {
			responder.create();
			responder.startListenAsync();
		} catch (Exception ex) {
			this.listening = false;
			logger.error("unable to start listener :" + respConfig.getName(), ex);
			throw ex;
		}
		this.listening = true;
		// initialize requester, maxConnection = 1 only 1 connection allowed for register server
		this.requester = new SCRequester(new RequesterContext(this.scHost, this.scPort, this.connectionType.getValue(),
				this.keepAliveIntervalSeconds, 1));
	}

	/**
	 * StopListener. Stop listening and clean up.
	 */
	public void stopListener() {
		if (this.listening == false) {
			// server is not listening
			return;

		}
		this.listening = false;
		this.responder.stopListening();
		this.responder.destroy();
	}

	public SCSessionServer newSessionServer(String serviceName) throws Exception {
		if (this.listening == false) {
			throw new SCServiceException("newSessionServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCSessionServer(this, serviceName, requester);
	}

	public SCPublishServer newPublishServer(String serviceName) throws Exception {
		if (this.listening == false) {
			throw new SCServiceException("newPublishServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCPublishServer(this, serviceName, requester);
	}
}
