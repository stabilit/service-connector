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
package com.stabilit.scm.common.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The Class RequeserConfig. Requester configuration may hold more than one configuration for a requester, is represented
 * by <code>RequesterConfigItem</code>.
 * 
 * @author JTraber
 */
public class RequeserConfig {

	/** The props. */
	private Properties props;
	/** The requester configuration item list. */
	private List<RequesterConfigItem> reqConfigItemList;
	/** The logger key. */
	private String loggerKey;

	/**
	 * Instantiates a new client configuration.
	 */
	public RequeserConfig() {
		this.reqConfigItemList = null;
		this.props = null;
		this.loggerKey = null;
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

		String respNames = props.getProperty(IConstants.CONNECTION_NAMES);

		String[] resps = respNames.split(IConstants.COMMA_OR_SEMICOLON);
		reqConfigItemList = new ArrayList<RequesterConfigItem>();

		for (String respName : resps) {
			RequesterConfigItem reqConfigItem = new RequesterConfigItem();

			reqConfigItemList.add(reqConfigItem);

			int port = Integer.parseInt((String) props.get(respName + IConstants.PORT_QUALIFIER));

			reqConfigItem.setPort(port);
			reqConfigItem.setHost((String) props.get(respName + IConstants.HOST_QUALIFIER));
			reqConfigItem.setConnection((String) props.get(respName + IConstants.CON_QUALIFIER));
			reqConfigItem.setNumberOfThreads(Integer.parseInt((String) props.get(respName
					+ IConstants.THREAD_QUALIFIER)));
		}
		
		this.loggerKey = props.getProperty("root.logger");
	}
	
	/**
	 * Gets the logger key.
	 * 
	 * @return the loggerKey
	 */
	public String getLoggerKey() {
		return loggerKey;
	}

	/**
	 * Gets the client configuration list.
	 * 
	 * @return the client configuration list
	 */
	public List<RequesterConfigItem> getClientConfigList() {
		return reqConfigItemList;
	}

	/**
	 * Gets the client configuration.
	 * 
	 * @return the client configuration
	 */
	public IRequesterConfigItem getClientConfig() {
		return reqConfigItemList.get(0);
	}

	/**
	 * The Class RequesterConfigItem.
	 */
	public class RequesterConfigItem implements IRequesterConfigItem {

		/** The port. */
		private int port;
		/** The host. */
		private String host;
		/** The connection identifies concrete client implementation. */
		private String connection;
		/** The number of threads to use in thread pool of this client. */
		private int numberOfThreads;

		/**
		 * Instantiates a new RequesterConfigItem.
		 */
		public RequesterConfigItem() {
		}

		/**
		 * Instantiates a new RequesterConfigItem.
		 * 
		 * @param host
		 *            the host
		 * @param port
		 *            the port
		 * @param numberOfThreads
		 *            the number of threads
		 * @param connection
		 *            the connection
		 */
		public RequesterConfigItem(String host, int port, String connection, int numberOfThreads) {
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
