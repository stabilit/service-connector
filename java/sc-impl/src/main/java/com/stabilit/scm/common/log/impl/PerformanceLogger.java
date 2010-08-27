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

import java.util.Formatter;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.IPerformanceLogger;
import com.stabilit.scm.common.log.Loggers;

public class PerformanceLogger implements IPerformanceLogger {

	private static final Logger perfLogger = Logger.getLogger(Loggers.PERFORMANCE.getValue());
	private static final IPerformanceLogger PERFORMANCE_LOGGER = new PerformanceLogger();

	/** The thread local is needed to save time in running thread. */
	private ThreadLocal<PerformanceItem> threadLocal = new ThreadLocal<PerformanceItem>();
	private static String END_STR = "perf by class %s.%s time(ms) %s.%s started %s.%s";

	/**
	 * Instantiates a new performance logger. Private for singelton use.
	 */
	private PerformanceLogger() {
	}

	public static IPerformanceLogger getInstance() {
		return PerformanceLogger.PERFORMANCE_LOGGER;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void begin(String source, String methodName) {
		if (PerformanceLogger.perfLogger.isDebugEnabled() == false) {
			return;
		}
		this.threadLocal.set(new PerformanceItem(source, methodName, System.nanoTime()));
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void end(String source, String methodName) {
		if (PerformanceLogger.perfLogger.isDebugEnabled() == false) {
			return;
		}
		PerformanceItem beginItem = this.threadLocal.get();
		if (beginItem == null) {
			return;
		}
		String beginMethodName = beginItem.getMethodName();
		long beginTime = beginItem.getTime();
		long endTime = System.nanoTime();
		String beginSource = beginItem.getSource();

		Formatter format = new Formatter();
		format.format(END_STR, source, beginMethodName, String.valueOf((endTime - beginTime) / 1000000), String
				.valueOf((endTime - beginTime) % 1000000), beginSource, beginMethodName);
		PerformanceLogger.perfLogger.debug(format.toString());
		format.close();
	}

	private class PerformanceItem {
		private String source;
		private String methodName;
		private long time;

		public PerformanceItem(String source, String methodName, long time) {
			super();
			this.source = source;
			this.methodName = methodName;
			this.time = time;
		}

		public String getSource() {
			return source;
		}

		public String getMethodName() {
			return methodName;
		}

		public long getTime() {
			return time;
		}
	}
}
