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

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Constants.class);

	/**
	 * Instantiates a new constants.
	 */
	private Constants() {
		// instantiating not allowed
	}

	/*
	 * Numbers.
	 */
	/** The Constant NUMBER_10. */
	public static final int NUMBER_10 = 10;
	/** The Constant NUMBER_100. */
	public static final int NUMBER_100 = 100;
	/** The Constant NUMBER_1000. */
	public static final int NUMBER_1000 = 1000;
	/** The Constant NUMBER_3600. */
	private static final int NUMBER_3600 = 3600;

	/*
	 * Various Constants *********
	 */
	/**
	 * Technical operation timeout. <br />
	 * It is the time a single WRITE/READ/CLOSE/OPEN can take. Must be reasonably short.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 2000;
	/** Empty application error code. */
	public static final int EMPTY_APP_ERROR_CODE = -9999;
	/** The wait time in a loop waiting for a free connection. */
	public static final int WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS = 200;
	/** Maximum size of a message. Larger data must be broken into parts */
	public static final int MAX_MESSAGE_SIZE = 7340032; // 7 << 20 = 7MB;
	/** The default SC character set (encoding). */
	public static final String SC_CHARACTER_SET = "ISO-8859-1";
	/** Seconds to milliseconds calculation factor. */
	public static final int SEC_TO_MILLISEC_FACTOR = 1000;
	/** Seconds to milliseconds calculation factor. */
	public static final int SEC_TO_NANOSSEC_FACTOR = SEC_TO_MILLISEC_FACTOR * SEC_TO_MILLISEC_FACTOR;
	/** flag to enable / disable command validation. */
	public static final boolean COMMAND_VALIDATION_ENABLED = true;
	/** The Constant STATIC. */
	public static final String STATIC = "static";
	/** The Constant SLASH. */
	public static final String SLASH = "/";
	/** The Constant UNDERLINE. */
	public static final String UNDERLINE = "_";
	/** HttpHeaders.Names.ACCEPT parameter used when http data is sent. */
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
	/** The dump file name format. */
	public static final String DUMP_FILE_NAME_FORMAT = "yyyyMMddHHmmssSSS";
	/** The dump file extension. */
	public static final String DUMP_FILE_EXTENSION = ".xml";
	/** File containing the SC logs (zipped). Created with the console command or WEB-GUI */
	public static final String LOGS_FILE_NAME = "scLogs_";
	/** The SC logs file name format. */
	public static final String LOGS_FILE_NAME_FORMAT = "yyyyMMddHHmmssSSS";
	/** The SC logs file extension. */
	public static final String LOGS_FILE_EXTENSION = ".zip";
	/** FILE_LIST_DELIMITER, separates file names in file list SCFileService. */
	public static final String FILE_LIST_DELIMITER = "\\|";
	/** The Constant LINE_BREAK_SIGN. */
	public static final String LINE_BREAK_SIGN = "\n";
	/** The Constant BLANK_SIGN. */
	public static final String BLANK_SIGN = " ";

	/*
	 * Defaults ********
	 */
	/** Default value used if no DEFAULT_MAX_IO_THREADS is configured. */
	public static final int DEFAULT_MAX_IO_THREADS = 30;
	/** Default value used if no DEFAULT_MAX_ORDERED_IO_THREADS is configured. */
	public static final int DEFAULT_MAX_ORDERED_IO_THREADS = 30;
	/** Default value used if no ECHO_TIMEOUT_MULTIPLIER is configured. */
	public static final double DEFAULT_ECHO_INTERVAL_MULTIPLIER = 1.2;
	/** Default value used if no interval is passed in the API. */
	public static final int DEFAULT_ECHO_INTERVAL_SECONDS = 60;
	/** Default value if no DEFAULT_CHECK_REGISTRATION_INTERVAL_MULTIPLIER is configured. */
	public static final double DEFAULT_CHECK_REGISTRATION_INTERVAL_MULTIPLIER = 1.2;
	/** Default value if no OPERATION_TIMEOUT_MULTIPLIER is configured. */
	public static final double DEFAULT_OPERATION_TIMEOUT_MULTIPLIER = 0.8;
	/** Default value used if no timeout for operation is passed in the API. */
	public static final int DEFAULT_OPERATION_TIMEOUT_SECONDS = 60;
	/** Default timeout for creation of a connection to peer. */
	public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
	/**
	 * Maximal time between subsequent receive publication.
	 * After this time subscription is marked as dead.
	 * This is analogous to echoInterval in session services
	 */
	public static final int DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS = 10000;
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
	public static final int WAIT_FOR_PERMIT_IN_RECEIVE_PUBLICATION_MILLIS = 20000;
	/** The default keep alive interval, 0 = not active. */
	public static final int DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS = 60;
	/** The default check registration interval, 0 = not active. */
	public static final int DEFAULT_CHECK_REGISTRATION_INTERVAL_SECONDS = 0;
	/** The default number of subsequent keep alive messages before the connection is closed. */
	public static final int DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE = 10;
	/** Default operation timeout used if ROOT_KEEP_ALIVE_OTI_MILLIS is not configured. */
	public static final int DEFAULT_KEEP_ALIVE_OTI_MILLIS = 10000;
	/** Default operation timeout used if ROOT_KEEP_ALIVE_OTI_SECONDS is not configured. */
	public static final int DEFAULT_KEEP_ALIVE_OTI_SECONDS = DEFAULT_KEEP_ALIVE_OTI_MILLIS / SEC_TO_MILLISEC_FACTOR;
	/** The Constant DEFAULT_CHECK_REGISTRATION_OTI_SECONDS. */
	public static final int DEFAULT_CHECK_REGISTRATION_OTI_SECONDS = 10000;
	/** The default maximal connection pool size. */
	public static final int DEFAULT_MAX_CONNECTION_POOL_SIZE = 100;

	// ** ATTENTION ** SIX Exchange C-Servers depend on minimum = 1 !!
	/** The default minimal connection pool size, 1 - means there is always one connection active. */
	public static final int DEFAULT_MIN_CONNECTION_POOL_SIZE = 0;
	/** The default maximal file sessions. */
	public static final int DEFAULT_MAX_FILE_SESSIONS = 10;
	/** The message compression. */
	public static final boolean DEFAULT_COMPRESSION_FLAG = true;
	/** The write PID. */
	public static final boolean DEFAULT_WRITE_PID_FLAG = false;
	/** Default message part size. */
	public static final int DEFAULT_MESSAGE_PART_SIZE = 204800; // 200 << 10 = 200KB
	/** Default service state. */
	public static final boolean DEFAULT_SERVICE_ENABLED = true;
	/** Size of 64KB. */
	public static final int SIZE_64KB = 65536; // 1 << 16 = 64KB

	/*
	 * console command constants *************************
	 */
	/** The Constant CC_CMD_DISABLE. */
	public static final String CC_CMD_DISABLE = "disable";
	/** The Constant CC_CMD_ENABLE. */
	public static final String CC_CMD_ENABLE = "enable";
	/** The Constant CC_CMD_STATE. */
	public static final String CC_CMD_STATE = "state";
	/** The Constant CC_CMD_SC_VERSION. */
	public static final String CC_CMD_SC_VERSION = "scVersion";
	/** The Constant CC_CMD_SERVICE_CONF. */
	public static final String CC_CMD_SERVICE_CONF = "serviceConfiguration";
	/** The Constant CC_CMD_SESSIONS. */
	public static final String CC_CMD_SESSIONS = "sessions";
	/** The Constant CC_CMD_CLEAR_CACHE. */
	public static final String CC_CMD_CLEAR_CACHE = "clearCache";
	/** The Constant CC_CMD_INSPECT_CACHE. */
	public static final String CC_CMD_INSPECT_CACHE = "inspectCache";
	/** The Constant CC_CMD_KILL. */
	public static final String CC_CMD_KILL = "kill";
	/** The Constant CC_CMD_DUMP. */
	public static final String CC_CMD_DUMP = "dump";
	/** The Constant EQUAL_SIGN. */
	public static final String EQUAL_SIGN = "=";
	/** The Constant AMPERSAND_SIGN. */
	public static final String AMPERSAND_SIGN = "&";
	/** The Constant QUESTION_MARK. */
	public static final String QUESTION_MARK = "?";
	/** The Constant SERVICE_NAME. */
	public static final String SERVICE_NAME = "serviceName";
	/** The Constant CACHE_ID. */
	public static final String CACHE_ID = "cacheId";
	/** The Constant STATE_DISABLE. */
	public static final String STATE_DISABLED = "disabled";
	/** The Constant STATE_ENABLE. */
	public static final String STATE_ENABLED = "enabled";
	/** The Constant NOT_FOUND. */
	public static final String NOT_FOUND = "notfound";

	/*
	 * Constants for syntax in sc.properies ************************************
	 */
	/** The Constant ROOT_WRITEPID. */
	public static final String ROOT_WRITEPID = "root.writePID";
	/** The Constant ROOT_SERVER_TIMEOUT_MULTIPLIER. */
	public static final String ROOT_CHECK_REGISTRATION_INTERVAL_MULTIPLIER = "root.checkRegistrationIntervalMultiplier";
	/** The Constant ROOT_OPERATION_TIMEOUT_MULTIPLIER. */
	public static final String ROOT_OPERATION_TIMEOUT_MULTIPLIER = "root.operationTimeoutMultiplier";
	/** The Constant ROOT_ECHO_INTERVAL_MULTIPLIER. */
	public static final String ROOT_ECHO_INTERVAL_MULTIPLIER = "root.echoIntervalMultiplier";
	/** The Constant ROOT_COMMAND_VALIDATION_ENABLED. */
	public static final String ROOT_COMMAND_VALIDATION_ENABLED = "root.commandValidationEnabled";
	/** The Constant ROOT_MAX_IO_THREADS. */
	public static final String ROOT_MAX_IO_THREADS = "root.maxIOThreads";
	/** The Constant ROOT_TCP_KEEPALIVE_INITIATOR. */
	public static final String ROOT_TCP_KEEPALIVE_INITIATOR = "root.tcpKeepAliveInitiator";
	/** The Constant ROOT_TCP_KEEPALIVE_LISTENER. */
	public static final String ROOT_TCP_KEEPALIVE_LISTENER = "root.tcpKeepAliveListener";
	/** The Constant ROOT_CONNECTION_TIMEOUT_MILLIS. */
	public static final String ROOT_CONNECTION_TIMEOUT_MILLIS = "root.connectionTimeoutMillis";
	/** The Constant ROOT_SUBSCRIPTION_TIMEOUT_MILLIS. */
	public static final String ROOT_SUBSCRIPTION_TIMEOUT_MILLIS = "root.subscriptionTimeoutMillis";
	/** The Constant ROOT_SERVER_ABORT_OTI_MILLIS. */
	public static final String ROOT_SERVER_ABORT_OTI_MILLIS = "root.serverAbortOTIMillis";
	/** The Constant ROOT_KEEP_ALIVE_OTI_MILLIS. */
	public static final String ROOT_KEEP_ALIVE_OTI_MILLIS = "root.keepAliveOTIMillis";
	/** The Constant ROOT_PID_PATH. */
	public static final String ROOT_PID_PATH = "root.pidPath";
	/** The Constant ROOT_DUMP_PATH. */
	public static final String ROOT_DUMP_PATH = "root.dumpPath";
	/** The Constant PROPERTY_LISTENERS. */
	public static final String PROPERTY_LISTENERS = "listeners";
	/** The Constant PROPERTY_SERVICE_NAMES. */
	public static final String PROPERTY_SERVICE_NAMES = "serviceNames";
	/** The Constant PROPERTY_REMOTE_NODES. */
	public static final String PROPERTY_REMOTE_NODES = "remoteNodes";
	/** The Constant PROPERTY_QUALIFIER_CONNECTION_TYPE. */
	public static final String PROPERTY_QUALIFIER_CONNECTION_TYPE = ".connectionType";
	/** The Constant PROPERTY_QUALIFIER_USERNAME. */
	public static final String PROPERTY_QUALIFIER_USERNAME = ".username";
	/** The Constant PROPERTY_QUALIFIER_PASSWORD. */
	public static final String PROPERTY_QUALIFIER_PASSWORD = ".password";
	/** The Constant PROPERTY_QUALIFIER_UPLOAD_SCRIPT_NAME. */
	public static final String PROPERTY_QUALIFIER_UPLOAD_SCRIPT_NAME = ".uploadScriptName";
	/** The Constant PROPERTY_QUALIFIER_REMOTE_NODE. */
	public static final String PROPERTY_QUALIFIER_REMOTE_NODE = ".remoteNode";
	/** The Constant PROPERTY_QUALIFIER_NOI. */
	public static final String PROPERTY_QUALIFIER_NOI = ".noDataIntervalSeconds";
	/** The Constant PROPERTY_QUALIFIER_INTERFACES. */
	public static final String PROPERTY_QUALIFIER_INTERFACES = ".interfaces";
	/** The Constant PROPERTY_QUALIFIER_HOST. */
	public static final String PROPERTY_QUALIFIER_HOST = ".host";
	/** The Constant PROPERTY_QUALIFIER_PORT. */
	public static final String PROPERTY_QUALIFIER_PORT = ".port";
	/** The Constant PROPERTY_QUALIFIER_TYPE. */
	public static final String PROPERTY_QUALIFIER_TYPE = ".type";
	/** The Constant PROPERTY_QUALIFIER_ENABLED. */
	public static final String PROPERTY_QUALIFIER_ENABLED = ".enabled";
	/** The Constant PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE. */
	public static final String PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE = ".maxConnectionPoolSize";
	/** The Constant PROPERTY_QALIFIER_MAX_SESSIONS. */
	public static final String PROPERTY_QALIFIER_MAX_SESSIONS = ".maxSessions";
	/** The Constant PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS. */
	public static final String PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS = ".keepAliveIntervalSeconds";
	/** The Constant PROPERTY_QUALIFIER_PATH. */
	public static final String PROPERTY_QUALIFIER_PATH = ".path";
	/** The Constant PROPERTY_QUALIFIER_LIST_SCRIPT. */
	public static final String PROPERTY_QUALIFIER_LIST_SCRIPT = ".listScript";
	/** The Constant PROPERTY_QUALIFIER_UPLOAD_SCRIPT. */
	public static final String PROPERTY_QUALIFIER_UPLOAD_SCRIPT = ".uploadScript";

	// default cache values
	/** The Constant DEFAULT_CACHE_ENABLED. */
	public static final boolean DEFAULT_CACHE_ENABLED = true;
	/** The Constant DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY. */
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_IN_MEMORY = 100000;
	/** The Constant DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK. */
	public static final int DEFAULT_CACHE_MAX_ELEMENTS_ON_DISK = 1000000;
	/** The Constant DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS. */
	public static final int DEFAULT_CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS = 30;
	// for internal use in class Cache
	/** The Constant CACHE_ENABLED. cache property keys */
	public static final String CACHE_ENABLED = "cache.enabled";
	/** The Constant CACHE_DISK_PATH. */
	public static final String CACHE_DISK_PATH = "cache.diskPath";
	/** The Constant CACHE_MAX_ELEMENTS_IN_MEMORY. */
	public static final String CACHE_MAX_ELEMENTS_IN_MEMORY = "cache.maxElementsInMemory";
	/** The Constant CACHE_MAX_ELEMENTS_ON_DISK. */
	public static final String CACHE_MAX_ELEMENTS_ON_DISK = "cache.maxElementsOnDisk";
	/** The Constant CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS. */
	public static final String CACHE_EXPIRATION_CHECK_INTERVAL_SECONDS = "cache.expirationCheckIntervalSeconds";
	/** The Constant UPLOAD_FILE_PARAM_NAME. File server upload values */
	public static final String UPLOAD_FILE_PARAM_NAME = "filename";
	/** The Constant UPLOAD_SERVICE_PARAM_NAME. */
	public static final String UPLOAD_SERVICE_PARAM_NAME = "servicename";
	/** The Constant DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED. web default values */
	public static final boolean DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED = true;
	/** The Constant DEFAULT_WEB_COLOR_SCHEME. */
	public static final String DEFAULT_WEB_COLOR_SCHEME = "classic";
	/** The Constant DEFAULT_WEB_PAGE_SIZE, the default visible element per page */
	public static final int DEFAULT_WEB_PAGE_SIZE = 25;
	/** The Constant DEFAULT_WEB_SITE_SIZE, the default visible site size in paging area */
	public static final int DEFAULT_WEB_SITE_SIZE = 20;
	/**
	 * The Constant DEFAULT_WEB_SESSION_SCHEDULE_TIMEOUT_SECONDS.
	 * default session inactivity control schedule timeout (3 minutes)
	 */
	public static final int DEFAULT_WEB_SESSION_SCHEDULE_TIMEOUT_SECONDS = 180;
	/**
	 * The Constant DEFAULT_WEB_SESSION_TIMEOUT_MINUTES.
	 * default session inactivity timeout 30 minutes web property keys
	 */
	public static final int DEFAULT_WEB_SESSION_TIMEOUT_MINUTES = 30;
	/** XSL transformation cache enabled = transformation will be done every cycle. */
	public static final String WEB_XSL_TRANSFORMATION_CACHE_ENABLED = "web.xslTransformationCache.enabled";
	/** Prefix displayed in header and title to identify the SC instance. */
	public static final String WEB_PAGE_HEADER_PREFIX = "web.pageHeaderPrefix";
	/** file service used for upload via GUI. */
	public static final String WEB_SC_UPLOAD_SERVICE = "web.scUploadService";
	/** file service used for download via GUI. */
	public static final String WEB_SC_DOWNLOAD_SERVICE = "web.scDownloadService";
	/** allow termination button in Web GUI. */
	public static final String WEB_SC_TERMINATE_ALLOWED = "web.scTerminateAllowed";
	/** color schema for Web GUI. */
	public static final String WEB_COLOR_SCHEMA = "web.colorSchema";

	/*
	 * SCMP protocol constants ***********************
	 */
	/** The Constant SCMP_CR. */
	public static final byte SCMP_CR = 0x0D; // Carriage return character.
	/** The Constant SCMP_LF. */
	public static final byte SCMP_LF = 0x0A; // Line feed character
	/** The Constant SCMP_EQUAL. */
	public static final byte SCMP_EQUAL = 0x3D; // Equal character
	/** The Constant SCMP_ZERO. */
	public static final byte SCMP_ZERO = 0x30; // Zero character
	/** The Constant SCMP_HEADLINE_SIZE. */
	public static final int SCMP_HEADLINE_SIZE = 22;
	/** The Constant SCMP_HEADLINE_SIZE_WITHOUT_VERSION. */
	public static final int SCMP_HEADLINE_SIZE_WITHOUT_VERSION = 18;
	/** The Constant SCMP_MSG_SIZE_START. */
	public static final int SCMP_MSG_SIZE_START = 4;
	/** The Constant SCMP_MSG_SIZE_END. */
	public static final int SCMP_MSG_SIZE_END = 10;
	/** The Constant SCMP_HEADER_SIZE_START. */
	public static final int SCMP_HEADER_SIZE_START = 12;
	/** The Constant SCMP_HEADER_SIZE_END. */
	public static final int SCMP_HEADER_SIZE_END = 16;
	/** The Constant SCMP_VERSION_LENGTH_IN_HEADLINE. */
	public static final int SCMP_VERSION_LENGTH_IN_HEADLINE = 3;
	/** The Constant SCMP_FORMAT_OF_MSG_SIZE. */
	public static final String SCMP_FORMAT_OF_MSG_SIZE = " 0000000";
	/** The Constant SCMP_FORMAT_OF_HEADER_SIZE. */
	public static final String SCMP_FORMAT_OF_HEADER_SIZE = " 00000";
	/** The Constant SCMP_FORMAT_OF_DATE_TIME. */
	public static final String SCMP_FORMAT_OF_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	/** The Constant SCMP_FORMAT_OF_DATE_TIME_UTC. */
	public static final String SCMP_FORMAT_OF_DATE_TIME_UTC = "yyyy-MM-dd'T'HH:mm:ss";
	/** The Constant MAX_HTTP_CONTENT_LENGTH. */
	public static final int MAX_HTTP_CONTENT_LENGTH = Integer.MAX_VALUE; // 2^31-1 => 2147483647, 2GB
	/** The Constant DOT_HEX. */
	public static final byte DOT_HEX = 0x2E;
	/** The Constant PERCENT_SIGN. */
	public static final byte PERCENT_SIGN = 0x25;

	/*
	 * SCMP validation constants ***********************
	 */
	/** The Constant MAX_LENGTH_SERVICENAME. */
	public static final int MAX_LENGTH_SERVICENAME = 32;
	/** The Constant MAX_STRING_LENGTH_256. */
	public static final int MAX_STRING_LENGTH_256 = 256;
	/** The Constant MIN_PORT_VALUE. */
	public static final int MIN_PORT_VALUE = 1;
	/** The Constant MAX_PORT_VALUE. */
	public static final int MAX_PORT_VALUE = 65535; // 0xFFFF
	/** The Constant MIN_OTI_VALUE. */
	public static final int MIN_OTI_VALUE_CLN = 1000;
	/** The Constant MIN_OTI_VALUE. */
	public static final int MIN_OTI_VALUE_CSC = 300;
	/** The Constant MIN_OTI_VALUE_SRV. */
	public static final int MIN_OTI_VALUE_SRV = NUMBER_100;
	/** The Constant MAX_OTI_VALUE. */
	public static final int MAX_OTI_VALUE = 3600000;
	/** The Constant MIN_NOI_VALUE. */
	public static final int MIN_NOI_VALUE = NUMBER_10;
	/** The Constant MAX_NOI_VALUE. */
	public static final int MAX_NOI_VALUE = NUMBER_3600;
	/** The Constant MIN_ECI_VALUE. */
	public static final int MIN_ECI_VALUE = NUMBER_10;
	/** The Constant MAX_ECI_VALUE. */
	public static final int MAX_ECI_VALUE = NUMBER_3600;
	/** The Constant MIN_KPI_VALUE. */
	public static final int MIN_KPI_VALUE = 0;
	/** The Constant MAX_KPI_VALUE. */
	public static final int MAX_KPI_VALUE = NUMBER_3600;
	/** The Constant MIN_CRI_VALUE. */
	public static final int MIN_CRI_VALUE = 0;
	/** The Constant MAX_CRI_VALUE. */
	public static final int MAX_CRI_VALUE = NUMBER_3600;
	/** The Constant MAX_ECHO_TIMEOUT_VALUE. */
	public static final int MAX_ECHO_TIMEOUT_VALUE = NUMBER_3600;
	/** The Constant MAX_RECEIVE_PUBLICAION_TIMEOUT_VALUE. */
	public static final int MAX_RECEIVE_PUBLICAION_TIMEOUT_VALUE = NUMBER_3600;
	/** The Constant MAX_KP_TIMEOUT_VALUE. */
	public static final int MAX_KP_TIMEOUT_VALUE = NUMBER_3600;
	/** The Constant MAX_CRG_TIMEOUT_VALUE. */
	public static final int MAX_CRG_TIMEOUT_VALUE = NUMBER_3600;
}
