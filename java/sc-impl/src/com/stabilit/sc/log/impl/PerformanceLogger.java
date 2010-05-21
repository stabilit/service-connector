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
import com.stabilit.sc.listener.IPerformanceListener;
import com.stabilit.sc.listener.PerformanceEvent;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

/**
 * The Class PerformanceLogger. Provides functionality of logging an <code>PerformanceEvent</code>.
 */
public class PerformanceLogger implements IPerformanceListener, ILoggerDecorator {

	/** The thread local is needed to save timestamps in running thread. */
	private ThreadLocal<PerformanceEvent> threadLocal;

	/** The concrete logger implementation to use. */
	private ILogger logger;

	/**
	 * Instantiates a new performance logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	PerformanceLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
		this.threadLocal = new ThreadLocal<PerformanceEvent>();
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
			this.logger.log("PRF ");
			this.logger.log(source.getClass().getSimpleName());
			this.logger.log(".");
			this.logger.log(beginMethodName);
			this.logger.log(" time(ms) ");
			this.logger.log(String.valueOf((endTime - beginTime) / 1000000));
			this.logger.log(".");
			this.logger.log(String.valueOf((endTime - beginTime) % 1000000));
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
		return IConstants.PERFORMANCE_LOG_FILE_NAME;
	}
}
