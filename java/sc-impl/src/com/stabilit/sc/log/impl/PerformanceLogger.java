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
import com.stabilit.sc.listener.IPerformanceListener;
import com.stabilit.sc.listener.PerformanceEvent;
import com.stabilit.sc.log.SimpleLogger;

/**
 * The Class PerformanceLogger. Provides functionality of logging an <code>PerformanceEvent</code>.
 * 
 * @author JTraber
 */
public class PerformanceLogger extends SimpleLogger implements IPerformanceListener {

	/** The thread local is needed to save timestamps in running thread. */
	private ThreadLocal<PerformanceEvent> threadLocal;

	/**
	 * Instantiates a new performance logger.
	 * 
	 * @throws Exception the exception
	 */
	PerformanceLogger() throws Exception {
		this(IConstants.LOG_DIR, IConstants.PERFORMANCE_LOG_FILE_NAME);
	}

	/**
	 * Instantiates a new performance logger.
	 * 
	 * @param dir the directory
	 * @param fileName the file name
	 * 
	 * @throws Exception the exception
	 */
	PerformanceLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
		this.threadLocal = new ThreadLocal<PerformanceEvent>();
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
	public synchronized void begin(PerformanceEvent performanceEvent) {
		this.threadLocal.set(performanceEvent);
	}

	/** {@inheritDoc} */
	public synchronized void end(PerformanceEvent performanceEvent) {
		try {
			PerformanceEvent beginEvent = this.threadLocal.get();
			String beginMethodName = beginEvent.getMethodName();
			long beginTime = beginEvent.getTime();
			long endTime = performanceEvent.getTime();
			Object source = performanceEvent.getSource();
			this.log("PRF ");
			this.log(source.getClass().getSimpleName());
			this.log(".");
			this.log(beginMethodName);
			this.log(" time(ms) ");
			this.log(String.valueOf((endTime - beginTime) / 1000000));
			this.log(".");
			this.log(String.valueOf((endTime - beginTime) % 1000000));
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
