/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import com.stabilit.sc.listener.IPerformanceListener;
import com.stabilit.sc.listener.PerformanceEvent;
import com.stabilit.sc.log.SimpleLogger;

/**
 * The Class PerformanceLogger. Provides functionality of logging an <code>PerformanceEvent</code>.
 * 
 * @author JTraber
 */
public class PerformanceLogger extends SimpleLogger implements IPerformanceListener {

	/**
	 * Instantiates a new performance logger.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public PerformanceLogger() throws Exception {
		this(IConstants.LOG_DIR, IConstants.PERFORMANCE_LOG_FILE_NAME);
	}

	/**
	 * Instantiates a new performance logger.
	 * 
	 * @param dir
	 *            the dir
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public PerformanceLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.log.SimpleLogger#log(byte[])
	 */
	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.log.SimpleLogger#log(byte[], int, int)
	 */
	public void log(byte[] buffer, int offset, int length) throws IOException {
		super.log(buffer, offset, length);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.listener.IPerformanceListener#begin(com.stabilit.sc.listener.PerformanceEvent)
	 */
	@Override
	public synchronized void begin(PerformanceEvent performanceEvent) {
		try {
			// TODO (JOT)
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.listener.IPerformanceListener#end(com.stabilit.sc.listener.PerformanceEvent)
	 */
	public synchronized void end(PerformanceEvent performanceEvent) {
		try {
			// TODO (JOT)
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
