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
	public final String HTTP = "http";
	/** File qualifier for Http requests. */
	public final String HTTP_FILE = "/";

	/** The DEFAULT_NR_OF_THREADS. */
	public final int DEFAULT_NR_OF_THREADS = 10;
	/** The MAX KEEP ALIVE TIME OF THREADS. */
	public final int MAX_KEEP_ALIVE_OF_THREADS = 10;
	/** The read timeout in seconds. */
	public final int READ_TIMEOUT = 200;
	/** The write timeout in seconds. */
	public final int WRITE_TIMEOUT = 200;

	/** The REGEX. */
	public final String COMMA_OR_SEMICOLON = ",|;";
	/** The CON. */
	public final String CON_QUALIFIER = ".con";
	/** The HOST. */
	public final String HOST_QUALIFIER = ".host";
	/** The PORT. */
	public final String PORT_QUALIFIER = ".port";
	/** The THREAD. */
	public final String THREAD_QUALIFIER = ".thread";
	/** The connection names. */
	public final String CONNECTION_NAMES = "connectionNames";
	/** The server names. */
	public final String SERVER_NAMES = "serverNames";
	/** The services Names. */
	public final String SERVICE_NAMES = "serviceNames";

	/** The logging directory. */
	public final String LOG_DIR = "log/";
	/** The file name of connection log file. */
	public final String CONNECTION_LOG_FILE_NAME = "con.log";
	/** The file name of exception log file. */
	public final String EXCEPTION_LOG_FILE_NAME = "exc.log";
	/** The file name of general log file. */
	public final String GENERAL_LOG_FILE_NAME = "gen.log";
	/** The file name of performance log file. */
	public final String PERFORMANCE_LOG_FILE_NAME = "prf.log";
	/** The file name of warning log file. */
	public final String RUNTIME_LOG_FILE_NAME = "rtm.log";
	public final String SESSION_LOG_FILE_NAME = "ses.log";

	/** The ACCEPT_PARAMS. */
	public final String ACCEPT_PARAMS = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";

	/** The DEFAULT_CLIENT_CON. */
	public final String DEFAULT_CLIENT_CON = "netty.http";
	/** The DEFAULT_SERVER_CON. */
	public final String DEFAULT_SERVER_CON = "netty.tcp";
}
