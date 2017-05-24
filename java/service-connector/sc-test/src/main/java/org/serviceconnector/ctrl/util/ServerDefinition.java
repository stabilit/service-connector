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
package org.serviceconnector.ctrl.util;

import org.serviceconnector.TestConstants;
import org.serviceconnector.net.ConnectionType;

public class ServerDefinition {

	private String serverType;
	private String logbackFileName;
	private String serverName;
	private int serverPort;
	private int scPort;
	private int maxSessions;
	private int maxConnections;
	private String serviceNames;
	private ConnectionType connectionType;
	private String timezone;
	private String nics;

	public ServerDefinition(String serverType, String logbackFilename, String serverName, int serverPort, int scPort, int maxSessions, int maxConnections, String serviceNames,
			ConnectionType connectionType) {
		this(serverType, logbackFilename, serverName, serverPort, scPort, maxSessions, maxConnections, serviceNames, connectionType, null, TestConstants.HOST);
	}

	public ServerDefinition(String serverType, String logbackFileName, String serverName, int serverPort, int scPort, int maxSessions, int maxConnections, String serviceNames,
			ConnectionType connectionType, String timezone, String nics) {
		this.serverType = serverType;
		this.logbackFileName = logbackFileName;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.scPort = scPort;
		this.maxSessions = maxSessions;
		this.maxConnections = maxConnections;
		this.serviceNames = serviceNames;
		this.connectionType = connectionType;
		this.timezone = timezone;
		this.nics = nics;
	}

	public ServerDefinition(String serverType, String logbackFilename, String serverName, int serverPort, int scPort, int maxSessions, int maxConnections, String serviceNames) {
		this(serverType, logbackFilename, serverName, serverPort, scPort, maxSessions, maxConnections, serviceNames, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);
	}

	public String getServerType() {
		return serverType;
	}

	public String getLogbackFileName() {
		return logbackFileName;
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

	public String getNics() {
		return nics;
	}
}
