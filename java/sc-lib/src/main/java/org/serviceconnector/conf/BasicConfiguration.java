/*
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
 */
package org.serviceconnector.conf;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

// TODO: Auto-generated Javadoc
/**
 * root.writePID=true root.operationTimeoutMultiplier=0.8 root.echoIntervalMultiplier=1.2 root.connectionTimeoutMillis=10000
 * 
 * @author Daniel Schmutz
 * 
 */
public class BasicConfiguration {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(BasicConfiguration.class);
	/** The write pid. */
	private boolean writePID = true;
	/** The operation timeout multiplier. */
	private double operationTimeoutMultiplier = Constants.DEFAULT_OPERATION_TIMEOUT_MULTIPLIER;
	/** The echo interval multiplier. */
	private double echoIntervalMultiplier = Constants.DEFAULT_ECHO_INTERVAL_MULTIPLIER;
	/** The connection timeout millis. */
	private int connectionTimeoutMillis = Constants.CONNECT_TIMEOUT_MILLIS;	
	/** The subscription timeout. */
	private int subscriptionTimeout = Constants.DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS;
	/** The command validation. */
	private boolean commandValidation = Constants.COMMAND_VALIDATION_ENABLED;
	/** The keep alive timeout. */
	private int keepAliveTimeout = Constants.KEEP_ALIVE_TIMEOUT;
	/** The srv abort timeout. */
	private int srvAbortTimeout = Constants.SERVER_ABORT_OTI_MILLIS;
	
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
	public int getSubscriptionTimeout() {
		return subscriptionTimeout;
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
	 * Gets the keep alive timeout.
	 *
	 * @return the keep alive timeout
	 */
	public int getKeepAliveTimeout() {
		return keepAliveTimeout;
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
		try {
			this.writePID = compositeConfiguration.getBoolean(Constants.ROOT_WRITEPID);
			logger.info("basic configuration: writePID is " + this.writePID);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.operationTimeoutMultiplier = compositeConfiguration.getDouble(Constants.ROOT_OPERATION_TIMEOUT_MULTIPLIER);
			logger.info("basic configuration: operationTimeoutMultiplier is " + this.operationTimeoutMultiplier);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.echoIntervalMultiplier = compositeConfiguration.getDouble(Constants.ROOT_ECHO_INTERVAL_MULTIPLIER);
			logger.info("basic configuration: echoIntervalMultiplier is " + this.echoIntervalMultiplier);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.connectionTimeoutMillis = compositeConfiguration.getInt(Constants.ROOT_CONNECTION_TIMEOUT);
			logger.info("basic configuration: connectionTimeoutMillis is " + this.connectionTimeoutMillis);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.subscriptionTimeout = compositeConfiguration.getInt(Constants.ROOT_SUBSCRIPTION_TIMEOUT);
			logger.info("basic configuration: subscriptionTimeout is " + this.subscriptionTimeout);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.commandValidation = compositeConfiguration.getBoolean(Constants.ROOT_COMMAND_VALIDATION_ENABLED);
			logger.info("basic configuration: commandValidation is " + this.commandValidation);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.keepAliveTimeout = compositeConfiguration.getInt(Constants.ROOT_KEEP_ALIVE_TIMEOUT);
			logger.info("basic configuration: keepAliveTimeout is " + this.keepAliveTimeout);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		try {
			this.srvAbortTimeout = compositeConfiguration.getInt(Constants.ROOT_COMMAND_VALIDATION_ENABLED);
			logger.info("basic configuration: srvAbortTimeout is " + this.srvAbortTimeout);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}
}
