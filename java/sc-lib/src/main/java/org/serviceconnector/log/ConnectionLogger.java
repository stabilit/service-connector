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
package org.serviceconnector.log;

import java.util.Formatter;

import org.apache.log4j.Logger;

/**
 * The Class ConnectionLogger.
 */
public class ConnectionLogger {

	/** The Constant connectionLogger. */
	private static final Logger connectionLogger = Logger.getLogger(Loggers.CONNECTION.getValue());

	/** The CONNECT_STR. */
	private static String CONNECT_STR = "%s connect to:%s/%s";
	/** The DISCONNECT_STR. */
	private static String DISCONNECT_STR = "%s disconnect from:%s/%s";
	/** The READ_STR. */
	private static String READ_STR = "%s read from:%s/%s buffer:%s";
	/** The WRITE_STR. */
	private static String WRITE_STR = "%s write to:%s/%s buffer:%s";
	/** The KEEP_ALIVE_STR. */
	private static String KEEP_ALIVE_STR = "%s send keep alive to:%s/%s - idle count: %s";

	/**
	 * Private constructor for singleton use.
	 */
	private ConnectionLogger() {
	}

	/**
	 * Log connect.
	 * 
	 * @param className
	 *            the class name
	 * @param hostName
	 *            the host name
	 * @param port
	 *            the port
	 */
	public static synchronized void logConnect(String className, String hostName, int port) {
		Formatter format = new Formatter();
		format.format(CONNECT_STR, className, hostName, String.valueOf(port));
		connectionLogger.debug(format.toString());
		format.close();
	}

	/**
	 * Log disconnect.
	 * 
	 * @param className
	 *            the class name
	 * @param hostName
	 *            the host name
	 * @param port
	 *            the port
	 */
	public static synchronized void logDisconnect(String className, String hostName, int port) {

		Formatter format = new Formatter();
		format.format(DISCONNECT_STR, className, hostName, String.valueOf(port));
		connectionLogger.debug(format.toString());
		format.close();
	}

	/**
	 * Log read buffer.
	 * 
	 * @param className
	 *            the class name
	 * @param hostName
	 *            the host name
	 * @param port
	 *            the port
	 * @param data
	 *            the data
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 */
	public static synchronized void logReadBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
		Formatter format = new Formatter();
		if (length > 100) {
			format.format(READ_STR, className, hostName, String.valueOf(port), new String(data, offset, 100));
		} else {
			format.format(READ_STR, className, hostName, String.valueOf(port), new String(data, offset, length));
		}
		connectionLogger.trace(format.toString());
		format.close();
	}

	/**
	 * Log write buffer.
	 * 
	 * @param className
	 *            the class name
	 * @param hostName
	 *            the host name
	 * @param port
	 *            the port
	 * @param data
	 *            the data
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 */
	public static synchronized void logWriteBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
		Formatter format = new Formatter();
		if (length > 100) {
			format.format(WRITE_STR, className, hostName, String.valueOf(port), new String(data, offset, 100));
		} else {
			format.format(WRITE_STR, className, hostName, String.valueOf(port), new String(data, offset, length));
		}
		connectionLogger.trace(format.toString());
		format.close();
	}

	/**
	 * Log keep alive.
	 * 
	 * @param className
	 *            the class name
	 * @param hostName
	 *            the host name
	 * @param port
	 *            the port
	 * @param nrOfIdles
	 *            the nr of idles
	 */
	public static synchronized void logKeepAlive(String className, String hostName, int port, int nrOfIdles) {
		Formatter format = new Formatter();
		format.format(KEEP_ALIVE_STR, className, hostName, String.valueOf(port), nrOfIdles);
		connectionLogger.trace(format.toString());
		format.close();
	}

	/**
	 * Checks if is enabled full.
	 * 
	 * @return true, if is enabled full
	 */
	public static boolean isEnabledFull() {
		return connectionLogger.isTraceEnabled();
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return connectionLogger.isDebugEnabled();
	}
}
