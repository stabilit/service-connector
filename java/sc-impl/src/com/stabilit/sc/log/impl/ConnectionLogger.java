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
package com.stabilit.sc.log.impl;

import java.io.IOException;
import java.net.InetAddress;

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.listener.ConnectionEvent;
import com.stabilit.sc.listener.IConnectionListener;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

/**
 * The Class ConnectionLogger. Provides functionality of logging a <code>ConnectionEvent</code>.
 */
public class ConnectionLogger implements IConnectionListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	/**
	 * Instantiates a new connection logger. Only visible in package for Factory.
	 * 
	 * @param logger the logger
	 */
	ConnectionLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void connectEvent(ConnectionEvent connectionEvent) {
		try {
			this.logger.log("------- connect -------\r\n");
			this.logger.log("connect by class " + connectionEvent.getSource().getClass().getName());
			this.logger.log(" - ");
			this.logger.log(InetAddress.getLocalHost().toString());
			this.logger.log(":");
			this.logger.log(String.valueOf(connectionEvent.getPort()));
			this.logger.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void disconnectEvent(ConnectionEvent connectionEvent) {
		try {
			this.logger.log("------- disconnect -------\r\n");
			this.logger.log("disconnect by class " + connectionEvent.getSource().getClass().getName());
			this.logger.log(" - ");
			this.logger.log(InetAddress.getLocalHost().toString());
			this.logger.log(":");
			this.logger.log(String.valueOf(connectionEvent.getPort()));
			this.logger.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void readEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.logger.log(">>>>>>>> ");
			this.logger.log(InetAddress.getLocalHost().toString());
			this.logger.log(":");
			this.logger.log(String.valueOf(connectionEvent.getPort()));
			this.logger.log(" - read >>>>>>>\r\n");
			if (length > 0) {
				this.logger.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
				this.logger.log((byte[]) connectionEvent.getData());
			}
			this.logger.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void writeEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.logger.log("<<<<<<<< ");
			this.logger.log(InetAddress.getLocalHost().toString());
			this.logger.log(":");
			this.logger.log(String.valueOf(connectionEvent.getPort()));
			this.logger.log(" - write <<<<<<<<\r\n");
			if (length > 0) {
				this.logger.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
				this.logger.log((byte[]) connectionEvent.getData());
			}
			this.logger.log("\r\n");
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
