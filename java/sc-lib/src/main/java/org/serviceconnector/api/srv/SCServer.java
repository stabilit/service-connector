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
package org.serviceconnector.api.srv;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.Responder;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCServer. Server to a SC.
 */
public class SCServer {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCServer.class);
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
	/** The keep alive timeout in seconds. Time to wait for the reply of a keep alive sent to the SC. Default = 10. */
	private int keepAliveTimeoutSeconds;
	/** The check registration interval seconds. Default = 0. */
	private int checkRegistrationIntervalSeconds;
	/** The check registration timeout in seconds. Time to wait for the reply of a check registration sent to the SC. Default = 10. */
	private int checkRegistraionTimeoutSeconds;
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
		this.keepAliveTimeoutSeconds = Constants.DEFAULT_KEEP_ALIVE_OTI_SECONDS;
		this.checkRegistrationIntervalSeconds = Constants.DEFAULT_CHECK_REGISTRATION_INTERVAL_SECONDS;
		this.checkRegistraionTimeoutSeconds = Constants.DEFAULT_CHECK_REGISTRATION_OTI_SECONDS;
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
	 * @throws SCServiceException
	 *             listener is already started<br />
	 */
	public void setImmediateConnect(boolean immediateConnect) throws SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
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
	 * @throws SCServiceException
	 *             listener is already started<br />
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) throws SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	/**
	 * Sets the keep alive timeout in seconds. Time in seconds a keep alive request waits to be confirmed. If no confirmation is
	 * received single connection gets closed.
	 * 
	 * @param keepAliveTimeoutSeconds
	 *            time to wait for completion of a keep alive request
	 *            Example: 10
	 * @throws SCMPValidatorException
	 *             keepAliveTimeoutSeconds > 1 and < 3600<br />
	 * @throws SCServiceException
	 *             listener is already started<br />
	 */
	public void setKeepAliveTimeoutSeconds(int keepAliveTimeoutSeconds) throws SCMPValidatorException, SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
		// validate in this case its a local needed information
		ValidatorUtility.validateInt(1, keepAliveTimeoutSeconds, Constants.MAX_KP_TIMEOUT_VALUE,
				SCMPError.HV_WRONG_KEEPALIVE_TIMEOUT);
		this.keepAliveTimeoutSeconds = keepAliveTimeoutSeconds;
	}

	/**
	 * Gets the keep alive timeout seconds.
	 * 
	 * @return the keep alive timeout seconds
	 */
	public int getKeepAliveTimeoutSeconds() {
		return this.keepAliveTimeoutSeconds;
	}
	

	/**
	 * Sets the TCP keep alive. True to enable sending of TCP keep alive. False to disable sending. If the method is not called the
	 * default value from underlying OS is taken.
	 * 
	 * @param tcpKeepAlive
	 *            the new TCP keep alive
	 * @throws SCServiceException
	 *             called method after attach
	 */
	public void setTCPKeepAlive(boolean tcpKeepAlive) throws SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
		// sets the TCP keep alive in basic configuration
		AppContext.getBasicConfiguration().setTCPKeepAlive(tcpKeepAlive);
	}

	/**
	 * Gets the TCP keep alive.
	 * 
	 * @return the TCP keep alive
	 *         TRUE - TCP keep alive is enabled
	 *         FALSE - TCP keep alive is disabled
	 *         NULL - not specified, underlying OS setting takes place
	 */
	public Boolean getTCPKeepAlive() {
		// returns TCP keep alive from basic configuration
		return AppContext.getBasicConfiguration().getTcpKeepAlive();
	}

	/**
	 * Sets the check registration timeout in seconds. Time in seconds a check registration request waits to be confirmed. If no
	 * confirmation is received server for specific service is dead.
	 * 
	 * @param checkRegistrationTimeoutSeconds
	 *            time to wait for completion of a check registration request
	 *            Example: 10
	 * @throws SCMPValidatorException
	 *             checkRegistrationTimeoutSeconds > 1 and < 3600<br />
	 * @throws SCServiceException
	 *             listener is already started<br />
	 */
	public void setCheckRegistrationTimeoutSeconds(int checkRegistrationTimeoutSeconds) throws SCMPValidatorException,
			SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
		// validate in this case its a local needed information
		ValidatorUtility.validateInt(1, checkRegistrationTimeoutSeconds, Constants.MAX_CRG_TIMEOUT_VALUE,
				SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.checkRegistraionTimeoutSeconds = checkRegistrationTimeoutSeconds;
	}

	/**
	 * Gets the check registration timeout seconds.
	 * 
	 * @return the check registration timeout seconds
	 */
	public int getCheckRegistrationTimeoutSeconds() {
		return this.checkRegistraionTimeoutSeconds;
	}

	/**
	 * Sets the check registration interval in seconds. Interval in seconds between two subsequent check registration requests
	 * (CRG). The check registration message is used to refresh the server timeout on the SC. If the SC no check registration
	 * message receives within the interval the server gets destroyed. The value = 0 means no observation will be active on SC.
	 * Check registration message can be sent any time to verify SC is alive and Server is registered properly.
	 * 
	 * @param checkRegistrationIntervalSeconds
	 *            Example: 360
	 * @throws SCServiceException
	 *             listener is already started<br />
	 */
	public void setCheckRegistrationIntervalSeconds(int checkRegistrationIntervalSeconds) throws SCServiceException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to set property.");
		}
		this.checkRegistrationIntervalSeconds = checkRegistrationIntervalSeconds;
	}

	/**
	 * Gets the check registration interval seconds.
	 * 
	 * @return the check registration interval seconds
	 */
	public int getCheckRegistrationIntervalSeconds() {
		return checkRegistrationIntervalSeconds;
	}

	/**
	 * Start listener.
	 * 
	 * @throws SCServiceException
	 *             listener is already started<br />
	 *             SC host not set<br />
	 *             ConnectionType not set<br />
	 *             starting listener fails<br />
	 * @throws SCMPValidatorException
	 *             scPort Number > 1 and < 65535<br />
	 *             listenerPort Number > 1 and < 65535<br />
	 *             SC port and listener port are the same<br />
	 *             bind to interface failed<be>
	 */
	public synchronized void startListener() throws SCServiceException, SCMPValidatorException {
		if (this.listening == true) {
			throw new SCServiceException("Listener is already started not allowed to start again.");
		}
		if (this.scHost == null) {
			throw new SCMPValidatorException("Host must be set.");
		}
		if (this.connectionType == null) {
			throw new SCMPValidatorException("ConnectionType must be set.");
		}
		ValidatorUtility.validateInt(Constants.MIN_PORT_VALUE, this.scPort, Constants.MAX_PORT_VALUE, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(Constants.MIN_PORT_VALUE, this.listenerPort, Constants.MAX_PORT_VALUE,
				SCMPError.HV_WRONG_PORTNR);

		if (this.scPort == this.listenerPort) {
			throw new SCMPValidatorException("SC port and listener port must not be the same.");
		}

		if (this.nics == null || this.nics.size() == 0) {
			nics = new ArrayList<String>();
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAdresses)) {
						if (inetAddress instanceof Inet6Address) {
							// ignore IPV6 addresses, bind not possible on this NIC
							continue;
						}
						nics.add(inetAddress.getHostAddress());
						LOGGER.debug("SCServer listens on " + inetAddress.getHostAddress());
					}
				}
			} catch (Exception e) {
				LOGGER.fatal("unable to detect network interface", e);
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "Wrong interface.");
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
			LOGGER.error("unable to start listener=" + listenerConfig.getName(), ex);
			throw new SCServiceException("Unable to start listener.", ex);
		}
		this.listening = true;
		RemoteNodeConfiguration remoteNodeConf = new RemoteNodeConfiguration(this.listenerPort + "server", this.scHost,
				this.scPort, this.connectionType.getValue(), this.keepAliveIntervalSeconds, this.checkRegistrationIntervalSeconds,
				1);
		// initialize requester, maxConnection = 1 only 1 connection allowed for register server
		this.requester = new SCRequester(remoteNodeConf, this.keepAliveTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
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
		if (this.requester != null) {
			this.requester.destroy();
		}
		AppContext.destroy();
	}

	/**
	 * New session server.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC session server
	 * @throws SCServiceException
	 *             server not listening<br />
	 * @throws SCMPValidatorException
	 *             service name not set<br />
	 */
	public SCSessionServer newSessionServer(String serviceName) throws SCServiceException, SCMPValidatorException {
		if (this.listening == false) {
			throw new SCServiceException("NewSessionServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new SCMPValidatorException("service name must be set");
		}
		return new SCSessionServer(this, serviceName, requester);
	}

	/**
	 * New publish server.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC publish server
	 * @throws SCServiceException
	 *             server not listening<br />
	 * @throws SCMPValidatorException
	 *             service name not set<br />
	 */
	public SCPublishServer newPublishServer(String serviceName) throws SCServiceException, SCMPValidatorException {
		if (this.listening == false) {
			throw new SCServiceException("newPublishServer not possible - server not listening.");
		}
		if (serviceName == null) {
			throw new SCMPValidatorException("service name must be set");
		}
		return new SCPublishServer(this, serviceName, requester);
	}
}
