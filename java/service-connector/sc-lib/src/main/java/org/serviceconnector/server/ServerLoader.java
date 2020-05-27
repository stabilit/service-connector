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
package org.serviceconnector.server;

import java.net.InetSocketAddress;
import java.util.Map;

import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.conf.RemoteNodeListConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JTraber
 */
public final class ServerLoader {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLoader.class);

	/**
	 * Instantiates a new server loader.
	 */
	private ServerLoader() {
	}

	/**
	 * Loads remote hosts from a file.
	 *
	 * @param remoteNodeListConfigurations the remote node list configurations
	 * @throws Exception the exception
	 */
	public static void load(RemoteNodeListConfiguration remoteNodeListConfigurations) throws Exception {
		Map<String, RemoteNodeConfiguration> remoteNodesMap = remoteNodeListConfigurations.getRequesterConfigurations();

		for (RemoteNodeConfiguration remoteNodeConfiguration : remoteNodesMap.values()) {

			ServerType serverType = remoteNodeConfiguration.getServerType();
			InetSocketAddress socketAddress = new InetSocketAddress(remoteNodeConfiguration.getHost(), remoteNodeConfiguration.getPort());

			Server server = null;
			switch (serverType) {
				case FILE_SERVER:
					server = new FileServer(remoteNodeConfiguration, socketAddress);
					break;
				case CASCADED_SC:
					server = new CascadedSC(remoteNodeConfiguration, socketAddress);
					break;
				case WEB_SERVER:
					// nothing to do in case of a web server is registered in specific endpoint
					continue;
				default:
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
							"wrong serverType, serverName/serverType=" + remoteNodeConfiguration.getName() + Constants.SLASH + serverType);
			}
			AppContext.getServerRegistry().addServer(server.getServerKey(), server);
		}
	}
}
