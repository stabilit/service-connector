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
package com.stabilit.sc.cln.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stabilit.sc.config.IConstants;

/**
 * The Class ClientConfig. Client configuration may hold more than one configuration for a client, is represented
 * by <code>ClientConfigItem</code>.
 * 
 * @author JTraber
 */
public class ClientConfig {

	/** The props. */
	private Properties props;
	/** The client configuration item list. */
	private List<ClientConfigItem> clientConfigItemList;

	/**
	 * Instantiates a new client configuration.
	 */
	public ClientConfig() {
		clientConfigItemList = null;
		props = null;
	}

	/**
	 * Loads configuration from a file.
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

		String serverNames = props.getProperty(IConstants.CONNECTION_NAMES);

		String[] servers = serverNames.split(IConstants.COMMA_OR_SEMICOLON);
		clientConfigItemList = new ArrayList<ClientConfigItem>();

		for (String serverName : servers) {
			ClientConfigItem clientConfigItem = new ClientConfigItem();

			clientConfigItemList.add(clientConfigItem);

			int port = Integer.parseInt((String) props.get(serverName + IConstants.PORT_QUALIFIER));

			clientConfigItem.setPort(port);
			clientConfigItem.setHost((String) props.get(serverName + IConstants.HOST_QUALIFIER));
			clientConfigItem.setConnection((String) props.get(serverName + IConstants.CON_QUALIFIER));
			clientConfigItem.setNumberOfThreads(Integer.parseInt((String) props.get(serverName
					+ IConstants.THREAD_QUALIFIER)));
		}
	}

	/**
	 * Gets the client configuration list.
	 * 
	 * @return the client configuration list
	 */
	public List<ClientConfigItem> getClientConfigList() {
		return clientConfigItemList;
	}

	/**
	 * Gets the client configuration.
	 * 
	 * @return the client configuration
	 */
	public IClientConfigItem getClientConfig() {
		return clientConfigItemList.get(0);
	}

	/**
	 * The Class ClientConfigItem.
	 */
	public class ClientConfigItem implements IClientConfigItem {

		/** The port. */
		private int port;
		/** The host. */
		private String host;
		/** The connection identifies concrete client implementation. */
		private String connection;
		/** The number of threads to use in thread pool of this client. */
		private int numberOfThreads;

		/**
		 * Instantiates a new client configuration item.
		 */
		public ClientConfigItem() {
		}

		/**
		 * Instantiates a new client configuration item.
		 * 
		 * @param host
		 *            the host
		 * @param port
		 *            the port
		 * @param con
		 *            the configuration
		 * @param numberOfThreads
		 *            the number of threads
		 */
		public ClientConfigItem(String host, int port, String connection, int numberOfThreads) {
			super();
			this.port = port;
			this.host = host;
			this.connection = connection;
			this.numberOfThreads = numberOfThreads;
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
			return connection;
		}

		/** {@inheritDoc} */
		public void setConnection(String connection) {
			this.connection = connection;
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
