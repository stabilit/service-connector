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

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.IRuntimeListener;
import com.stabilit.sc.listener.RuntimeEvent;
import com.stabilit.sc.log.SimpleLogger;

/**
 * The Class RuntimeLogger. Provides functionality of logging an <code>RuntimeEvent</code>.
 * 
 * @author JTraber
 */
public class RuntimeLogger extends SimpleLogger implements IRuntimeListener {

	/**
	 * Instantiates a new runtime logger.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	RuntimeLogger() throws Exception {
		this(IConstants.LOG_DIR, IConstants.RUNTIME_LOG_FILE_NAME);
	}

	/**
	 * Instantiates a new runtime logger.
	 * 
	 * @param dir
	 *            the directory
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	RuntimeLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.log.SimpleLogger#log(byte[])
	 */
	/**
	 * Log.
	 * 
	 * @param buffer
	 *            the buffer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.log.SimpleLogger#log(byte[], int, int)
	 */
	/**
	 * Log.
	 * 
	 * @param buffer
	 *            the buffer
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(byte[] buffer, int offset, int length) throws IOException {
		super.log(buffer, offset, length);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	/**
	 * New instance.
	 * 
	 * @return the factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.listener.IWarningListener#warningEvent(com.stabilit.sc.listener.WarningEvent)
	 */
	/**
	 * Runtime event.
	 * 
	 * @param runtimeEvent
	 *            the runtime event
	 */
	@Override
	public synchronized void runtimeEvent(RuntimeEvent runtimeEvent) {
		try {
			this.log(runtimeEvent.getText());
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
