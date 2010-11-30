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

public class SCServerContext {

	private String scHost;
	private int scPort;
	private int listenerPort;

	private ConnectionType connectionType;
	/** The server listening state. */
	private volatile boolean listening;
	/** The immediate connect. */
	private boolean immediateConnect;
	private int keepAliveIntervalSeconds;

	private SCServer scServer;

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

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public String getSCHost() {
		return scHost;
	}

	public int getSCPort() {
		return scPort;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	public boolean isImmediateConnect() {
		return immediateConnect;
	}

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public int getKeepAliveIntervalSeconds() {
		return keepAliveIntervalSeconds;
	}

	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	public SCServer getSCServer() {
		return scServer;
	}

	public void setSCServer(SCServer scServer) {
		this.scServer = scServer;
	}
}