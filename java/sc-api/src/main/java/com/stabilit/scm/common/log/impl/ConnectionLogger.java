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
package com.stabilit.scm.common.log.impl;

import java.util.Formatter;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.IConnectionLogger;
import com.stabilit.scm.common.log.Loggers;

public class ConnectionLogger implements IConnectionLogger {

	private static final Logger logger = Logger.getLogger(Loggers.CONNECTION.getValue());
	private static final IConnectionLogger CONNECTION_LOGGER = new ConnectionLogger();

	private static String CONNECT_STR = "%s connect to:%s/%s";
	private static String DISCONNECT_STR = "%s disconnect from:%s/%s";
	private static String READ_STR = "%s read on:%s/%s buffer:%s";
	private static String WRITE_STR = "%s write from:%s/%s buffer:%s";
	private static String KEEP_ALIVE_STR = "%s send keep alive to:%s/%s - idle count: %s";

	/**
	 * Instantiates a new connection logger. Private for singelton use.
	 */
	private ConnectionLogger() {
	}

	public static IConnectionLogger getInstance() {
		return ConnectionLogger.CONNECTION_LOGGER;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logConnect(String className, String hostName, int port) {
		Formatter format = new Formatter();
		format.format(CONNECT_STR, className, hostName, String.valueOf(port));
		logger.info(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logDisconnect(String className, String hostName, int port) {

		Formatter format = new Formatter();
		format.format(DISCONNECT_STR, className, hostName, String.valueOf(port));
		logger.info(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logReadBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
		Formatter format = new Formatter();
		if (length > 0) {
			format.format(READ_STR, className, hostName, String.valueOf(port), new String(data, offset, length));
		} else {
			format.format(READ_STR, className, hostName, String.valueOf(port), new String(data, offset, length)); //TODO TRN
		}
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logWriteBuffer(String className, String hostName, int port, byte[] data, int offset, int length) {
		Formatter format = new Formatter();
		if (length > 0) {
			format.format(WRITE_STR, className, hostName, String.valueOf(port), new String(data, offset, length));
		} else {
			format.format(WRITE_STR, className, hostName, String.valueOf(port), new String(data, offset, length)); //TODO TRN
		}
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logKeepAlive(String className, String hostName, int port, int nrOfIdles) {
		Formatter format = new Formatter();
		format.format(KEEP_ALIVE_STR, className, hostName, String.valueOf(port), nrOfIdles);
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
}
