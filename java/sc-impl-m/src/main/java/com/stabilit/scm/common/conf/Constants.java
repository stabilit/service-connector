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

import com.stabilit.scm.common.listener.LoggerPoint;

/**
 * The Interface IConstants. SC constants.
 * 
 * @author JTraber
 */
public final class Constants {

	private Constants() {
		// instantiating not allowed
	}

	/** Protocol literal. */
	public static final String HTTP = "http";
	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";

	/** The DEFAULT_NR_OF_THREADS. */
	public static final int DEFAULT_NR_OF_THREADS = 16;
	/** The MAX KEEP ALIVE TIME OF THREADS. */
	public static final int MAX_KEEP_ALIVE_OF_THREADS = 10;

	/**
	 * OPERATION_TIMEOUT_MILLIS: Pay attention variable might be set different when configuration gets loaded. Setting is
	 * only allowed one time. Needed on lowest level of communication. Used to detect operation timeout, can be ignored
	 * if no request is outstanding. If a request is outstanding the connection is busy and also blocked. Hand timeout
	 * at least up to requester callback to give connection free. OPERATION_TIMEOUT_MILLIS should be lower than
	 * SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS so that most of operation timeout can be detected on lower level.<br>
	 **/
	private static int OPERATION_TIMEOUT_MILLIS = Constants.OPERATION_TIMEOUT_MILLIS_DEFAULT;
	/**
	 * SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS: Is used to detect operation timeout on service level. Actually it should
	 * never happen. Operation timeout should be detected in lower level by idle timeout. Therefore a difference is
	 * summed up.
	 **/
	private static int SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS = Constants.OPERATION_TIMEOUT_MILLIS
			+ Constants.OPERATION_TIMEOUT_DIFFERENCE;
	/** OPERATION_TIMEOUT_MILLIS_DEFAULT: Default value for OPERATION_TIMEOUT_MILLIS **/
	private static final int OPERATION_TIMEOUT_MILLIS_DEFAULT = 60000;
	/** OPERATION_TIMEOUT_DIFFERENCE: Difference between SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS and IDLE_TIMEOUT_MILLIS. */
	private static final int OPERATION_TIMEOUT_DIFFERENCE = 2000;
	/**
	 * TECH_LEVEL_OPERATION_TIMEOUT_MILLIS: Is used to detect a technical operation timeout. It is the time a single
	 * WRITE/READ/CLOSE/OPEN can have. Should be low/short.
	 */
	public static final int TECH_LEVEL_OPERATION_TIMEOUT_MILLIS = 10000;
	/** ONNECT_TIMEOUT_MILLIS: Timeout prevents stocking in technical connect process. */
	public static final int CONNECT_TIMEOUT_MILLIS = 500;

	/** The Constant SEC_TO_MILISEC_FACTOR. */
	public static final int SEC_TO_MILISEC_FACTOR = 1000;

	/** The REGEX. */
	public static final String COMMA_OR_SEMICOLON = ",|;";
	/** The Constant ROOT_LOGGER_QUALIFIER. */
	public static final String ROOT_LOGGER_QUALIFIER = "root.logger";
	/** The Constant ROOT_OPERATION_TIMEOUT_QUALIFIER. */
	public static final String ROOT_OPERATION_TIMEOUT_QUALIFIER = "root.operationTimeoutMillis";
	/** The CON. */
	public static final String CONNECTION_TYPE_QUALIFIER = ".connectionType";
	/** The HOST. */
	public static final String HOST_QUALIFIER = ".host";
	/** The PORT. */
	public static final String PORT_QUALIFIER = ".port";
	/** The THREAD. */
	public static final String THREAD_POOL_SIZE_QUALIFIER = ".threadPoolSize";
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

	/** The logging directory. */
	public static final String LOG_DIR = "log/";
	/** The file name of connection log file. */
	public static final String CONNECTION_LOG_FILE_NAME = "con.log";
	/** The file name of exception log file. */
	public static final String EXCEPTION_LOG_FILE_NAME = "exc.log";
	/** The file name of general log file. */
	public static final String GENERAL_LOG_FILE_NAME = "gen.log";
	/** The file name of performance log file. */
	public static final String PERFORMANCE_LOG_FILE_NAME = "prf.log";
	/** The file name of warning log file. */
	public static final String TOP_LOG_FILE_NAME = "top.log";
	/** The Constant SESSION_LOG_FILE_NAME. */
	public static final String SESSION_LOG_FILE_NAME = "ses.log";

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

	/**
	 * @return the serviceLevelOperationTimeoutMillis
	 */
	public static int getServiceLevelOperationTimeoutMillis() {
		return Constants.SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS;
	}

	public static void setOperationTimeoutMillis(int operationTimeoutMillis) {
		if (Constants.OPERATION_TIMEOUT_MILLIS != Constants.OPERATION_TIMEOUT_MILLIS_DEFAULT) {
			// setting OPERATION_TIMEOUT_MILLIS only allowed one time
			LoggerPoint.getInstance().fireWarn(Constants.class, "setOperationTimeoutMillis called two times - not allowed.");
			return;
		}
		Constants.OPERATION_TIMEOUT_MILLIS = operationTimeoutMillis;
		// OPERATION_TIMEOUT_MILLIS needs to be lower than SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS
		Constants.SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS = operationTimeoutMillis + Constants.OPERATION_TIMEOUT_DIFFERENCE;
	}

	public static int getOperationTimeoutMillis() {
		return Constants.OPERATION_TIMEOUT_MILLIS;
		
	}
}
