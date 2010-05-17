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
import com.stabilit.sc.listener.ILoggerListener;
import com.stabilit.sc.listener.LoggerEvent;
import com.stabilit.sc.log.SimpleLogger;

/**
 * The Class GeneralLogger. Provides functionality of logging an <code>LoggerEvent</code>.
 * 
 * @author JTraber
 */
public class GeneralLogger extends SimpleLogger implements ILoggerListener {

	/**
	 * Instantiates a new general logger.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	GeneralLogger() throws Exception {
		this(IConstants.LOG_DIR, IConstants.GENERAL_LOG_FILE_NAME);
	}

	/**
	 * Instantiates a new general logger.
	 * 
	 * @param dir
	 *            the directory
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	GeneralLogger(String dir, String fileName) throws Exception {
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
	public synchronized void logEvent(LoggerEvent loggerEvent) {
		try {
			this.log(loggerEvent.getLevel().getName());
			this.log("-");
			this.log(loggerEvent.getText());
			this.log("\r\n");
			System.out.print(loggerEvent.getLevel().getName());
			System.out.print("-");
			System.out.print(loggerEvent.getText());
			System.out.print("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
