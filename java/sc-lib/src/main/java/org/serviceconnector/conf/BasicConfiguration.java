/*
 * jettz * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.conf;

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * root.writePID=true root.operationTimeoutMultiplier=0.8 root.echoIntervalMultiplier=1.2 root.connectionTimeoutMillis=10000
 * 
 * @author Daniel Schmutz
 */
public class BasicConfiguration {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(BasicConfiguration.class);
	/** The write pid. */
	private boolean writePID = Constants.DEFAULT_WRITE_PID_FLAG;
	/** The Pid file path. */
	private String pidPath = null;
	/** The dump file path. */
	private String dumpPath = null;

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
	/**
	 * The subscription timeout. Monitors the maximum time between receive publication. If this timeout expires, subscription is
	 * deleted
	 */
	private int subscriptionTimeoutMillis = Constants.DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS;
	/** The command validation. */
	private boolean commandValidation = Constants.COMMAND_VALIDATION_ENABLED;
	/**
	 * Used to observe the reply of a keep alive message. <br>
	 * If the peer does not reply within this time, connection will be cleaned up.
	 */
	private int keepAliveOTIMillis = Constants.DEFAULT_KEEP_ALIVE_OTI_MILLIS;
	/**
	 * Used to observe the reply of a abort session. <br>
	 * If server does not reply within this time, the server will be cleaned up.
	 */
	private int srvAbortOTIMillis = Constants.DEFAULT_SERVER_ABORT_OTI_MILLIS;
	/**
	 * the maximum size of a message (part) Larger messages will be splitted . <br>
	 */
	private int maxMessageSize = Constants.DEFAULT_MAX_MESSAGE_SIZE;

	/**
	 * Instantiates a new basic configuration.
	 */
	public BasicConfiguration() {
	}

	/**
	 * inits the configuration.
	 * 
	 * @param compositeConfiguration
	 *            the composite configuration
	 */
	public void load(CompositeConfiguration compositeConfiguration) throws SCMPValidatorException {
		// writePID
		Boolean localWritePID = compositeConfiguration.getBoolean(Constants.ROOT_WRITEPID, null);
		if (localWritePID != null && this.writePID != localWritePID) {
			this.writePID = localWritePID;
			logger.info("writePID set to " + localWritePID);
		}

		// pidPath
		String localPidPath = compositeConfiguration.getString(Constants.ROOT_PID_PATH, null);
		if (localPidPath == null && this.writePID) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.ROOT_PID_PATH
					+ " is missing");
		}
		if (this.pidPath != localPidPath) {
			File configFile = new File(localPidPath);
			if (configFile.exists() == true && configFile.isDirectory() == false) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
						+ Constants.ROOT_PID_PATH + " is not a directory");
			}
			this.pidPath = configFile.getAbsolutePath();
			logger.info("pidPath set to " + this.pidPath);
		}

		// dumpPath
		String localdumpPath = compositeConfiguration.getString(Constants.ROOT_DUMP_PATH, null);
		if (localdumpPath == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.ROOT_DUMP_PATH
					+ " is missing");
		}
		if (this.dumpPath != localdumpPath) {
			File dumpFile = new File(localdumpPath);
			if (dumpFile.exists() == true && dumpFile.isDirectory() == false) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
						+ Constants.ROOT_DUMP_PATH + " is not a directory");
			}
			this.dumpPath = dumpFile.getAbsolutePath();
			logger.info("dumpPath set to " + this.dumpPath);
		}

		// maxMessageSize
		Integer localMaxMessageSize = compositeConfiguration.getInteger(Constants.ROOT_MAX_MESSAGE_SIZE, null);
		if (localMaxMessageSize != null && this.maxMessageSize != localMaxMessageSize) {
			this.maxMessageSize = localMaxMessageSize;
			logger.info("maxMessageSize set to " + localMaxMessageSize);
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
		Integer localConnectionTimeoutMultiplier = compositeConfiguration
				.getInteger(Constants.ROOT_CONNECTION_TIMEOUT_MILLIS, null);
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
		Integer localKeepAliveOTIMillis = compositeConfiguration.getInteger(Constants.ROOT_KEEP_ALIVE_OTI_MILLIS, null);
		if (localKeepAliveOTIMillis != null && this.keepAliveOTIMillis != localKeepAliveOTIMillis) {
			this.keepAliveOTIMillis = localKeepAliveOTIMillis;
			logger.info("keepAliveTimeoutMillis set to " + localKeepAliveOTIMillis);
		}

		// serverAbortTimeout
		Integer localSrvAbortOTIMillis = compositeConfiguration.getInteger(Constants.ROOT_SERVER_ABORT_OTI_MILLIS, null);
		if (localSrvAbortOTIMillis != null && this.srvAbortOTIMillis != localSrvAbortOTIMillis) {
			this.srvAbortOTIMillis = localSrvAbortOTIMillis;
			logger.info("srvAbortOTIMillis set to " + localSrvAbortOTIMillis);
		}
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
	 * Gets the keep alive oti in milliseconds.
	 * 
	 * @return the keep alive oti in milliseconds
	 */
	public int getKeepAliveOTIMillis() {
		return keepAliveOTIMillis;
	}

	/**
	 * Gets the srv abort timeout.
	 * 
	 * @return the srv abort timeout
	 */
	public int getSrvAbortOTIMillis() {
		return srvAbortOTIMillis;
	}

	/**
	 * Gets the path to the directory where pid file should be written to
	 * 
	 * @return the pid path
	 */
	public String getPidPath() {
		return pidPath;
	}

	/**
	 * Gets the path to the directory where dump file should be written to
	 * 
	 * @return the dump path
	 */
	public String getDumpPath() {
		return dumpPath;
	}

	/**
	 * Gets the maximum message size
	 * 
	 * @return the maximum message size
	 */
	public int getMaxMessageSize() {
		return maxMessageSize;
	}

}
