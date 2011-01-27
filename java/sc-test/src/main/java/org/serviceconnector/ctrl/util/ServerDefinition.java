package org.serviceconnector.ctrl.util;

import org.serviceconnector.net.ConnectionType;

public class ServerDefinition {

	private String serverType;
	private String log4jproperty;
	private String serverName;
	private int serverPort;
	private int scPort;
	private int maxSessions;
	private int maxConnections;
	private String serviceNames;
	private ConnectionType connectionType;
	private String timezone;

	public ServerDefinition(String serverType, String log4jproperty, String serverName, int serverPort, int scPort,
			int maxSessions, int maxConnections, String serviceNames, ConnectionType connectionType) {
		this(serverType, log4jproperty, serverName, serverPort, scPort, maxSessions, maxConnections, serviceNames, connectionType,
				null);
	}

	public ServerDefinition(String serverType, String log4jproperty, String serverName, int serverPort, int scPort,
			int maxSessions, int maxConnections, String serviceNames, ConnectionType connectionType, String timezone) {
		this.serverType = serverType;
		this.log4jproperty = log4jproperty;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.scPort = scPort;
		this.maxSessions = maxSessions;
		this.maxConnections = maxConnections;
		this.serviceNames = serviceNames;
		this.connectionType = connectionType;
		this.timezone = timezone;
	}

	public ServerDefinition(String serverType, String log4jproperty, String serverName, int serverPort, int scPort,
			int maxSessions, int maxConnections, String serviceNames) {
		this(serverType, log4jproperty, serverName, serverPort, scPort, maxSessions, maxConnections, serviceNames,
				ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);
	}

	public String getServerType() {
		return serverType;
	}

	public String getLog4jproperty() {
		return log4jproperty;
	}

	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getScPort() {
		return scPort;
	}

	public int getMaxSessions() {
		return maxSessions;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public String getServiceNames() {
		return serviceNames;
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public String getTimezone() {
		return timezone;
	}
}
