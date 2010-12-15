/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;

/**
 * @author JTraber
 */
public class ServerLoader {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServerLoader.class);

	/**
	 * Loads remote hosts from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void load(CompositeConfiguration config) throws Exception {
		@SuppressWarnings("unchecked")
		List<String> serverNames = config.getList(Constants.PROPERTY_REMOTE_NODES);

		for (String serverName : serverNames) {
			serverName = serverName.trim(); // remove blanks in serverName
			
			int portNr = Integer.parseInt((String) config.getString(serverName + Constants.PROPERTY_QUALIFIER_PORT));
			String host = (String) config.getString(serverName + Constants.PROPERTY_QUALIFIER_HOST);
			String connectionType = (String) config.getString(serverName + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);

			if (connectionType == null) {
				connectionType = ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE.getValue();
			}
			String maxConnectionsValue = (String) config
					.getString(serverName + Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE);
			int maxConnections = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
			if (maxConnectionsValue != null) {
				maxConnections = Integer.parseInt(maxConnectionsValue);
			}

			String keepAliveIntervalValue = (String) config.getString(serverName + Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS);
			int keepAliveInterval = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			String serverTypeString = (String) config.getString(serverName + Constants.PROPERTY_QUALIFIER_TYPE);
			ServerType serverType = ServerType.getServiceType(serverTypeString);

			InetSocketAddress socketAddress = new InetSocketAddress(host, portNr);
			// instantiate right type of server

			Server server = null;
			switch (serverType) {
			case FILE_SERVER:
				String maxSessionValue = (String) config.getString(serverName + Constants.PROPERTY_QALIFIER_MAX_SESSIONS);
				int maxSessions = Constants.DEFAULT_MAX_FILE_SESSIONS;
				if (maxSessionValue != null) {
					maxSessions = Integer.parseInt(maxSessionValue);
				}
				server = new FileServer(serverName, socketAddress, portNr, maxSessions, maxConnections, connectionType,
						keepAliveInterval);
				break;
			case CASCADED_SC:
				// TODO JOT .. cascaded handling
				// server = new CascadedSC(serviceName);
				continue;
			case WEB_SERVER:
				// nothing to do in case of a web server is registered in specific endpoint
				continue;
			default:
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "wrong serverType, serverName/serverType: "
						+ serverName + "/" + serverTypeString);
			}
			AppContext.getServerRegistry().addServer(server.getServerKey(), server);
		}
	}
}
