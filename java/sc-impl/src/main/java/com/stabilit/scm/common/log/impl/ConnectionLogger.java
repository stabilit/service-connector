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

	/** The Constant connectionLogger. */
	private static final Logger connectionLogger = Logger.getLogger(Loggers.CONNECTION.getValue());
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
	public synchronized void logConnect(String source, int port) {
		if (ConnectionLogger.connectionLogger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();
			format.format(CONNECT_STR, source, InetAddress.getLocalHost().toString(), String.valueOf(port));
			ConnectionLogger.connectionLogger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logDisconnect(String source, int port) {
		if (ConnectionLogger.connectionLogger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();
			format.format(DISCONNECT_STR, source, InetAddress.getLocalHost().toString(), String.valueOf(port));
			ConnectionLogger.connectionLogger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logRead(String source, int port, byte[] data, int offset, int length) {
		if (ConnectionLogger.connectionLogger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();

			if (length > 0) {
				format.format(READ_STR, source, InetAddress.getLocalHost().toString(), String.valueOf(port),
						new String(data, offset, length));
			} else {
				format.format(READ_STR, source.getClass().getClass().getName(), InetAddress.getLocalHost().toString(),
						String.valueOf(port), new String(data, offset, length));
			}
			ConnectionLogger.connectionLogger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logWrite(String source, int port, byte[] data, int offset, int length) {
		if (ConnectionLogger.connectionLogger.isDebugEnabled() == false) {
			return;
		}
		try {
			Formatter format = new Formatter();

			if (length > 0) {
				format.format(WRITE_STR, source, InetAddress.getLocalHost().toString(), String.valueOf(port),
						new String(data, offset, length));
			} else {
				format.format(WRITE_STR, source.getClass().getClass().getName(), InetAddress.getLocalHost().toString(),
						String.valueOf(port), new String(data, offset, length));
			}
			ConnectionLogger.connectionLogger.debug(format.toString());
			format.close();
		} catch (IOException e) {
			// TODO JOT exception logging
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logKeepAlive(String source, int nrOfIdles) {
		if (ConnectionLogger.connectionLogger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(KEEP_ALIVE_STR, source, nrOfIdles);
		ConnectionLogger.connectionLogger.debug(format.toString());
		format.close();
	}
}
