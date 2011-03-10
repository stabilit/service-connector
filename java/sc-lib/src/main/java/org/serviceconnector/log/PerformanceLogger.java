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


/**
 * The Class PerformanceLogger.
 */
public final class PerformanceLogger {

	/** The Constant performanceLogger. */
	private static final Logger PERFORMANCE_LOGGER = Logger.getLogger(Loggers.PERFORMANCE.getValue());
	
	/** The Constant instance. */
	private static final PerformanceLogger instance = new PerformanceLogger();

	/** The thread local is needed to save time in running thread. */
	private ThreadLocal<PerformanceItem> threadLocal = new ThreadLocal<PerformanceItem>();
	
	/** The EN d_ str. */
	private static String END_STR = "begin:%s.%s() end:%s.%s() time:%s.%s(ms)";

	/**
	 * Private constructor for singleton use. 
	 */
	private PerformanceLogger() {
	}

	/**
	 * Gets the single instance of PerformanceLogger.
	 * 
	 * @return single instance of PerformanceLogger
	 */
	public static PerformanceLogger getInstance() {
		return PerformanceLogger.instance;
	}

	/**
	 * Begin.
	 * 
	 * @param className
	 *            the class name
	 * @param methodName
	 *            the method name
	 */
	public synchronized void begin(String className, String methodName) {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
			this.threadLocal.set(new PerformanceItem(className, methodName, System.nanoTime()));
		}
	}

	/**
	 * End.
	 * 
	 * @param className
	 *            the class name
	 * @param methodName
	 *            the method name
	 */
	public synchronized void end(String className, String methodName) {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
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
			PERFORMANCE_LOGGER.trace(format.toString());
			format.close();
		}

	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return PERFORMANCE_LOGGER.isTraceEnabled();
	}

	/**
	 * The Class PerformanceItem.
	 */
	private class PerformanceItem {
		
		/** The class name. */
		private String className;
		
		/** The method name. */
		private String methodName;
		
		/** The time. */
		private long time;

		/**
		 * Instantiates a new performance item.
		 * 
		 * @param className
		 *            the class name
		 * @param methodName
		 *            the method name
		 * @param time
		 *            the time
		 */
		public PerformanceItem(String className, String methodName, long time) {
			super();
			this.className = className;
			this.methodName = methodName;
			this.time = time;
		}

		/**
		 * Gets the class name.
		 * 
		 * @return the class name
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * Gets the method name.
		 * 
		 * @return the method name
		 */
		public String getMethodName() {
			return methodName;
		}

		/**
		 * Gets the time.
		 * 
		 * @return the time
		 */
		public long getTime() {
			return time;
		}
	}
}
