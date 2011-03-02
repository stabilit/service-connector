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

/**
 * The class Constants. SC constants.
 * 
 * @author JTraber
 */
public final class Constants {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(Constants.class);

	private Constants() {
		// instantiating not allowed
	}

	/*
	 * Defaults ********
	 */
	/** The default SC character set (encoding). */
	public static final String DEFAULT_ENCODING = "ISO-8859-1";

	/** Default value used if no ECHO_TIMEOUT_MULTIPLIER is configured */
	public static final double DEFAULT_ECHO_INTERVAL_MULTIPLIER = 1.2;

	/** Default value used if no interval is passed in the API */
	public static final int DEFAULT_ECHO_INTERVAL_SECONDS = 60;

	/** Default value if no OPERATION_TIMEOUT_MULTIPLIER is configured. */
	public static final double DEFAULT_OPERATION_TIMEOUT_MULTIPLIER = 0.8;

	/** Default value used if no timeout for operation is passed in the API. */
	public static final int DEFAULT_OPERATION_TIMEOUT_SECONDS = 60;

	/** Default value used if no ABORT_SERVER_OTI_MILLIS is configured. */
	public static final int DEFAULT_SERVER_ABORT_OTI_MILLIS = 10000;

	/** Default timeout for file session creation. */
	public static final int DEFAULT_FILE_SESSION_TIMEOUT_SECONDS = 15;

	/** Default timeout for creation of a connection to peer. */
	public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 5000;

	/**
	 * Maximal time between subsequent receive publication.
	 * After this time subscription is marked as dead.
	 * This is analogous to echoInterval in session services
	 */
	public static final int DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS = 60000;

	/**
	 * Default interval used for publishing services if the NO_DATA_INTERVAL was
	 * not set by API.
	 */
	public static final int DEFAULT_NO_DATA_INTERVAL_SECONDS = 300;

	/**
	 * Default value to wait in a receive publication call.
	 * Careful: For the total OTI of RCP noDataInterval will be added.
	 */
	public static final int DEFAULT_RECEIVE_PUBLICATION_OTI_MILLIS = 2000;

	/**
	 * Defines the time to wait in receive publication on cascaded client to get permit to proceed.
	 */
	// TODO JOT/JAN.. reduce to 2000, 200000 for testing purpose
	public static final int WAIT_FOR_PERMIT_IN_RECEIVE_PUBLICATION_MILLIS = 2000000;

	/** The default keep alive interval, 0 = not active. */
	public static final int DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS = 60;

	/** The default number of subsequent keep alive messages before the connection is closed. */
	public static final int DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE = 10;

	/** Default operation timeout used if ROOT_KEEP_ALIVE_OTI_MILLIS is not configured. */
	public static final int DEFAULT_KEEP_ALIVE_OTI_MILLIS = 2000;

	/** The default maximal connection pool size */
	public static final int DEFAULT_MAX_CONNECTION_POOL_SIZE = 100;

	// ** ATTENTION ** SIX Exchange C-Servers depend on minimum = 1 !!
	/** The default minimal connection pool size, 1 - means there is always one connection active */
	public static final int DEFAULT_MIN_CONNECTION_POOL_SIZE = 1;

	/** The default maximal file sessions */
	public static final int DEFAULT_MAX_FILE_SESSIONS = 10;

	/** The message compression */
	public static final boolean DEFAULT_COMPRESSION_FLAG = true;

	/** The write PID */
	public static final boolean DEFAULT_WRITE_PID_FLAG = false;

	/** Default max message size */
	public static final int DEFAULT_MAX_MESSAGE_SIZE = 60 << 10; // 64K

	/** Default service state. */
	public static final boolean DEFAULT_SERVICE_ENABLED = true;

	/*
	 * Various Constants *********
	 */
	/**
	 * Technical operation timeout. <br>
	 * It is the time a single WRITE/READ/CLOSE/OPEN can take. Must be reasonably sort.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 2000;

	/** Empty application error code. */
	public static final int EMPTY_APP_ERROR_CODE = -9999;

	/** The wait time in a loop waiting for a free connection. */
	public static final int WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS = 200;

	/** Maximum size of a message. Larger data is treated as large message */
	public static final int MAX_MESSAGE_SIZE = DEFAULT_MAX_MESSAGE_SIZE;

