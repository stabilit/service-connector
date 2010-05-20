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
package com.stabilit.sc.srv.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.srv.config.IServerConfigItem;

/**
 * The Class ServerConfig. Server configuration may hold more than one configuration for a server, is represented
 * by <code>ServerConfigItem</code>.
 * 
 * @author JTraber
 */
public class ServerConfig {

	/** The props. */
	private Properties props;
	/** The server configuration list. */
	private List<ServerConfigItem> serverConfigList;
	
	private String loggerKey;

	/**
	 * Instantiates a new server configuration.
	 */
	public ServerConfig() {
		serverConfigList = null;
		props = null;
		loggerKey = null;
	}

	/**
	 * Load.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void load(String fileName) throws IOException {
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		props = new Properties();
		props.load(is);

		String serverNames = props.getProperty("serverNames");

		String[] servers = serverNames.split(",|;");
		serverConfigList = new ArrayList<ServerConfigItem>();

		for (String serverName : servers) {
			ServerConfigItem serverConfig = new ServerConfigItem(serverName);

			serverConfigList.add(serverConfig);

			int port = Integer.parseInt((String) props.get(serverName + IConstants.PORT_QUALIFIER));

			serverConfig.setPort(port);
			serverConfig.setHost((String) props.get(serverName + IConstants.HOST_QUALIFIER));
			serverConfig.setConnection((String) props.get(serverName + IConstants.CON_QUALIFIER));
			serverConfig.setNumberOfThreads(Integer.parseInt((String) props.get(serverName
					+ IConstants.THREAD_QUALIFIER)));
		}
		
		this.loggerKey = props.getProperty("root.logger");
	}
	
	/**
	 * @return the loggerKey
	 */
	public String getLoggerKey() {
		return loggerKey;
	}

	/**
	 * Gets the server configuration list.
	 * 
	 * @return the server configuration list
	 */
	public List<ServerConfigItem> getServerConfigList() {
		return serverConfigList;
	}

	/**
	 * The Class ServerConfigItem.
	 */
	public class ServerConfigItem implements IServerConfigItem {

		/** The server name. */
		private String serverName;
		/** The port. */
		private int port;
		/** The host. */
		private String host;
		/** The con. */
		private String con;
		/** The number of threads. */
		private int numberOfThreads;

		/**
		 * The Constructor.
		 * 
		 * @param serverName
		 *            the server name
		 */
		public ServerConfigItem(String serverName) {
			this.serverName = serverName;
		}

		/** {@inheritDoc} */
		public String getServerName() {
			return serverName;
		}

		/** {@inheritDoc} */
		public void setServerName(String serverName) {
			this.serverName = serverName;
		}

		/** {@inheritDoc} */
		public int getPort() {
			return port;
		}

		/** {@inheritDoc} */
		public void setPort(int port) {
			this.port = port;
		}

		/** {@inheritDoc} */
		public String getHost() {
			return host;
		}

		/** {@inheritDoc} */
		public void setHost(String host) {
			this.host = host;
		}

		/** {@inheritDoc} */
		public String getConnection() {
			return con;
		}

		/** {@inheritDoc} */
		public void setConnection(String con) {
			this.con = con;
		}

		/** {@inheritDoc} */
		public int getNumberOfThreads() {
			return numberOfThreads;
		}

		/** {@inheritDoc} */
		public void setNumberOfThreads(int numberOfThreads) {
			this.numberOfThreads = numberOfThreads;
		}
	}
}
