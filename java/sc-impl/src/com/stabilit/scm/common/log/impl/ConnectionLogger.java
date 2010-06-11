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

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;
import com.stabilit.scm.common.log.listener.ConnectionEvent;
import com.stabilit.scm.common.log.listener.IConnectionListener;

/**
 * The Class ConnectionLogger. Provides functionality of logging a <code>ConnectionEvent</code>.
 */
public class ConnectionLogger implements IConnectionListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	private Formatter format;
	private String CONNECT_EVENT_STR = "connect by class %s - %s:%s";
	private String DISCONNECT_EVENT_STR = "disconnect by class %s - %s:%s";
	private String READ_EVENT_STR = "read by class %s - %s:%s : %s";
	private String WRITE_EVENT_STR = "write by class %s - %s:%s : %s";

	/**
	 * Instantiates a new connection logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	ConnectionLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
		this.format = null;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void connectEvent(ConnectionEvent connectionEvent) {
		try {
			format = new Formatter();
			format.format(CONNECT_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
					.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()));
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void disconnectEvent(ConnectionEvent connectionEvent) {
		try {
			format = new Formatter();
			format.format(DISCONNECT_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
					.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()));
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void readEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			format = new Formatter();

			if (length > 0) {
				format.format(READ_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
						.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()), new String(
						(byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length));
			} else {
				format.format(READ_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
						.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()), new String(
						(byte[]) connectionEvent.getData()));
			}
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void writeEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			format = new Formatter();

			if (length > 0) {
				format.format(WRITE_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
						.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()), new String(
						(byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length));
			} else {
				format.format(WRITE_EVENT_STR, connectionEvent.getSource().getClass().getName(), InetAddress
						.getLocalHost().toString(), String.valueOf(connectionEvent.getPort()), new String(
						(byte[]) connectionEvent.getData()));
			}
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ILoggerDecorator newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogDir() {
		return IConstants.LOG_DIR;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogFileName() {
		return IConstants.CONNECTION_LOG_FILE_NAME;
	}
}
