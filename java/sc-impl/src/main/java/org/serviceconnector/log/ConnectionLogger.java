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


public class ConnectionLogger {

	private static final Logger connectionLogger = Logger.getLogger(Loggers.CONNECTION.getValue());
	private static final ConnectionLogger instance = new ConnectionLogger();

	private static String CONNECT_STR = "%s connect to:%s/%s";
	private static String DISCONNECT_STR = "%s disconnect from:%s/%s";
	private static String READ_STR = "%s read from:%s/%s buffer:%s";
	private static String WRITE_STR = "%s write to:%s/%s buffer:%s";
	private static String KEEP_ALIVE_STR = "%s send keep alive to:%s/%s - idle count: %s";

	/**
	 * Private constructor for singleton use.
	 */
	private ConnectionLogger() {
	}

	public static ConnectionLogger getInstance() {
		return ConnectionLogger.instance;
	}

	/**
	 * @param className
	 * @param hostName
	 * @param port
	 */
	public synchronized void logConnect(String className, String hostName, int port) {
		Formatter format = new Formatter();
		format.format(CONNECT_STR, className, hostName, String.valueOf(port));
		connectionLogger.debug(format.toString());
		format.close();
	}


	/**
	 * @param className
	 * @param hostName
	 * @param port
	 */
	public synchronized void logDisconnect(String className, String hostName, int port) {

		Formatter format = new Formatter();
		format.format(DISCONNECT_STR, className, hostName, String.valueOf(port));
		connectionLogger.debug(format.toString());
		format.close();
	}


	/**
	 * @param className
	 * @param hostName
	 * @param port
	 * @param data
	 * @param offset
	 * @param length
	 */
	public synchronized void logReadBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
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
	 * @param className
	 * @param hostName
	 * @param port
	 * @param data
	 * @param offset
	 * @param length
	 */
	public synchronized void logWriteBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
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
	 * @param className
	 * @param hostName
	 * @param port
	 * @param nrOfIdles
	 */
	public synchronized void logKeepAlive(String className, String hostName, int port, int nrOfIdles) {
		Formatter format = new Formatter();
		format.format(KEEP_ALIVE_STR, className, hostName, String.valueOf(port), nrOfIdles);
		connectionLogger.trace(format.toString());
		format.close();
	}


	/**
	 * @return
	 */
	public boolean isEnabledFull() {
		return connectionLogger.isTraceEnabled();
	}


	/**
	 * @return
	 */
	public boolean isEnabled() {
		return connectionLogger.isDebugEnabled();
	}
}
