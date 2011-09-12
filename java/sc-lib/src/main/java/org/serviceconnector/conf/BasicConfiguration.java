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

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class BasicConfiguration.
 */
public class BasicConfiguration {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(BasicConfiguration.class);
	/** The write pid. */
	private boolean writePID = Constants.DEFAULT_WRITE_PID_FLAG;
	/** The Pid file path. */
	private String pidPath = null;
	/** The dump file path. */
	private String dumpPath = null;

	/**
	 * defines the maximum of I/O threads are going to be acquired to handle incoming/outgoing messages.
	 */
	private int maxIOThreads = Constants.DEFAULT_MAX_IO_THREADS;
	/**
	 * Multiplier to calculate the check registration interval.<br />
	 * SC must adapt (extend) the interval passed from server to get the right timeout.
	 */
	private double checkRegistrationIntervalMultiplier = Constants.DEFAULT_CHECK_REGISTRATION_INTERVAL_MULTIPLIER;
	/**
	 * Multiplier to calculate the operation timeout.<br />
	 * SC must adapt (shorten) the timeout passed from client to get the right timeout.
	 */
	private double operationTimeoutMultiplier = Constants.DEFAULT_OPERATION_TIMEOUT_MULTIPLIER;
	/**
	 * Multiplier to calculate the echo timeout of a session. <br />
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
	 * Used to observe the reply of a keep alive message. <br />
	 * If the peer does not reply within this time, connection will be cleaned up.
	 */
	private int keepAliveOTIMillis = Constants.DEFAULT_KEEP_ALIVE_OTI_MILLIS;
	/**
	 * Used to observe the reply of a abort session. <br />
	 * If server does not reply within this time, the server will be cleaned up.
	 */
	private int srvAbortOTIMillis = Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR;

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
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void load(CompositeConfiguration compositeConfiguration) throws SCMPValidatorException {
		// writePID
		Boolean localWritePID = compositeConfiguration.getBoolean(Constants.ROOT_WRITEPID, null);
		if (localWritePID != null && this.writePID != localWritePID) {
			this.writePID = localWritePID;
		}
		LOGGER.info("writePID=" + this.writePID);

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
		}
		LOGGER.info("pidPath=" + this.pidPath);

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
		}
		LOGGER.info("dumpPath=" + this.dumpPath);

		// maxIOThreads
		Integer localMaxIOThreads = compositeConfiguration.getInteger(Constants.ROOT_MAX_IO_THREADS, null);
		if (localMaxIOThreads == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
					+ Constants.ROOT_MAX_IO_THREADS + " is missing");
		}
		this.maxIOThreads = localMaxIOThreads;
		LOGGER.info("maxIOThreads=" + this.maxIOThreads);

		// operationTimeoutMultiplier
		Double localOTIMultiplier = compositeConfiguration.getDouble(Constants.ROOT_OPERATION_TIMEOUT_MULTIPLIER, null);
		if (localOTIMultiplier != null && this.operationTimeoutMultiplier != localOTIMultiplier) {
			this.operationTimeoutMultiplier = localOTIMultiplier;
		}
		LOGGER.info("operationTimeoutMultiplier=" + this.operationTimeoutMultiplier);

		// checkRegistrationIntervalMultiplier
		Double localCheckRegistrationIntervalMultiplier = compositeConfiguration.getDouble(
				Constants.ROOT_CHECK_REGISTRATION_INTERVAL_MULTIPLIER, null);
		if (localCheckRegistrationIntervalMultiplier != null
				&& this.checkRegistrationIntervalMultiplier != localCheckRegistrationIntervalMultiplier) {
			this.checkRegistrationIntervalMultiplier = localCheckRegistrationIntervalMultiplier;
		}
		LOGGER.info("checkRegistrationIntervalMultiplier=" + this.checkRegistrationIntervalMultiplier);

		// echoIntervalMultiplier
		Double localECIMultiplier = compositeConfiguration.getDouble(Constants.ROOT_ECHO_INTERVAL_MULTIPLIER, null);
		if (localECIMultiplier != null && this.echoIntervalMultiplier != localECIMultiplier) {
			this.echoIntervalMultiplier = localECIMultiplier;
		}
		LOGGER.info("echoIntervalMultiplier=" + this.echoIntervalMultiplier);

		// connectionTimeoutMillis
		Integer localConnectionTimeoutMultiplier = compositeConfiguration
				.getInteger(Constants.ROOT_CONNECTION_TIMEOUT_MILLIS, null);
		if (localConnectionTimeoutMultiplier != null && this.connectionTimeoutMillis != localConnectionTimeoutMultiplier) {
			this.connectionTimeoutMillis = localConnectionTimeoutMultiplier;
		}
		LOGGER.info("connectionTimeoutMillis=" + this.connectionTimeoutMillis);

		// subscriptionTimeout
		Integer localSubscriptionTimeout = compositeConfiguration.getInteger(Constants.ROOT_SUBSCRIPTION_TIMEOUT_MILLIS, null);
		if (localSubscriptionTimeout != null && this.subscriptionTimeoutMillis != localSubscriptionTimeout) {
			this.subscriptionTimeoutMillis = localSubscriptionTimeout;
		}
		LOGGER.info("subscriptionTimeoutMillis=" + this.subscriptionTimeoutMillis);

		// commandValidation
		Boolean localCMDValidation = compositeConfiguration.getBoolean(Constants.ROOT_COMMAND_VALIDATION_ENABLED, null);
		if (localCMDValidation != null && this.commandValidation != localCMDValidation) {
			this.commandValidation = localCMDValidation;
		}
		LOGGER.info("commandValidation=" + this.commandValidation);

		// keepAliveTimeout in milliseconds
		Integer localKeepAliveOTIMillis = compositeConfiguration.getInteger(Constants.ROOT_KEEP_ALIVE_OTI_MILLIS, null);
		if (localKeepAliveOTIMillis != null && this.keepAliveOTIMillis != localKeepAliveOTIMillis) {
			this.keepAliveOTIMillis = localKeepAliveOTIMillis;
		}
		LOGGER.info("keepAliveOTIMillis=" + this.keepAliveOTIMillis);

		// serverAbortTimeout
		Integer localSrvAbortOTIMillis = compositeConfiguration.getInteger(Constants.ROOT_SERVER_ABORT_OTI_MILLIS, null);
		if (localSrvAbortOTIMillis != null && this.srvAbortOTIMillis != localSrvAbortOTIMillis) {
			this.srvAbortOTIMillis = localSrvAbortOTIMillis;
			LOGGER.info("srvAbortOTIMillis set to " + localSrvAbortOTIMillis);
		}
		LOGGER.info("srvAbortOTIMillis=" + this.srvAbortOTIMillis);
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
	 * Gets the max io threads.
	 * 
	 * @return the max io threads
	 */
	public int getMaxIOThreads() {
		return maxIOThreads;
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
	 * Gets the check registration interval multiplier.
	 * 
	 * @return the check registration interval multiplier
	 */
	public double getCheckRegistrationIntervalMultiplier() {
		return checkRegistrationIntervalMultiplier;
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
	 * Gets the connection timeout milliseconds.
	 * 
	 * @return the connection timeout milliseconds
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
	 * Gets the path to the directory where pid file should be written to.
	 * 
	 * @return the pid path
	 */
	public String getPidPath() {
		return pidPath;
	}

	/**
	 * Gets the path to the directory where dump file should be written to.
	 * 
	 * @return the dump path
	 */
	public String getDumpPath() {
		return dumpPath;
	}

	/**
	 * Dump the basic configuration into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("configuration");
		writer.writeElement("commandValidation", this.commandValidation);
		writer.writeElement("dumpPath", this.dumpPath);
		writer.writeElement("pidPath", this.pidPath);
		writer.writeElement("writePID", this.writePID);
		writer.writeElement("echoIntervalMultiplier", this.echoIntervalMultiplier);
		writer.writeElement("checkRegistrationIntervalMultiplier", this.checkRegistrationIntervalMultiplier);
		writer.writeElement("operationTimeoutMultiplier", this.operationTimeoutMultiplier);
		writer.writeElement("connectionTimeoutMillis", this.connectionTimeoutMillis);
		writer.writeElement("keepAliveOTIMillis", this.keepAliveOTIMillis);
		writer.writeElement("srvAbortOTIMillis", this.srvAbortOTIMillis);
		writer.writeElement("subscriptionTimeoutMillis", this.subscriptionTimeoutMillis);
		writer.writeEndElement(); // end of configuration
	}

}
