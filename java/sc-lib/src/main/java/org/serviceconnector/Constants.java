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
package org.serviceconnector;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpHeaders;

/**
 * The Interface IConstants. SCM constants.
 * 
 * @author JTraber
 */
public final class Constants {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(Constants.class);

	private Constants() {
		// instantiating not allowed
	}

	/*
	 * Defaults ********
	 */
	/** Default value used if no ECHO_TIMEOUT_MULTIPLIER is configured */
	public static final double DEFAULT_ECHO_INTERVAL_MULTIPLIER = 1.2;

	/** Default value if no OPERATION_TIMEOUT_MULTIPLIER is configured. */
	public static final double DEFAULT_OPERATION_TIMEOUT_MULTIPLIER = 0.8;

	/** Default value used if no timeout for operation is passed in the API. */
	public static final int DEFAULT_OPERATION_TIMEOUT_SECONDS = 60;

	/** Default value used if no ABORT_SERVER_OTI_MILLIS is configured. */
	public static final int DEFAULT_SERVER_ABORT_OTI_MILLIS = 10000;

	/** Default timeout for file session creation. */
	public static final int DEFAULT_FILE_SESSION_TIMEOUT_SECONDS = 15;

	/** Default timeout for creation of an connection to peer. */
	public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 5000;

	/** Default timeout after a subscription is marked as dead. */
	public static final int DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS = 300000;

	/** Default interval used for publishing services if the NO_DATA_INTERVAL was not set by API. */
	public static final int DEFAULT_NO_DATA_INTERVAL_SECONDS = 300;

	/** Default value used if no KEEP_ALIVE_TIMEOUT is configured. */
	public static final int DEFAULT_KEEP_ALIVE_TIMEOUT = 2000;

	/** The default keep alive interval, 0 = not active. */
	public static final int DEFAULT_KEEP_ALIVE_INTERVAL = 0;

	/** The default number of subsequent keep alive before the connection is closed. */
	public static final int DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE = 10;

	/** The default maximal connection pool size */
	public static final int DEFAULT_MAX_CONNECTION_POOL_SIZE = 100;

	/** Default number of threads for the server. */
	public static final int DEFAULT_NR_OF_THREADS_SERVER = 10000; // TODO not used

	/** Default number of threads for the client. */
	public static final int DEFAULT_NR_OF_THREADS_CLIENT = 5000; // TODO not used

	/*
	 * Constants *********
	 */
	/**
	 * Multiplier to calculate the echo timeout of a session. <br>
	 * SC must adapt (extend) echo interval passed from client to get the right interval for echo messages.
	 */
	public static double ECHO_INTERVAL_MULTIPLIER = DEFAULT_ECHO_INTERVAL_MULTIPLIER;

	/**
	 * Multiplier to calculate the operation timeout.<br>
	 * SC must adapt (shorten) the timeout passed from client to get the right timeout.
	 */
	public static double OPERATION_TIMEOUT_MULTIPLIER = DEFAULT_OPERATION_TIMEOUT_MULTIPLIER;

	/**
	 * Used to observe the reply of a keep alive message. <br>
	 * If the peer does not reply within this time, connection will be cleaned up.
	 */
	public static int KEEP_ALIVE_TIMEOUT = DEFAULT_KEEP_ALIVE_TIMEOUT;

	/**
	 * Used to send keep alive message to the peer. <br>
	 */
	public static int KEEP_ALIVE_INTERVAL = DEFAULT_KEEP_ALIVE_INTERVAL;

	/**
	 * Used to observe the reply of a abort session. <br>
	 * If server does not reply within this time, the server will be cleaned up.
	 */
	public static int SERVER_ABORT_OTI_MILLIS = DEFAULT_SERVER_ABORT_OTI_MILLIS;

	/**
	 * Technical operation timeout. <br>
	 * It is the time a single WRITE/READ/CLOSE/OPEN can take. Must be reasonably sort.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 2000;

	/** Timeout to prevent stocking in technical connect process. */
	public static final int CONNECT_TIMEOUT_MILLIS = DEFAULT_CONNECT_TIMEOUT_MILLIS;

	/** The wait time in a loop waiting for a busy connection. */
	public static final int WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS = 200;

	/** The time after a subscription is marked as dead. */
	public static final int SUBSCRIPTION_TIMEOUT_MILLIS = DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS;

	/** Maximum size of a message. Larger data is treated as large message */
	public static final int MAX_MESSAGE_SIZE = 60 << 10; // 64K

	/** flag to enable / disable command validation. */
	public static final boolean COMMAND_VALIDATION_ENABLED = true;

	/** flag to enable / disable message caching. */
	public static final boolean MESSAGE_CACHE_ENABLED = true;

	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";

	/** Seconds to milliseconds calculation factor */
	public static final int SEC_TO_MILLISEC_FACTOR = 1000;

