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

	/** The Constant LARGE_MESSAGE_LIMIT. */
	private static final int DEFAULT_LARGE_MESSAGE_LIMIT = 60 << 10; // 64K
	/** The Constant LARGE_MESSAGE_LIMIT. */
	public static int LARGE_MESSAGE_LIMIT = DEFAULT_LARGE_MESSAGE_LIMIT;
	/** File qualifier for command line argument configuration file. */
	public static final String CLI_CONFIG_ARG = "-sc.configuration";
	/** flag to enable / disable validation. */
	public static boolean COMMAND_VALIDATION_ENABLED = true;
	/** flag to enable / disable cache. */
	public static boolean MESSAGE_CACHE_ENABLED = true;
	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";
	/** The DEFAULT_NR_OF_THREADS FOR SERVER. */
	public static final int DEFAULT_NR_OF_THREADS_SERVER = 10000;
	/** The DEFAULT_NR_OF_THREADS FOR CLIENT. */
	public static final int DEFAULT_NR_OF_THREADS_CLIENT = 5000;
	/**
	 * DEFAULT_ECHO_TIMEOUT_MULTIPLIER: Default value if no ECHO_TIMEOUT_MULTIPLIER will be set by configuration.
	 */
	private static final double DEFAULT_ECHO_INTERVAL_MULTIPLIER = 1.2;
	/**
	 * ECHO_TIMEOUT_MULTIPLIER: The multiplier is needed to calculate the echo timeout of a session. E.g. SC needs to
	 * adapt echo timeout interval from client to get right interval for echo messages.
	 */
	public static double ECHO_INTERVAL_MULTIPLIER = DEFAULT_ECHO_INTERVAL_MULTIPLIER;
	/**
	 * DEFAULT_OPERATION_TIMEOUT_MULTIPLIER: Default value if no OPERATION_TIMEOUT_MULTIPLIER will be set by
	 * configuration.
	 */
	private static final double DEFAULT_OPERATION_TIMEOUT_MULTIPLIER = 0.8;
	/**
	 * ECHO_TIMEOUT_MULTIPLIER: The multiplier is needed to calculate the echo timeout of a session. E.g. SC needs to
	 * adapt echo timeout interval from client to get right interval for echo messages.
	 */
	public static final double OPERATION_TIMEOUT_MULTIPLIER = DEFAULT_OPERATION_TIMEOUT_MULTIPLIER;
	/**
	 * DEFAULT_OPERATION_TIMEOUT: This operation timeout is used when communicating with SC to set timeout on a higher
	 * level of architecture. Time unit is seconds. Used if no timeout for operation is handed over by the user.
	 */
	public static final int DEFAULT_OPERATION_TIMEOUT_SECONDS = 60;
	/**
	 * OPERATION_TIMEOUT_MILLIS_SHORT: This operation timeout is used in urgent situations when communication should
	 * work very fast. Often used in emergency cases and in situation where reply of operation is irrelevant.
	 */
	public static final int OPERATION_TIMEOUT_MILLIS_SHORT = 200;
	/**
	 * DEFAULT_KEEP_ALIVE_TIMEOUT: Default value if no KEEP_ALIVE_TIMEOUT will be set by configuration.
	 */
	private static final int DEFAULT_KEEP_ALIVE_TIMEOUT = 2000;
	/**
	 * KEEP_ALIVE_TIMEOUT: This timeout is used to observe the reply of a keep alive message. If reply can not be
	 * received within this time, connection is will be cleaned up.
	 */
	public static int KEEP_ALIVE_TIMEOUT = DEFAULT_KEEP_ALIVE_TIMEOUT;
	/**
	 * TECH_LEVEL_OPERATION_TIMEOUT_MILLIS: Is used to detect a technical operation timeout. It is the time a single
	 * WRITE/READ/CLOSE/OPEN can have. Should be low/short.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 2000;
	/** The Constant DEFAULT_CONNECT_TIMEOUT_MILLIS. */
	private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 5000;
	/** ONNECT_TIMEOUT_MILLIS: Timeout prevents stocking in technical connect process. */
	public static int CONNECT_TIMEOUT_MILLIS = DEFAULT_CONNECT_TIMEOUT_MILLIS;
	/** The DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS, time after a subscription is marked as dead. */
	private static final int DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS = 300000;
	/** The SUBSCRIPTION_TIMEOUT_MILLIS, time after a subscription is marked as dead. */
	public static int SUBSCRIPTION_TIMEOUT_MILLIS = DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS;
	/** The WAIT_FOR_CONNECTION_INTERVAL_MILLIS. */
	public static final int WAIT_FOR_CONNECTION_INTERVAL_MILLIS = 200;
	/** The Constant SEC_TO_MILISEC_FACTOR. */
	public static final int SEC_TO_MILLISEC_FACTOR = 1000;
	/** The REGEX. */
	public static final String COMMA_OR_SEMICOLON = ",|;";
	/** The Constant ROOT_TEST_QUALIFIER. */
	public static final String ROOT_WRITEPID_QUALIFIER = "root.writePID";
	/** The Constant ROOT_OPERATION_TIMEOUT_QUALIFIER. */
	public static final String ROOT_OPERATION_TIMEOUT_QUALIFIER = "root.operationTimeoutMultiplier";
	/** The Constant ROOT_ECHO_TIMEOUT_QUALIFIER. */
	public static final String ROOT_ECHO_INTERVAL_QUALIFIER = "root.echoIntervalMultiplier";
	/** The Constant ROOT_COMMAND_VALIDATION_ENABLED. */
	public static final String ROOT_COMMAND_VALIDATION_ENABLED = "root.commandValidationEnabled";
	/** The Constant ROOT_MESSAGE_CACHE_ENABLED. */
	public static final String ROOT_MESSAGE_CACHE_ENABLED = "root.messageCacheEnabled";
	/** The Constant ROOT_CONNECTION_TIMEOUT_QUALIFIER. */
	public static final String ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER = "root.largeMessageLimit";
	/** The Constant ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER. */
	public static final String ROOT_CONNECTION_TIMEOUT_QUALIFIER = "root.connectionTimeoutMillis";
	/** The Constant ROOT_SUBSCRIPTION_TIMEOUT_QUALIFIER. */
	public static final String ROOT_SUBSCRIPTION_TIMEOUT_QUALIFIER = "root.subscriptionTimeout";
	/** The Constant ROOT_KEEP_ALIVE_TIMEOUT_QUALIFIER. */
	public static final String ROOT_KEEP_ALIVE_TIMEOUT_QUALIFIER = "root.keepAliveTimeout";
	/** The connection type. */
	public static final String CONNECTION_TYPE_QUALIFIER = ".connectionType";
	/** The USERID. */
	public static final String CONNECTION_USERNAME = ".username";
	/** The PASSWORD. */
	public static final String CONNECTION_PASSWORD = ".password";

	/** The Constant REMOTE_URI. */
	public static final String REMOTE_URI = ".remoteURI";
	/** The HOST. */
	public static final String HOST_QUALIFIER = ".host";
	/** The PORT. */
	public static final String PORT_QUALIFIER = ".port";
	/** The connection names. */
	public static final String TYPE_QUALIFIER = ".type";
	/** The Constant ENABLE_QUALIFIER. */
	public static final String ENABLE_QUALIFIER = ".enabled";
	/** The Constant CONNECTIONS. */
	public static final String CONNECTIONS = "connections";
	/** The server names. */
	public static final String SERVER_LISTENER = "serverListener";
	/** The services Names. */
	public static final String SERVICE_NAMES = "serviceNames";
	/** The Constant MAX_CONNECTION_POOL_SIZE. */
	public static final String MAX_CONNECTION_POOL_SIZE = "maxConnectionPoolSize";
	/** The Constant KEEP_ALIVE_INTERVAL. */
	public static final String KEEP_ALIVE_INTERVAL = "keepAliveInterval";

	/** The ACCEPT_PARAMS. */
	public static final String ACCEPT_PARAMS = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";

	/** Protocol literal. */
	public static final String HTTP = "http";
	/** Protocol literal. */
	public static final String TCP = "tcp";

	/** The Constant NETTY_TCP. */
	public static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	public static final String NETTY_HTTP = "netty.http";
	/** The Constant NETTY_WEB. */
	public static final String NETTY_WEB = "netty.web";
	/** The Constant NETTY_TCP_PROXY. */
	public static final String NETTY_TCP_PROXY = "netty.tcp.proxy";
	/** The DEFAULT_CLIENT_CON. */
	public static final String DEFAULT_CLIENT_CON = NETTY_HTTP;
	/** The DEFAULT_SERVER_CON. */
	public static final String DEFAULT_SERVER_CON = NETTY_TCP;

	/** The DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE, after keep alive occurs 10 times connection gets disconnected. */
	public static final int DEFAULT_NR_OF_KEEP_ALIVES_TO_CLOSE = 10;
	/** The DEFAULT_MAX_CONNECTIONS, 100 connections in the pool. */
	public static final int DEFAULT_MAX_CONNECTIONS = 100;
	/** The DEFAULT_KEEP_ALIVE_INTERVAL, 0 = not active. */
	public static final int DEFAULT_KEEP_ALIVE_INTERVAL = 0;

	/** Carriage return character. */
	public static final byte CR = 0x0D;
	/** Line feed character. */
	public static final byte LF = 0x0A;
	/** The Constant FIX_HEADLINE_SIZE. */
	public static final int FIX_HEADLINE_SIZE = 22;
	/** The Constant FIX_HEADLINE_SIZE. */
	public static final int FIX_HEADLINE_SIZE_WITHOUT_VERSION = 18;
	/** The Constant FORMAT_OF_MSG_SIZE. */
	public static final String FORMAT_OF_MSG_SIZE = " 0000000";
	/** The Constant FORMAT_OF_HEADER_SIZE. */
	public static final String FORMAT_OF_HEADER_SIZE = " 00000";
	/** The Constant FIX_MSG_SIZE_START. */
	public static final int FIX_MSG_SIZE_START = 4;
	/** The Constant FIX_MSG_SIZE_END. */
	public static final int FIX_MSG_SIZE_END = 10;
	/** The Constant FIX_HEADER_SIZE_START. */
	public static final int FIX_HEADER_SIZE_START = 12;
	/** The Constant FIX_HEADER_SIZE_END. */
	public static final int FIX_HEADER_SIZE_END = 16;
	/** The Constant MAX_HTTP_CONTENT_LENGTH. */
	public static final int MAX_HTTP_CONTENT_LENGTH = Integer.MAX_VALUE; // 2^31-1 => 2147483647, 2GB
	/** The Constant FIX_VERSION_LENGTH_IN_HEADLINE. */
	public static final int FIX_VERSION_LENGTH_IN_HEADLINE = 3;

	/** The Constant DISABLE. */
	public static final String DISABLE = "disable";
	/** The Constant ENABLE. */
	public static final String ENABLE = "enable";
	/** The Constant STATE. */
	public static final String STATE = "state";
	/** The Constant SESSIONS. */
	public static final String SESSIONS = "sessions";
	/** The Constant SHUTDOWN. */
	public static final String KILL = "kill";
	/** The Constant EQUAL_SIGN. */
	public static final String EQUAL_SIGN = "=";

	/**
	 * @param flag
	 */
	public static void setCommandValidation(boolean flag) {
		Constants.COMMAND_VALIDATION_ENABLED = flag;
	}

	/**
	 * @param flag
	 */
	public static void setMessageCache(boolean flag) {
		Constants.MESSAGE_CACHE_ENABLED = flag;
	}

	/**
	 * Sets the large message limit.
	 * 
	 * @param largeMessageLimit
	 *            the new large message limit
	 */
	public static void setLargeMessageLimit(int largeMessageLimit) {
		if (Constants.LARGE_MESSAGE_LIMIT != Constants.DEFAULT_LARGE_MESSAGE_LIMIT) {
			// setting LARGE_MESSAGE_LIMIT only allowed one time
			logger.error("setLargeMessageLimit called two times - not allowed.");
			return;
		}
		Constants.LARGE_MESSAGE_LIMIT = largeMessageLimit;
	}

	/**
	 * Sets the echo timeout multiplier.
	 * 
	 * @param echoTimeoutMultiplier
	 *            the new echo timeout multiplier
	 */
	public static void setEchoIntervalMultiplier(double echoInteralMultiplier) {
		if (Constants.ECHO_INTERVAL_MULTIPLIER != Constants.DEFAULT_ECHO_INTERVAL_MULTIPLIER) {
			// setting ECHO_INTERVAL_MULTIPLIER only allowed one time
			logger.error("setEchoIntervalMultiplier called two times - not allowed.");
			return;
		}
		Constants.ECHO_INTERVAL_MULTIPLIER = echoInteralMultiplier;
	}

	/**
	 * Sets the connection timeout.
	 * 
	 * @param connectionTimeoutMillis
	 *            the new connection timeout
	 */
	public static void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
		if (Constants.CONNECT_TIMEOUT_MILLIS != Constants.DEFAULT_CONNECT_TIMEOUT_MILLIS) {
			// setting CONNECT_TIMEOUT_MILLIS only allowed one time
			logger.error("setConnectionTimeoutMillis called two times - not allowed.");
			return;
		}
		Constants.CONNECT_TIMEOUT_MILLIS = connectionTimeoutMillis;
	}

	/**
	 * Sets the subscription timeout.
	 * 
	 * @param subscriptionTimeout
	 *            the new subscription timeout
	 */
	public static void setSubscriptionTimeout(int subscriptionTimeout) {
		if (Constants.SUBSCRIPTION_TIMEOUT_MILLIS != Constants.DEFAULT_SUBSCRIPTION_TIMEOUT_MILLIS) {
			// setting SUBSCRIPTION_TIMEOUT_MILLIS only allowed one time
			logger.error("setSubscriptionTimeout called two times - not allowed.");
			return;
		}
		Constants.SUBSCRIPTION_TIMEOUT_MILLIS = subscriptionTimeout;
	}

	/**
	 * Sets the keep alive timeout.
	 * 
	 * @param keepAliveTimeout
	 *            the new keep alive timeout
	 */
	public static void setKeepAliveTimeout(int keepAliveTimeout) {
		if (Constants.KEEP_ALIVE_TIMEOUT != Constants.DEFAULT_KEEP_ALIVE_TIMEOUT) {
			// setting KEEP_ALIVE_TIMEOUT only allowed one time
			logger.error("setKeepAliveTimeout called two times - not allowed.");
			return;
		}
		Constants.KEEP_ALIVE_TIMEOUT = keepAliveTimeout;
	}
}