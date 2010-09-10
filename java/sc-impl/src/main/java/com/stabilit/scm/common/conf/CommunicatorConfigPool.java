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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.scmp.SCMPError;

/**
 * The Class CommunicatorConfigPool. Processes scm property files.
 * 
 * @author JTraber
 */
public abstract class CommunicatorConfigPool {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommunicatorConfigPool.class);

	/** The properties. */
	private Properties props;
	/** The requester configuration list. */
	private List<ICommunicatorConfig> comConfigList;
	/** The writePID flag. */
	private boolean writePIDFlag;

	/**
	 * Instantiates a new communicator configuration pool.
	 */
	public CommunicatorConfigPool() {
		this.comConfigList = null;
		this.props = null;
		this.writePIDFlag = false;
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
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "could not find property file: "
					+ fileName);
		}
		props = new Properties();
		props.load(is);

		String respNames = props.getProperty(topLevelPropsKey);

		if (respNames == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "top level key not found: "
					+ topLevelPropsKey);
		}

		String[] communicators = respNames.split(Constants.COMMA_OR_SEMICOLON);
		comConfigList = new ArrayList<ICommunicatorConfig>();

		String operationTimeoutString = props.getProperty(Constants.ROOT_OPERATION_TIMEOUT_QUALIFIER);
		double operationTimeoutMultiplier = Constants.OPERATION_TIMEOUT_MULTIPLIER;
		if (operationTimeoutString != null) {
			operationTimeoutMultiplier = Double.parseDouble(operationTimeoutString);
		}

		for (String commName : communicators) {
			commName = commName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(commName);

			comConfigList.add(commConfig);

			int port = Integer.parseInt((String) props.get(commName + Constants.PORT_QUALIFIER));
			String maxPoolSizeValue = (String) props.get(commName + Constants.MAX_CONNECTION_POOL_SIZE);

			if (maxPoolSizeValue != null) {
				int maxPoolSize = Integer.parseInt(maxPoolSizeValue);
				commConfig.setMaxPoolSize(maxPoolSize);
			}

			String keepAliveIntervalValue = (String) props.get(commName + Constants.KEEP_ALIVE_INTERVAL);
			int keepAliveInterval = 0;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			commConfig.setKeepAliveInterval(keepAliveInterval);

			commConfig.setPort(port);
			commConfig.setHost((String) props.get(commName + Constants.HOST_QUALIFIER));
			commConfig.setConnectionType((String) props.get(commName + Constants.CONNECTION_TYPE_QUALIFIER));
			commConfig.setOperationTimeoutMultiplier(operationTimeoutMultiplier);
		}

		String largeMsgLimitValue = props.getProperty(Constants.ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER);
		if (largeMsgLimitValue != null) {
			int largeMsgLimit = Integer.parseInt(largeMsgLimitValue);
			Constants.setLargeMessageLimit(largeMsgLimit);
		}

		String writePIDFlag = props.getProperty(Constants.ROOT_WRITEPID_QUALIFIER);
		if (writePIDFlag != null) {
			this.writePIDFlag = true;
		}

		String echoIntervalString = props.getProperty(Constants.ROOT_ECHO_INTERVAL_QUALIFIER);
		if (operationTimeoutString != null) {
			double echoIntervalMultiplier = Double.parseDouble(echoIntervalString);
			Constants.setEchoIntervalMultiplier(echoIntervalMultiplier);
		}
	}

	/**
	 * Load responder configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadResponderConfig(String fileName) throws Exception {
		this.load(fileName, Constants.SERVER_LISTENER);
	}

	/**
	 * Load requester configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadRequesterConfig(String fileName) throws Exception {
		this.load(fileName, Constants.CONNECTIONS);
	}

	/**
	 * Gets the requester configuration list.
	 * 
	 * @return the requester configuration list
	 */
	public List<ICommunicatorConfig> getCommunicatorConfigList() {
		return comConfigList;
	}

	/**
	 * Checks if is test.
	 */
	public boolean writePID() {
		return this.writePIDFlag;
	}
}
