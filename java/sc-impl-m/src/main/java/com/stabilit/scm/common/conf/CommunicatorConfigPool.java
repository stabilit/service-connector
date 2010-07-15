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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stabilit.scm.common.cmd.SCMPValidatorException;

/**
 * The Class CommunicatorConfigPool.
 * 
 * @author JTraber
 */
public abstract class CommunicatorConfigPool {

	/** The properties. */
	private Properties props;
	/** The requester configuration list. */
	private List<ICommunicatorConfig> comConfigList;
	/** The logger key. */
	private String loggerKey;

	/**
	 * Instantiates a new communicator configuration pool.
	 */
	public CommunicatorConfigPool() {
		this.comConfigList = null;
		this.props = null;
		this.loggerKey = null;
	}

	public static InputStream getLiveOddsConfigStream() {
		// try to load config file from actual path
		InputStream is = null;
		try {
			is = new FileInputStream("liveodds-config.xml");
			return is;
		} catch (FileNotFoundException e) {
		}
		// try to load config file from classpath
		is = Thread.currentThread().getContextClassLoader().getResourceAsStream("liveodds-config.xml");
		return is;
	}

	/**
	 * Loads configuration from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void load(String fileName, String topLevelPropsKey) throws Exception {
		InputStream is = null;
		try {
			// try to find file outside of jar archive
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// try to find file inside jar archive
			is = ClassLoader.getSystemResourceAsStream(fileName);
		}

		if (is == null) {
			throw new InvalidParameterException("could not find property file : " + fileName);
		}
		props = new Properties();
		props.load(is);

		String respNames = props.getProperty(topLevelPropsKey);

		if (respNames == null) {
			throw new SCMPValidatorException("wrong format of configuration file, top level key not found : "
					+ topLevelPropsKey);
		}

		String[] resps = respNames.split(IConstants.COMMA_OR_SEMICOLON);
		comConfigList = new ArrayList<ICommunicatorConfig>();

		for (String respName : resps) {
			respName = respName.trim(); // remove blanks in name
			CommunicatorConfig reqConfig = new CommunicatorConfig(respName);

			comConfigList.add(reqConfig);

			int port = Integer.parseInt((String) props.get(respName + IConstants.PORT_QUALIFIER));
			String maxPoolSizeValue = (String) props.get(respName + IConstants.MAX_CONNECTION_POOL_SIZE);

			if (maxPoolSizeValue != null) {
				int maxPoolSize = Integer.parseInt(maxPoolSizeValue);
				reqConfig.setMaxPoolSize(maxPoolSize);
			}

			String keepAliveIntervalValue = (String) props.get(respName + IConstants.KEEP_ALIVE_INTERVAL);
			int keepAliveInterval = 0;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			reqConfig.setKeepAliveInterval(keepAliveInterval);

			reqConfig.setPort(port);
			reqConfig.setHost((String) props.get(respName + IConstants.HOST_QUALIFIER));
			reqConfig.setConnectionType((String) props.get(respName + IConstants.CONNECTION_TYPE_QUALIFIER));
			reqConfig.setNumberOfThreads(Integer.parseInt((String) props.get(respName
					+ IConstants.THREAD_POOL_SIZE_QUALIFIER)));

		}
		this.loggerKey = props.getProperty("root.logger");
	}

	/**
	 * Load responder configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadResponderConfig(String fileName) throws Exception {
		this.load(fileName, IConstants.SERVER_LISTENER);
	}

	/**
	 * Load requester configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadRequesterConfig(String fileName) throws Exception {
		this.load(fileName, IConstants.CONNECTIONS);
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
	 * Gets the requester configuration list.
	 * 
	 * @return the requester configuration list
	 */
	public List<ICommunicatorConfig> getCommunicatorConfigList() {
		return comConfigList;
	}
}