	/** Maximum port nummber */
	public static final int MAX_PORT_NR = 65535;

	/** Seconds to milliseconds calculation factor */
	public static final int SEC_TO_MILLISEC_FACTOR = 1000;

	/** flag to enable / disable command validation. */
	public static final boolean COMMAND_VALIDATION_ENABLED = true;

	/** File qualifier for Http requests. */
	public static final String HTTP_FILE_QUALIFIER = "/";

	/** HttpHeaders.Names.ACCEPT parameter used when http data is sent */
	public static final String HTTP_ACCEPT_PARAMS = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";

	/** Protocol literal. */
	public static final String HTTP = "http";

	/** Protocol literal. */
	public static final String TCP = "tcp";

	/** File qualifier for command line argument configuration file. */
	public static final String CLI_CONFIG_ARG = "-config";

	/**
	 * File containing the SC process PID. Created at startup and deleted on
	 * exit or error. Used to check if SC is running.
	 */
	public static final String PID_FILE_NAME = "sc.pid";

	/** File containing the SC dump. Created with the console command or WEB-GUI */
	public static final String DUMP_FILE_NAME = "scDump_";

	/** The dump file name format */
	public static final String DUMP_FILE_NAME_FORMAT = "yyyyMMddHHmmssSSS";

	/** The dump file extension */
	public static final String DUMP_FILE_EXTENSION = ".xml";

	/** The dump file encoding */
	public static final String DUMP_FILE_ENCODING = "ISO-8859-1";

	/** File containing the SC logs (zipped). Created with the console command or WEB-GUI */
	public static final String LOGS_FILE_NAME = "scLogs_";

	/** The dump file name format */
	public static final String LOGS_FILE_NAME_FORMAT = "yyyyMMddHHmmssSSS";

	/** The dump file extension */
	public static final String LOGS_FILE_EXTENSION = ".zip";

	/** The Constant IPV6_LOOPBACK_NIC. */
	public static final String IPV6_LOOPBACK_NIC = "0:0:0:0:0:0:0:1";

	/** FILE_LIST_DELIMITER, separates file names in file list SCFileService. */
	public static final String FILE_LIST_DELIMITER = "\\|";

	/** The Constant LINE_BREAK_SIGN. */
	public static final String LINE_BREAK_SIGN = "\n";

	/*
	 * console command constants *************************
	 */
	public static final String CC_CMD_DISABLE = "disable";
	public static final String CC_CMD_ENABLE = "enable";
	public static final String CC_CMD_STATE = "state";
	public static final String CC_CMD_SESSIONS = "sessions";
	public static final String CC_CMD_CLEAR_CACHE = "clearCache";
	public static final String CC_CMD_INSPECT_CACHE = "inspectCache";
	public static final String CC_CMD_KILL = "kill";
	public static final String CC_CMD_DUMP = "dump";
	public static final String EQUAL_SIGN = "=";
	public static final String AMPERSAND_SIGN = "&";
	public static final String QUESTION_MARK = "?";
	public static final String WILD_CARD_SIGN = "*";
	public static final String URL_ENCODING = "UTF-8"; // TODO SC default is "ISO-8859-1" does it match?
	public static final String SERVICE_NAME = "serviceName";
	public static final String CACHE_ID = "cacheId";

	/*
	 * Constants for syntax in sc.properies ************************************
	 */
	public static final String ROOT_WRITEPID = "root.writePID";
	public static final String ROOT_MAX_MESSAGE_SIZE = "root.maxMessageSize";
	public static final String ROOT_OPERATION_TIMEOUT_MULTIPLIER = "root.operationTimeoutMultiplier";
	public static final String ROOT_ECHO_INTERVAL_MULTIPLIER = "root.echoIntervalMultiplier";
	public static final String ROOT_COMMAND_VALIDATION_ENABLED = "root.commandValidationEnabled";
	public static final String ROOT_CONNECTION_TIMEOUT_MILLIS = "root.connectionTimeoutMillis";
	public static final String ROOT_SUBSCRIPTION_TIMEOUT_MILLIS = "root.subscriptionTimeoutMillis";
	public static final String ROOT_SERVER_ABORT_OTI_MILLIS = "root.serverAbortOTIMillis";
	public static final String ROOT_KEEP_ALIVE_OTI_MILLIS = "root.keepAliveOTIMillis";
	public static final String ROOT_PID_PATH = "root.pidPath";
	public static final String ROOT_DUMP_PATH = "root.dumpPath";

