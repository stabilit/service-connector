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
	private CompositeConfiguration apacheCompositeConfig;
	/** The responder (listeners) or requester (remoteHosts) configuration list. */
	private List<CommunicatorConfig> communicatorConfigList;
	/** The writePID flag. */
	private boolean writePIDFlag;

	/**
	 * Instantiates a new configuration.
	 */
	public Configuration() {
		this.communicatorConfigList = null;
		this.apacheCompositeConfig = null;
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
	private void load(String fileName, String propertyName) throws Exception {
		this.apacheCompositeConfig = new CompositeConfiguration();
		try {
			this.apacheCompositeConfig.addConfiguration(new PropertiesConfiguration(fileName));
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.toString());
		}
		@SuppressWarnings("unchecked")
		List<String> communicatorsList = this.apacheCompositeConfig.getList(propertyName);
		if (communicatorsList == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property:"+propertyName+" not found");
		}
		
		// load all communicators in the list into the array
		communicatorConfigList = new ArrayList<CommunicatorConfig>();
		for (String communicatorName : communicatorsList) {
			communicatorName = communicatorName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(communicatorName);
			commConfig.initialize(this.apacheCompositeConfig);
			communicatorConfigList.add(commConfig);
		}

		String operationTimeoutString = this.apacheCompositeConfig.getString(Constants.ROOT_OPERATION_TIMEOUT_MULTIPLIER);
		double operationTimeoutMultiplier = Constants.OPERATION_TIMEOUT_MULTIPLIER;
		if (operationTimeoutString != null) {
			operationTimeoutMultiplier = Double.parseDouble(operationTimeoutString);
			Constants.setOperationTimeoutMultiplier(operationTimeoutMultiplier);
		}
		
		String writePIDFlag = this.apacheCompositeConfig.getString(Constants.ROOT_WRITEPID);
		if (writePIDFlag != null) {
			this.writePIDFlag = true;
		}

		String echoIntervalString = this.apacheCompositeConfig.getString(Constants.ROOT_ECHO_INTERVAL_MULTIPLIER);
		if (operationTimeoutString != null) {
			double echoIntervalMultiplier = Double.parseDouble(echoIntervalString);
			Constants.setEchoIntervalMultiplier(echoIntervalMultiplier);
		}

		String connectionTimeoutString = this.apacheCompositeConfig.getString(Constants.ROOT_CONNECTION_TIMEOUT);
		if (connectionTimeoutString != null) {
			int connectionTimeout = Integer.parseInt(connectionTimeoutString);
			Constants.setConnectionTimeoutMillis(connectionTimeout);
		}

		String subscriptionTimeoutString = this.apacheCompositeConfig.getString(Constants.ROOT_SUBSCRIPTION_TIMEOUT);
		if (subscriptionTimeoutString != null) {
			int subscriptionTimeout = Integer.parseInt(subscriptionTimeoutString);
			Constants.setSubscriptionTimeout(subscriptionTimeout);
		}

		String commandValidationString = this.apacheCompositeConfig.getString(Constants.ROOT_COMMAND_VALIDATION_ENABLED);
		if (commandValidationString != null) {
			boolean commandValidation = Boolean.parseBoolean(commandValidationString);
			Constants.setCommandValidation(commandValidation);
		}

		String messageCacheString = this.apacheCompositeConfig.getString(Constants.ROOT_MESSAGE_CACHE_ENABLED);
		if (messageCacheString != null) {
			boolean messageCache = Boolean.parseBoolean(messageCacheString);
			Constants.setMessageCache(messageCache);
		}

		String keepAliveTimeoutString = this.apacheCompositeConfig.getString(Constants.ROOT_KEEP_ALIVE_TIMEOUT);
		if (keepAliveTimeoutString != null) {
			int keepAliveTimeout = Integer.parseInt(keepAliveTimeoutString);
			Constants.setKeepAliveTimeout(keepAliveTimeout);
		}
	
		String srvAbortTimeoutString = this.apacheCompositeConfig.getString(Constants.ROOT_SERVER_ABORT_TIMEOUT);
		if (srvAbortTimeoutString != null) {
			int srvAbortTimeout = Integer.parseInt(srvAbortTimeoutString);
			Constants.setServerAbortTimeout(srvAbortTimeout);
		}
	}

	/**
	 * Load responder (listeners) configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadResponderConfig(String fileName) throws Exception {
		this.load(fileName, Constants.PROPERTY_LISTENERS);
	}

	/**
	 * Load requester (remote hosts) configuration.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 */
	public void loadRequesterConfig(String fileName) throws Exception {
		this.load(fileName, Constants.PROPERTY_REMOTE_HOSTS);
	}

	/**
	 * Gets the communicators configuration list.
	 * 
	 * @return the communicators configuration list
	 */
	public List<CommunicatorConfig> getCommunicatorConfigList() {
		return communicatorConfigList;
	}

	/**
	 * Checks if is test.
	 */
	public boolean writePID() {
		return this.writePIDFlag;
	}
}
