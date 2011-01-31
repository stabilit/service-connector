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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCServer. Server to a SC.
 */
public class SCServer {
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(SCServer.class);
	/** The SC host. */
	private String scHost;
	/** The SC port. */
	private int scPort;
	/** The network interfaces which the server is listening. */
	private List<String> nics = null;
	/** The listener port. */
	private int listenerPort;
	/** The connection type which is used to communicate to SC. */
	private ConnectionType connectionType;
	/** The server listening state. */
	private volatile boolean listening;
	/** The immediate connect. Indicates if server immediately gets connections from SC after register is done. */
	private boolean immediateConnect;
	/** The keep alive interval seconds. Default = 60. */
	private int keepAliveIntervalSeconds;
	/** The responder. */
	private IResponder responder;
	/** The requester. */
	private SCRequester requester;

	/**
	 * Instantiates a new SC server.
	 * 
	 * @param scHost
	 *            the SC host
	 * @param scPort
	 *            the SC port
	 * @param listenerPort
	 *            the listener port
	 */
	public SCServer(String scHost, int scPort, int listenerPort) {
		this(scHost, scPort, null, listenerPort, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);
	}

	/**
	 * Instantiates a new SC server.
	 * 
	 * @param scHost
	 *            the SC host
	 * @param scPort
	 *            the SC port
	 * @param listenerPort
	 *            the listener port
	 * @param connectionType
	 *            the connection type
	 */
	public SCServer(String scHost, int scPort, int listenerPort, ConnectionType connectionType) {
		this(scHost, scPort, null, listenerPort, connectionType);
	}

	/**
	 * Instantiates a new SC server.
	 * 
	 * @param scHost
	 *            the SC host
	 * @param scPort
	 *            the SC port
	 * @param networkInterfaces
	 *            the network interfaces
	 * @param listenerPort
	 *            the listener port
	 * @param connectionType
	 *            the connection type
	 */
	public SCServer(String scHost, int scPort, List<String> networkInterfaces, int listenerPort, ConnectionType connectionType) {
		this.nics = networkInterfaces;
		this.responder = null;
		this.requester = null;
		this.scHost = scHost;
		this.scPort = scPort;
		this.listenerPort = listenerPort;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
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
	 * Gets the SC host.
	 * 
	 * @return the SC host
	 */
	public String getSCHost() {
		return scHost;
	}

	/**
	 * Gets the SC port.
	 * 
	 * @return the SC port
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
	 * Gets the listener interfaces.
	 * 
	 * @return the listener interfaces
	 */
	public List<String> getListenerInterfaces() {
		return this.nics;
	}

	/**
	 * Sets the immediate connect. Affects connecting behavior from SC. If immediateConnect is set SC establishes connection to
	 * server at the time registerServer is received.
	 * 
	 * @param immediateConnect
	 *            immediate connect
	 */
	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	/**
	 * Checks if is immediate connect flag is set.
	 * 
	 * @return true, if is immediate connect
	 */
	public boolean isImmediateConnect() {
		return immediateConnect;
	}

	/**
	 * Checks if server is listening.
	 * 
	 * @return true, if is listening
	 */
	public boolean isListening() {
		return listening;
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
	 * Sets the keep alive interval in seconds. Interval in seconds between two subsequent keepAlive requests (KRQ). The keepAlive
	 * message is solely used to refresh the firewall timeout on the network path. KeepAlive message is only sent on an idle
	 * connection. The value = 0 means no keep alive messages will be sent.
	 * 
	 * @param keepAliveIntervalSeconds
	 *            Example: 360
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	/**
	 * Start listener.
	 * 
	 * @throws SCMPValidatorException
	 *             scPort Number > 1 and < 65535<br>
	 *             listenerPort Number > 1 and < 65535<br>
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void startListener() throws Exception {
		if (this.listening == true) {
			throw new InvalidActivityException("listener is already started not allowed to start again.");
		}
		if (this.scHost == null) {
			throw new InvalidParameterException("host must be set.");
		}
		if (this.connectionType == null) {
			throw new InvalidParameterException("connectionType must be set.");
		}
		ValidatorUtility.validateInt(1, this.scPort, Constants.MAX_PORT_NR, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(1, this.listenerPort, Constants.MAX_PORT_NR, SCMPError.HV_WRONG_PORTNR);

		if (this.scPort == this.listenerPort) {
			throw new InvalidParameterException("SC port and listener port must not be the same.");
		}

		if (this.nics == null || this.nics.size() == 0) {
			nics = new ArrayList<String>();
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAdresses)) {
						if (inetAddress.getHostAddress().equals(Constants.IPV6_LOOPBACK_NIC)) {
							// ignore IPV6_LOOPBACK_NIC, bind not possible on this NIC
							continue;
						}
						nics.add(inetAddress.getHostAddress());
						logger.debug("SCServer listens on " + inetAddress.getHostAddress());
					}
				}
			} catch (Exception e) {
				logger.fatal("unable to detect network interface", e);
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "wrong interface");
			}
		}

		ListenerConfiguration listenerConfig = new ListenerConfiguration(SCSessionServer.class.getSimpleName());
		listenerConfig.setConnectionType(this.connectionType.getValue());
		listenerConfig.setNetworkInterfaces(nics);
		listenerConfig.setPort(this.listenerPort);

		responder = new Responder(listenerConfig);
		try {
			responder.create();
			responder.startListenAsync();
		} catch (Exception ex) {
			this.listening = false;
			logger.error("unable to start listener :" + listenerConfig.getName(), ex);
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

	/**
	 * Destroy server. Destroys server and releases all resources. No more communication to SC is possible after calling destroy.
	 * Deregister servers and stop listener before calling destroy.
	 */
	public synchronized void destroy() {
		this.requester.destroy();
		AppContext.destroy();
	}

	/**
	 * New session server.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC session server
	 * @throws Exception
	 *             the exception
	 */
	public SCSessionServer newSessionServer(String serviceName) throws Exception {
		if (this.listening == false) {
			throw new SCServiceException("newSessionServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		return new SCSessionServer(this, serviceName, requester);
	}

	/**
	 * New publish server.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC publish server
	 * @throws Exception
	 *             the exception
	 */
	public SCPublishServer newPublishServer(String serviceName) throws Exception {
		if (this.listening == false) {
			throw new SCServiceException("newPublishServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		return new SCPublishServer(this, serviceName, requester);
	}
}
