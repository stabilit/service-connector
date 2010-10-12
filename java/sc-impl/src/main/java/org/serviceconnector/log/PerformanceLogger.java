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
package org.serviceconnector.log;

import java.util.Formatter;

import org.apache.log4j.Logger;


public class PerformanceLogger {

	private static final Logger logger = Logger.getLogger(Loggers.PERFORMANCE.getValue());
	private static final PerformanceLogger PERFORMANCE_LOGGER = new PerformanceLogger();

	/** The thread local is needed to save time in running thread. */
	private ThreadLocal<PerformanceItem> threadLocal = new ThreadLocal<PerformanceItem>();
	private static String END_STR = "begin:%s.%s() end:%s.%s() time:%s.%s(ms)";

	/**
	 * Instantiates a new performance logger. Private for singelton use.
	 */
	private PerformanceLogger() {
	}

	public static PerformanceLogger getInstance() {
		return PerformanceLogger.PERFORMANCE_LOGGER;
	}

	public synchronized void begin(String className, String methodName) {
		if (logger.isTraceEnabled()) {
			this.threadLocal.set(new PerformanceItem(className, methodName, System.nanoTime()));
		}
	}

	/**
	 * @param className
	 * @param methodName
	 */
	public synchronized void end(String className, String methodName) {
		if (logger.isTraceEnabled()) {
			PerformanceItem beginItem = this.threadLocal.get();
			if (beginItem == null) {
				return;
			}
			String beginMethodName = beginItem.getMethodName();
			String beginClassName = beginItem.getClassName();
			long beginTime = beginItem.getTime();
			long endTime = System.nanoTime();

			Formatter format = new Formatter();
			format.format(END_STR, beginClassName, beginMethodName, className, methodName, String
					.valueOf((endTime - beginTime) / 1000000), String.valueOf((endTime - beginTime) % 1000000));
			logger.trace(format.toString());
			format.close();
		}

	}

	/**
	 * @return
	 */
	public boolean isEnabled() {
		return logger.isTraceEnabled();
	}

	private class PerformanceItem {
		private String className;
		private String methodName;
		private long time;

		public PerformanceItem(String className, String methodName, long time) {
			super();
			this.className = className;
			this.methodName = methodName;
			this.time = time;
		}

		public String getClassName() {
			return className;
		}

		public String getMethodName() {
			return methodName;
		}

		public long getTime() {
			return time;
		}
	}
}
