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
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ConnectionEvent;
import com.stabilit.sc.listener.IConnectionListener;
import com.stabilit.sc.log.SimpleLogger;

/**
 * The Class ConnectionLogger. Provides functionality of logging a <code>ConnectionEvent</code>.
 * 
 * @author JTraber
 */
public class ConnectionLogger extends SimpleLogger implements IConnectionListener {

	/**
	 * Instantiates a new connection logger.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	ConnectionLogger() throws Exception {
		this(IConstants.LOG_DIR, IConstants.CONNECTION_LOG_FILE_NAME);
	}

	/**
	 * Instantiates a new connection logger.
	 * 
	 * @param dir
	 *            the directory
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	ConnectionLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	/** {@inheritDoc} */
	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	/** {@inheritDoc} */
	public void log(byte[] buffer, int offset, int length) throws IOException {
		super.log(buffer, offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void connectEvent(ConnectionEvent connectionEvent) {
		try {
			this.log("------- connect -------\r\n");
			this.log("connect by class " + connectionEvent.getSource().getClass().getName());
			this.log(" - ");
			this.log(InetAddress.getLocalHost().toString());
			this.log(":");
			this.log(String.valueOf(connectionEvent.getPort()));
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void disconnectEvent(ConnectionEvent connectionEvent) {
		try {
			this.log("------- disconnect -------\r\n");
			this.log("disconnect by class " + connectionEvent.getSource().getClass().getName());
			this.log(" - ");
			this.log(InetAddress.getLocalHost().toString());
			this.log(":");
			this.log(String.valueOf(connectionEvent.getPort()));
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void readEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.log(">>>>>>>> ");
			this.log(InetAddress.getLocalHost().toString());
			this.log(":");
			this.log(String.valueOf(connectionEvent.getPort()));
			this.log(" - read >>>>>>>\r\n");
			if (length > 0) {
				this.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
				this.log((byte[]) connectionEvent.getData());
			}
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void writeEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.log("<<<<<<<< ");
			this.log(InetAddress.getLocalHost().toString());
			this.log(":");
			this.log(String.valueOf(connectionEvent.getPort()));
			this.log(" - write <<<<<<<<\r\n");
			if (length > 0) {
				this.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
				this.log((byte[]) connectionEvent.getData());
			}
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
