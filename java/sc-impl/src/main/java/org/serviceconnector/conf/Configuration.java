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
package org.serviceconnector.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class Configuration. Processes sc property file.
 * 
 * @author JTraber
 */
public abstract class Configuration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(Configuration.class);

	/** The properties. */
	private CompositeConfiguration configurations;
	/** The requester configuration list. */
	private List<CommunicatorConfig> comConfigList;
	/** The writePID flag. */
	private boolean writePIDFlag;

	/**
	 * Instantiates a new communicator configuration pool.
	 */
	public Configuration() {
		this.comConfigList = null;
		this.configurations = null;
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
		this.configurations = new CompositeConfiguration();
		try {
			this.configurations.addConfiguration(new PropertiesConfiguration(fileName));
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.toString());
		}
		@SuppressWarnings("unchecked")
		List<String> communicators = this.configurations.getList(topLevelPropsKey);
		if (communicators == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "top level key not found: "
					+ topLevelPropsKey);
		}
		comConfigList = new ArrayList<CommunicatorConfig>();
		String operationTimeoutString = this.configurations.getString(Constants.ROOT_OPERATION_TIMEOUT_QUALIFIER);
		double operationTimeoutMultiplier = Constants.OPERATION_TIMEOUT_MULTIPLIER;
		if (operationTimeoutString != null) {
			operationTimeoutMultiplier = Double.parseDouble(operationTimeoutString);
		}

		for (String commName : communicators) {
			commName = commName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(commName);

			comConfigList.add(commConfig);

			int port = Integer.parseInt((String) this.configurations.getString(commName + Constants.PORT_QUALIFIER));
			String maxPoolSizeValue = (String) this.configurations.getString(commName
					+ Constants.MAX_CONNECTION_POOL_SIZE);

			if (maxPoolSizeValue != null) {
				int maxPoolSize = Integer.parseInt(maxPoolSizeValue);
				commConfig.setMaxPoolSize(maxPoolSize);
			}

			String keepAliveIntervalValue = (String) this.configurations.getString(commName
					+ Constants.KEEP_ALIVE_INTERVAL);
			int keepAliveInterval = 0;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			commConfig.setKeepAliveInterval(keepAliveInterval);

			commConfig.setPort(port);
			commConfig.setHost((String) this.configurations.getString(commName + Constants.HOST_QUALIFIER));
			commConfig.setConnectionType((String) this.configurations.getString(commName
					+ Constants.CONNECTION_TYPE_QUALIFIER));
			commConfig.setUserid((String) this.configurations.getString(commName + Constants.CONNECTION_USERNAME));
			commConfig.setPassword((String) this.configurations.getString(commName + Constants.CONNECTION_PASSWORD));
			commConfig.setRemoteURI((String) this.configurations.getString(commName + Constants.REMOTE_URI));
			commConfig.setOperationTimeoutMultiplier(operationTimeoutMultiplier);
		}

		String largeMsgLimitValue = this.configurations.getString(Constants.ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER);
		if (largeMsgLimitValue != null) {
			int largeMsgLimit = Integer.parseInt(largeMsgLimitValue);
			Constants.setLargeMessageLimit(largeMsgLimit);
		}

		String writePIDFlag = this.configurations.getString(Constants.ROOT_WRITEPID_QUALIFIER);
		if (writePIDFlag != null) {
			this.writePIDFlag = true;
		}

		String echoIntervalString = this.configurations.getString(Constants.ROOT_ECHO_INTERVAL_QUALIFIER);
		if (operationTimeoutString != null) {
			double echoIntervalMultiplier = Double.parseDouble(echoIntervalString);
			Constants.setEchoIntervalMultiplier(echoIntervalMultiplier);
		}

		String connectionTimeoutString = this.configurations.getString(Constants.ROOT_CONNECTION_TIMEOUT_QUALIFIER);
		if (connectionTimeoutString != null) {
			int connectionTimeout = Integer.parseInt(connectionTimeoutString);
			Constants.setConnectionTimeout(connectionTimeout);
		}

		String subscriptionTimeoutString = this.configurations.getString(Constants.ROOT_SUBSCRIPTION_TIMEOUT_QUALIFIER);
		if (subscriptionTimeoutString != null) {
			int subscriptionTimeout = Integer.parseInt(subscriptionTimeoutString);
			Constants.setSubscriptionTimeout(subscriptionTimeout);
		}

		String commandValidationString = this.configurations.getString(Constants.ROOT_COMMAND_VALIDATION_ENABLED);
		if (commandValidationString != null) {
			boolean commandValidation = Boolean.parseBoolean(commandValidationString);
			Constants.setCommandValidation(commandValidation);
		}

		String messageCacheString = this.configurations.getString(Constants.ROOT_MESSAGE_CACHE_ENABLED);
		if (messageCacheString != null) {
			boolean messageCache = Boolean.parseBoolean(messageCacheString);
			Constants.setMessageCache(messageCache);
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
	public List<CommunicatorConfig> getCommunicatorConfigList() {
		return comConfigList;
	}

	/**
	 * Checks if is test.
	 */
	public boolean writePID() {
		return this.writePIDFlag;
	}
}
