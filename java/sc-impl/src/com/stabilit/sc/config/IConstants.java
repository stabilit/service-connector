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
package com.stabilit.sc.config;

/**
 * The Interface IConstants. SC constants.
 * 
 * @author JTraber
 */
public interface IConstants {

	/** Protocol literal. */
	public final String HTTP = "http";
	/** File qualifier for Http requests. */
	public static final String HTTP_FILE = "/";

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

	/** The logging directory. */
	public final String LOG_DIR = "log/";
	/** The file name of connection log file. */
	public final String CONNECTION_LOG_FILE_NAME = "con.log";
	/** The file name of exception log file. */
	public final String EXCEPTION_LOG_FILE_NAME = "exc.log";
	/** The file name of general log file. */
	public final String GENERAL_LOG_FILE_NAME = "gen.log";
	/** The file name of scmp log file. */
	public final String SCMP_LOG_FILE_NAME = "scmp.log";
	/** The file name of performance log file. */
	public final String PERFORMANCE_LOG_FILE_NAME = "prf.log";
	/** The file name of warning log file. */
	public final String RUNTIME_LOG_FILE_NAME = "wrn.log";

	/** The ACCEPT_PARAMS. */
	public final String ACCEPT_PARAMS = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
}
