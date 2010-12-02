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

import org.serviceconnector.Constants;
import org.serviceconnector.net.ConnectionType;

/**
 * The Class SCServerContext.
 */
public class SCServerContext {

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

	/** The sc server. */
	private SCServer scServer;

	/**
	 * Instantiates a new sC server context.
	 * 
	 * @param scServer
	 *            the sc server
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param listenerPort
	 *            the listener port
	 * @param connectionType
	 *            the connection type
	 */
	public SCServerContext(SCServer scServer, String scHost, int scPort, int listenerPort, ConnectionType connectionType) {
		this.scHost = scHost;
		this.scPort = scPort;
		this.scServer = scServer;
		this.listenerPort = listenerPort;
		this.listening = false;
		this.immediateConnect = true;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
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
	 * Sets the immediate connect.
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
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	/**
	 * Gets the sC server.
	 * 
	 * @return the sC server
	 */
	public SCServer getSCServer() {
		return scServer;
	}

	/**
	 * Sets the sC server.
	 * 
	 * @param scServer
	 *            the new sC server
	 */
	public void setSCServer(SCServer scServer) {
		this.scServer = scServer;
	}
}