/*
jettz *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
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
 */
package org.serviceconnector.conf;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * root.writePID=true root.operationTimeoutMultiplier=0.8 root.echoIntervalMultiplier=1.2 root.connectionTimeoutMillis=10000
 * 
 * @author Daniel Schmutz
 */
public class BasicConfiguration {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(BasicConfiguration.class);
	/** The write pid. */
	private boolean writePID = false;

	/**
	 * Multiplier to calculate the operation timeout.<br>
	 * SC must adapt (shorten) the timeout passed from client to get the right timeout.
	 */
	private double operationTimeoutMultiplier = Constants.DEFAULT_OPERATION_TIMEOUT_MULTIPLIER;
	/**
	 * Multiplier to calculate the echo timeout of a session. <br>
	 * SC must adapt (extend) echo interval passed from client to get the right interval for echo messages.
	 */
	private double echoIntervalMultiplier = Constants.DEFAULT_ECHO_INTERVAL_MULTIPLIER;
	/** Timeout to prevent stocking in technical connect process. */
	private int connectionTimeoutMillis = Constants.DEFAULT_CONNECT_TIMEOUT_MILLIS;
	/** The subscription timeout. */
	private int subscriptionTimeoutMillis = Constants.DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS;
	/** The command validation. */
	private boolean commandValidation = Constants.COMMAND_VALIDATION_ENABLED;
	/**
	 * Used to observe the reply of a keep alive message. <br>
	 * If the peer does not reply within this time, connection will be cleaned up.
	 */
	private int keepAliveTimeoutMillis = Constants.DEFAULT_KEEP_ALIVE_TIMEOUT_MILLIS;
	/**
	 * Used to observe the reply of a abort session. <br>
	 * If server does not reply within this time, the server will be cleaned up.
	 */
	private int srvAbortTimeout = Constants.DEFAULT_SERVER_ABORT_OTI_MILLIS;

	/**
	 * Instantiates a new basic configuration.
	 */
	public BasicConfiguration() {
	}

	/**
	 * Checks if is write pid.
	 * 
	 * @return true, if is write pid
	 */
	public boolean isWritePID() {
		return writePID;
	}

	/**
	 * Gets the operation timeout multiplier.
	 * 
	 * @return the operation timeout multiplier
	 */
	public double getOperationTimeoutMultiplier() {
		return operationTimeoutMultiplier;
	}

	/**
	 * Gets the echo interval multiplier.
	 * 
	 * @return the echo interval multiplier
	 */
	public double getEchoIntervalMultiplier() {
		return echoIntervalMultiplier;
	}

	/**
	 * Gets the connection timeout millis.
	 * 
	 * @return the connection timeout millis
	 */
	public int getConnectionTimeoutMillis() {
		return connectionTimeoutMillis;
	}

	/**
	 * Gets the subscription timeout.
	 * 
	 * @return the subscription timeout
	 */
	public int getSubscriptionTimeoutMillis() {
		return subscriptionTimeoutMillis;
	}

	/**
	 * Checks if is command validation.
	 * 
	 * @return true, if is command validation
	 */
	public boolean isCommandValidation() {
		return commandValidation;
	}

	/**
	 * Gets the keep alive timeout in milliseconds.
	 * 
	 * @return the keep alive timeout in milliseconds
	 */
	public int getKeepAliveTimeoutMillis() {
		return keepAliveTimeoutMillis;
	}

	/**
	 * Gets the srv abort timeout.
	 * 
	 * @return the srv abort timeout
	 */
	public int getSrvAbortTimeout() {
		return srvAbortTimeout;
	}

	/**
	 * inits the configuration.
	 * 
	 * @param compositeConfiguration
	 *            the composite configuration
	 */
	public void init(CompositeConfiguration compositeConfiguration) {
		// writePID
		Boolean localWritePID = compositeConfiguration.getBoolean(Constants.ROOT_WRITEPID, null);
		if (localWritePID != null && this.writePID != localWritePID) {
			this.writePID = localWritePID;
			logger.info("writePID set to " + localWritePID);
		}

		// operationTimeoutMultiplier
		Double localOTIMultiplier = compositeConfiguration.getDouble(Constants.ROOT_OPERATION_TIMEOUT_MULTIPLIER, null);
		if (localOTIMultiplier != null && this.operationTimeoutMultiplier != localOTIMultiplier) {
			this.operationTimeoutMultiplier = localOTIMultiplier;
			logger.info("operationTimeoutMultiplier set to " + localOTIMultiplier);
		}

		// echoIntervalMultiplier
		Double localECIMultiplier = compositeConfiguration.getDouble(Constants.ROOT_ECHO_INTERVAL_MULTIPLIER, null);
		if (localECIMultiplier != null && this.echoIntervalMultiplier != localECIMultiplier) {
			this.echoIntervalMultiplier = localECIMultiplier;
			logger.info("echoIntervalMultiplier set to " + localECIMultiplier);
		}

		// connectionTimeoutMillis
		Integer localConnectionTimeoutMultiplier = compositeConfiguration.getInteger(Constants.ROOT_CONNECTION_TIMEOUT_MILLIS, null);
		if (localConnectionTimeoutMultiplier != null && this.connectionTimeoutMillis != localConnectionTimeoutMultiplier) {
			this.connectionTimeoutMillis = localConnectionTimeoutMultiplier;
			logger.info("connectionTimeoutMillis set to " + localConnectionTimeoutMultiplier);
		}

		// subscriptionTimeout
		Integer localSubscriptionTimeout = compositeConfiguration.getInteger(Constants.ROOT_SUBSCRIPTION_TIMEOUT_MILLIS, null);
		if (localSubscriptionTimeout != null && this.subscriptionTimeoutMillis != localSubscriptionTimeout) {
			this.subscriptionTimeoutMillis = localSubscriptionTimeout;
			logger.info("subscriptionTimeout set to " + localSubscriptionTimeout);
		}

		// commandValidation
		Boolean localCMDValidation = compositeConfiguration.getBoolean(Constants.ROOT_COMMAND_VALIDATION_ENABLED, null);
		if (localCMDValidation != null && this.commandValidation != localCMDValidation) {
			this.commandValidation = localCMDValidation;
			logger.info("commandValidation set to " + localCMDValidation);
		}
		// keepAliveTimeout in milliseconds
		Integer localKeepAliveTimeoutMillis = compositeConfiguration.getInteger(Constants.ROOT_KEEP_ALIVE_TIMEOUT_MILLIS, null);
		if (localKeepAliveTimeoutMillis != null && this.keepAliveTimeoutMillis != localKeepAliveTimeoutMillis) {
			this.keepAliveTimeoutMillis = localKeepAliveTimeoutMillis;
			logger.info("keepAliveTimeoutMillis set to " + localKeepAliveTimeoutMillis);
		}

		// serverAbortTimeout
		Integer localSrvAbortTimeout = compositeConfiguration.getInteger(Constants.ROOT_COMMAND_VALIDATION_ENABLED, null);
		if (localSrvAbortTimeout != null && this.srvAbortTimeout != localSrvAbortTimeout) {
			this.srvAbortTimeout = localSrvAbortTimeout;
			logger.info("srvAbortTimeout set to " + localSrvAbortTimeout);
		}
	}
}
