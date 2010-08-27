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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Formatter;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.IConnectionLogger;
import com.stabilit.scm.common.log.Loggers;

public class ConnectionLogger implements IConnectionLogger {

	private static final Logger logger = Logger.getLogger(Loggers.CONNECTION.getValue());
	private static final IConnectionLogger CONNECTION_LOGGER = new ConnectionLogger();

	private static String CONNECT_STR = "connect by class %s - %s:%s";
	private static String DISCONNECT_STR = "disconnect by class %s - %s:%s";
	private static String READ_STR = "read by class %s - %s:%s : %s";
	private static String WRITE_STR = "write by class %s - %s:%s : %s";
	private static String KEEP_ALIVE_STR = "keep alive by class %s - number of keep alive: %s";

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
	public synchronized void logConnect(String className, int port) {
		if (logger.isInfoEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();
			format.format(CONNECT_STR, className, InetAddress.getLocalHost().toString(), String.valueOf(port));
			logger.info(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logDisconnect(String className, int port) {
		if (logger.isInfoEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();
			format.format(DISCONNECT_STR, className, InetAddress.getLocalHost().toString(), String.valueOf(port));
			logger.info(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logRead(String className, int port, byte[] data, int offset, int length) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();

			if (length > 0) {
				format.format(READ_STR, className, InetAddress.getLocalHost().toString(), String.valueOf(port),
						new String(data, offset, length));
			} else {
				format.format(READ_STR, className, InetAddress.getLocalHost().toString(),
						String.valueOf(port), new String(data, offset, length));
			}
			logger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logWrite(String className, int port, byte[] data, int offset, int length) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();

			if (length > 0) {
				format.format(WRITE_STR, className, InetAddress.getLocalHost().toString(), String.valueOf(port),
						new String(data, offset, length));
			} else {
				format.format(WRITE_STR, className, InetAddress.getLocalHost().toString(),
						String.valueOf(port), new String(data, offset, length));
			}
			logger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logKeepAlive(String className, int nrOfIdles) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(KEEP_ALIVE_STR, className, nrOfIdles);
		logger.debug(format.toString());
		format.close();
	}
}
