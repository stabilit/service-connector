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

	public SCServerContext(String scHost, int scPort, int listenerPort, ConnectionType connectionType) {
		this.scHost = scHost;
		this.scPort = scPort;
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
}