	/** HttpHeaders.Names.ACCEPT parameter used when http data is sent */
	public static final String HTTP_ACCEPT_PARAMS = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";

	/** Protocol literal. */
	public static final String HTTP = "http";

	/** Protocol literal. */
	public static final String TCP = "tcp";

	/** File qualifier for command line argument configuration file. */
	public static final String CLI_CONFIG_ARG = "-sc.configuration";

	/** Comma or semicolon REGEX. */
	public static final String COMMA_OR_SEMICOLON = ",|;"; // TODO not used

	/*
	 * console command constants *************************
	 */
	public static final String DISABLE = "disable";
	public static final String ENABLE = "enable";
	public static final String STATE = "state";
	public static final String SESSIONS = "sessions";
	public static final String KILL = "kill";
	public static final String EQUAL_SIGN = "=";

	/*
	 * Constants for syntax in sc.properies ************************************
	 */
	public static final String ROOT_WRITEPID = "root.writePID";
	public static final String ROOT_OPERATION_TIMEOUT_MULTIPLIER = "root.operationTimeoutMultiplier";
	public static final String ROOT_ECHO_INTERVAL_MULTIPLIER = "root.echoIntervalMultiplier";
	public static final String ROOT_COMMAND_VALIDATION_ENABLED = "root.commandValidationEnabled";
	public static final String ROOT_CONNECTION_TIMEOUT = "root.connectionTimeoutMillis";
	public static final String ROOT_SUBSCRIPTION_TIMEOUT = "root.subscriptionTimeout";
	public static final String ROOT_KEEP_ALIVE_TIMEOUT = "root.keepAliveTimeout";
	public static final String ROOT_SERVER_ABORT_TIMEOUT = "root.serverAbortTimeout";

	public static final String PROPERTY_LISTENERS = "listeners";
	public static final String PROPERTY_SERVICE_NAMES = "serviceNames";
	public static final String PROPERTY_REMOTE_HOSTS = "remoteHosts";

	public static final String PROPERTY_QUALIFIER_CONNECTION_TYPE = ".connectionType";
	public static final String PROPERTY_QUALIFIER_USERNAME = ".username";
	public static final String PROPERTY_QUALIFIER_PASSWORD = ".password";
	public static final String PROPERTY_QUALIFIER_UPLOAD_SCRIPT_NAME = "scupload.php";
	public static final String PROPERTY_QUALIFIER_REMOTE_HOST = ".remoteHost";
	public static final String PROPERTY_QUALIFIER_HOST = ".host";
	public static final String PROPERTY_QUALIFIER_PORT = ".port";
	public static final String PROPERTY_QUALIFIER_TYPE = ".type";
	public static final String PROPERTY_QUALIFIER_ENABLED = ".enabled";
	public static final String PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE = ".maxConnectionPoolSize";
	public static final String PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL = ".keepAliveInterval";
	public static final String PROPERTY_QUALIFIER_PATH = ".path";

	public static final boolean DEFAULT_CACHE_ENABLED = false;
	public static final String DEFAULT_CACHE_NAME = "scCache";
	public static final boolean DEFAULT_CACHE_DISK_PERSISTENT = true;
	public static final String DEFAULT_CACHE_DISK_PATH = "";
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY = 10000;
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK = 100000;

	public static final String CACHE_ENABLED = "cache.enabled";
	public static final String CACHE_NAME = "cache.name";
	public static final String CACHE_DISK_PERSISTENT = "cache.diskPersistent";
	public static final String CACHE_DISK_PATH = "cache.diskPath";
	public static final String CACHE_MAX_ELEMENTS_IN_MEMORY = "cache.maxElementsInMemory";
	public static final String CACHE_MAX_ELEMENTS_ON_DISK = "cache.maxElementsOnDisk";

	/*
	 * SCMP protocol constants ***********************
	 */
	/** Carriage return character. */
	public static final byte SCMP_CR = 0x0D;
	/** Line feed character. */
	public static final byte SCMP_LF = 0x0A;
	public static final int SCMP_HEADLINE_SIZE = 22;
	public static final int SCMP_HEADLINE_SIZE_WITHOUT_VERSION = 18;
	public static final int SCMP_MSG_SIZE_START = 4;
	public static final int SCMP_MSG_SIZE_END = 10;
	public static final int SCMP_HEADER_SIZE_START = 12;
	public static final int SCMP_HEADER_SIZE_END = 16;
	public static final int SCMP_VERSION_LENGTH_IN_HEADLINE = 3;
	public static final String SCMP_FORMAT_OF_MSG_SIZE = " 0000000";
	public static final String SCMP_FORMAT_OF_HEADER_SIZE = " 00000";
	public static final int MAX_HTTP_CONTENT_LENGTH = Integer.MAX_VALUE; // 2^31-1 => 2147483647, 2GB

}