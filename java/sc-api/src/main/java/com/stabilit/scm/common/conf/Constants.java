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
package com.stabilit.scm.common.conf;

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
	public static final int DEFAULT_LARGE_MESSAGE_LIMIT = 60 << 10; // 64Kb
	/** The Constant LARGE_MESSAGE_LIMIT. */
	public static int LARGE_MESSAGE_LIMIT = DEFAULT_LARGE_MESSAGE_LIMIT;
	/** The Constant DEFAULT_PROPERTY_FILE_NAME. */
	public static final String DEFAULT_PROPERTY_FILE_NAME = "sc.properties";
	/** Protocol literal. */
	public static final String HTTP = "http";
	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";

	/** The DEFAULT_NR_OF_THREADS FOR SERVER. */
	public static final int DEFAULT_NR_OF_THREADS_SERVER = 10000;
	/** The DEFAULT_NR_OF_THREADS FOR CLIENT. */
	public static final int DEFAULT_NR_OF_THREADS_CLIENT = 5000;

	
	
	
	public static final double DEFAULT_OPERATION_TIMEOUT_MULTIPLIER = 0.8;
	/**
	 * DEFAULT_OPERATION_TIMEOUT: This operation timeout is used when communicating with SC to set timeout on a higher
	 * level of architecture. Time unit is seconds.
	 */
	public static final int DEFAULT_OPERATION_TIMEOUT_SECONDS = 60;
	/**
	 * OPERATION_TIMEOUT_MILLIS_SHORT: This operation timeout is used in urgent situations when communication should
	 * work very fast. Often used in emergency cases and in situation where reply of operation is irrelevant.
	 */
	public static final int OPERATION_TIMEOUT_MILLIS_SHORT = 200;
	/**
	 * TECH_LEVEL_OPERATION_TIMEOUT_MILLIS: Is used to detect a technical operation timeout. It is the time a single
	 * WRITE/READ/CLOSE/OPEN can have. Should be low/short.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 2000;
	/** ONNECT_TIMEOUT_MILLIS: Timeout prevents stocking in technical connect process. */
	public static final int CONNECT_TIMEOUT_MILLIS = 1000;

	/** The Constant SEC_TO_MILISEC_FACTOR. */
	public static final int SEC_TO_MILISEC_FACTOR = 1000;

	/** The REGEX. */
	public static final String COMMA_OR_SEMICOLON = ",|;";
	/** The Constant ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER. */
	public static final String ROOT_LARGE_MESSAGE_LIMIT_QUALIFIER = "root.largeMessageLimit";
	/** The Constant ROOT_OPERATION_TIMEOUT_QUALIFIER. */
	public static final String ROOT_OPERATION_TIMEOUT_QUALIFIER = "root.operationTimeoutMultiplier";
	/** The CON. */
	public static final String CONNECTION_TYPE_QUALIFIER = ".connectionType";
	/** The HOST. */
	public static final String HOST_QUALIFIER = ".host";
	/** The PORT. */
	public static final String PORT_QUALIFIER = ".port";
	/** The connection names. */
	public static final String TYPE_QUALIFIER = ".type";
	/** The Constant ENABLE_QUALIFIER. */
	public static final String ENABLE_QUALIFIER = ".enable";
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

	/** The DEFAULT_CLIENT_CON. */
	public static final String DEFAULT_CLIENT_CON = "netty.http";
	/** The DEFAULT_SERVER_CON. */
	public static final String DEFAULT_SERVER_CON = "netty.tcp";

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
}
