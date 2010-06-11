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
import java.util.Formatter;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;
import com.stabilit.scm.common.log.listener.IPerformanceListener;
import com.stabilit.scm.common.log.listener.PerformanceEvent;

/**
 * The Class PerformanceLogger. Provides functionality of logging an <code>PerformanceEvent</code>.
 */
public class PerformanceLogger implements IPerformanceListener, ILoggerDecorator {

	/** The thread local is needed to save timestamps in running thread. */
	private ThreadLocal<PerformanceEvent> threadLocal;

	/** The concrete logger implementation to use. */
	private ILogger logger;

	private Formatter format;
	private String END_STR = "perf by class %s.%s time(ms) %s.%s";

	/**
	 * Instantiates a new performance logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	PerformanceLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
		this.threadLocal = new ThreadLocal<PerformanceEvent>();
		this.format = null;
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

			format = new Formatter();
			format.format(END_STR, source.getClass().getSimpleName(), beginMethodName, String
					.valueOf((endTime - beginTime) / 1000000), String.valueOf((endTime - beginTime) % 1000000));
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
		return IConstants.PERFORMANCE_LOG_FILE_NAME;
	}
}
