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

/**
 * The Interface IConstants. SC constants.
 * 
 * @author JTraber
 */
public interface IConstants {

	/** Protocol literal. */
	public static final String HTTP = "http";
	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";

	/** The DEFAULT_NR_OF_THREADS. */
	public static final int DEFAULT_NR_OF_THREADS = 10;
	/** The MAX KEEP ALIVE TIME OF THREADS. */
	public static final int MAX_KEEP_ALIVE_OF_THREADS = 10;
	/** The read timeout in seconds. */
	public static final int READ_TIMEOUT = 200;
	/** The write timeout in seconds. */
	public static final int WRITE_TIMEOUT = 200;
	/** The Constant SEC_TO_MILISEC_FACTOR. */
	public static final int SEC_TO_MILISEC_FACTOR = 1000;

	/** The REGEX. */
	public static final String COMMA_OR_SEMICOLON = ",|;";
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
	public static final String CONNECTIONS = "connections";
	/** The server names. */
	public static final String SERVER_LISTENER = "serverListener";
	/** The services Names. */
	public static final String SERVICE_NAMES = "serviceNames";
	public static final String MAX_CONNECTION_POOL_SIZE = "maxConnectionPoolSize";
	public static final String KEEP_ALIVE_INTERVAL = "keepAliveInterval";
	public static final String KEEP_ALIVE_TIMEOUT = "keepAliveTimeout";

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
	public static final String RUNTIME_LOG_FILE_NAME = "rtm.log";
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

	/** Carriage return character */
	public static final byte CR = 0x0D;
	/** Line feed character */
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
}
