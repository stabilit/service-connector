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
 * The Class ResponderConfig. Responder configuration may hold more than one configuration for a responder, is represented by
 * <code>ResponderConfigItem</code>.
 * 
 * @author JTraber
 */
public class ResponderConfig {

	/** The props. */
	private Properties props;
	/** The responder configuration list. */
	private List<ResponderConfigItem> respConfigList;
	/** The logger key. */
	private String loggerKey;

	/**
	 * Instantiates a new ResponderConfig.
	 */
	public ResponderConfig() {
		this.respConfigList = null;
		this.props = null;
		this.loggerKey = null;
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

		String respNames = props.getProperty("serverNames");

		String[] resps = respNames.split(",|;");
		respConfigList = new ArrayList<ResponderConfigItem>();

		for (String respName : resps) {
			ResponderConfigItem respConfig = new ResponderConfigItem(respName);

			respConfigList.add(respConfig);

			int port = Integer.parseInt((String) props.get(respName + IConstants.PORT_QUALIFIER));

			respConfig.setPort(port);
			respConfig.setHost((String) props.get(respName + IConstants.HOST_QUALIFIER));
			respConfig.setConnection((String) props.get(respName + IConstants.CON_QUALIFIER));
			respConfig.setNumberOfThreads(Integer.parseInt((String) props.get(respName
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
	 * Gets the responder configuration list.
	 * 
	 * @return the responder configuration list
	 */
	public List<ResponderConfigItem> getResponderConfigList() {
		return respConfigList;
	}

	/**
	 * The Class ResponderConfigItem.
	 */
	public class ResponderConfigItem implements IResponderConfigItem {

		/** The responder name. */
		private String respName;
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
		 * @param respName
		 *            the responder name
		 */
		public ResponderConfigItem(String respName) {
			this.respName = respName;
		}

		/** {@inheritDoc} */
		public String getResponderName() {
			return respName;
		}

		/** {@inheritDoc} */
		public void setResponderName(String respName) {
			this.respName = respName;
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