	public static final String PROPERTY_LISTENERS = "listeners";
	public static final String PROPERTY_SERVICE_NAMES = "serviceNames";
	public static final String PROPERTY_REMOTE_NODES = "remoteNodes";

	public static final String PROPERTY_QUALIFIER_CONNECTION_TYPE = ".connectionType";
	public static final String PROPERTY_QUALIFIER_USERNAME = ".username";
	public static final String PROPERTY_QUALIFIER_PASSWORD = ".password";
	public static final String PROPERTY_QUALIFIER_UPLOAD_SCRIPT_NAME = ".uploadScriptName";
	public static final String PROPERTY_QUALIFIER_REMOTE_NODE = ".remoteNode";
	public static final String PROPERTY_QUALIFIER_NOI = ".noDataIntervalSeconds";
	public static final String PROPERTY_QUALIFIER_INTERFACES = ".interfaces";
	public static final String PROPERTY_QUALIFIER_HOST = ".host";
	public static final String PROPERTY_QUALIFIER_PORT = ".port";
	public static final String PROPERTY_QUALIFIER_TYPE = ".type";
	public static final String PROPERTY_QUALIFIER_ENABLED = ".enabled";
	public static final String PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE = ".maxConnectionPoolSize";
	public static final String PROPERTY_QALIFIER_MAX_SESSIONS = ".maxSessions";
	public static final String PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS = ".keepAliveIntervalSeconds";
	public static final String PROPERTY_QUALIFIER_PATH = ".path";
	public static final String PROPERTY_QUALIFIER_LIST_SCRIPT = ".listScript";
	public static final String PROPERTY_QUALIFIER_UPLOAD_SCRIPT = ".uploadScript";

	// default cache values
	public static final boolean DEFAULT_CACHE_ENABLED = true;
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY = 10000;
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK = 100000;
	public static final int DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS = 300;
	// for internal use in class Cache
	public static final int DEFAULT_CACHE_RESPONSE_TIMEOUT_MILLIS = 60000;
	// cache property keys
	public static final String CACHE_ENABLED = "cache.enabled";
	public static final String CACHE_DISK_PATH = "cache.diskPath";
	public static final String CACHE_MAX_ELEMENTS_IN_MEMORY = "cache.maxElementsInMemory";
	public static final String CACHE_MAX_ELEMENTS_ON_DISK = "cache.maxElementsOnDisk";
	public static final String CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS = "cache.expirationCheckIntervalSeconds";

	// file server upload values
	public static final String DEFAULT_UPLOAD_FILE_PARAM_NAME = "filename";
	public static final String DEFAULT_UPLOAD_SERVICE_PARAM_NAME = "servicename";

	// web default values
	public static final boolean DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED = true;
	public static final int DEFAULT_WEB_SESSION_SCHEDULE_TIMEOUT_SECONDS = 180; // default session inactivity control schedule
																				// timeout (3 minutes)
	public static final int DEFAULT_WEB_SESSION_TIMEOUT_MINUTES = 30; // default session inactivty timeout 30 minutes
	// web property keys
	/** XSL transformation cache enebled = transformation will be done every cycle */
	public static final String WEB_XSL_TRANSFORMATION_CACHE_ENABLED = "web.xslTransformationCache.enabled";
	/** Prefix displayed in header and title to identify the SC instance */
	public static final String WEB_PAGE_HEADER_PREFIX = "web.pageHeaderPrefix";
	/** file service used for upload via GUI */
	public static final String WEB_SC_UPLOAD_SERVICE = "web.scUploadService";
	/** file service used for download via GUI */
	public static final String WEB_SC_DOWNLOAD_SERVICE = "web.scDownloadService";

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
	public static final String SCMP_FORMAT_OF_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String SCMP_FORMAT_OF_DATE_TIME_UTC = "yyyy-MM-dd'T'HH:mm:ss";
	public static final int MAX_HTTP_CONTENT_LENGTH = Integer.MAX_VALUE; // 2^31-1 => 2147483647, 2GB

}