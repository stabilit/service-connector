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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;

/**
 * The Class PerformanceLogger.
 */
public final class PerformanceLogger {

	/** The Constant performanceLogger. */
	private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger(Loggers.PERFORMANCE.getValue());

	/** The Constant instance. */
	private static final PerformanceLogger INSTANCE = new PerformanceLogger();
	/** The thread local is needed to save time in running thread. */
	private static ThreadLocal<PerformanceItem> threadLocal = new ThreadLocal<PerformanceItem>();
	/** The performance items. */
	private static Map<String, PerformanceItem> performanceItems;
	/** The performance string. */
	private static String perfStr = "key:%s begin:%s.%s() end:%s.%s() time:%s.%s(ms)";

	/**
	 * Private constructor for singleton use.
	 */
	private PerformanceLogger() {
		PerformanceLogger.performanceItems = new HashMap<String, PerformanceLogger.PerformanceItem>();
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return PERFORMANCE_LOGGER.isTraceEnabled();
	}

	/**
	 * Begin. Makes performance logger active. The method end stops the measuring and logs the result. Be careful its only working within the same thread.
	 */
	public static synchronized void beginThreadBound() {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
			PerformanceLogger.threadLocal.set(INSTANCE.new PerformanceItem(Thread.currentThread().getStackTrace()[2].getClassName(),
					Thread.currentThread().getStackTrace()[2].getMethodName(), System.nanoTime()));
		}
	}

	/**
	 * End. Stops current active performance logger and logs the result. Be careful its only working within the same thread.
	 *
	 * @param key the key
	 */
	public static synchronized void endThreadBound(String key) {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
			long endTime = System.nanoTime();
			PerformanceItem beginItem = PerformanceLogger.threadLocal.get();
			if (beginItem == null) {
				return;
			}
			String beginMethodName = beginItem.getMethodName();
			String beginClassName = beginItem.getClassName();
			long beginTime = beginItem.getTime();

			Formatter format = new Formatter();
			format.format(perfStr, key, beginClassName, beginMethodName, Thread.currentThread().getStackTrace()[2].getClassName(),
					Thread.currentThread().getStackTrace()[2].getMethodName(), String.valueOf((endTime - beginTime) / Constants.SEC_TO_NANOSSEC_FACTOR),
					String.valueOf((endTime - beginTime) % Constants.SEC_TO_NANOSSEC_FACTOR));
			PERFORMANCE_LOGGER.trace(format.toString());
			format.close();
		}

	}

	/**
	 * Begin. Makes performance logger active. The method end stops the measuring and logs the result. Be careful begin/end is not thread safe. Coordinating begin and end must be
	 * done by the user of the PerformanceLogger. Use key to identify performanceItem.
	 *
	 * @param key the key
	 */
	public static synchronized void begin(String key) {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
			PerformanceItem performanceItem = INSTANCE.new PerformanceItem(Thread.currentThread().getStackTrace()[2].getClassName(),
					Thread.currentThread().getStackTrace()[2].getMethodName(), System.nanoTime());
			PerformanceLogger.performanceItems.put(key, performanceItem);
		}
	}

	/**
	 * End. Stops current active performance logger and logs the result. Be careful begin/end is not thread safe. Coordinating begin and end must be done by the user of the
	 * PerformanceLogger. Use key to identify performanceItem.
	 *
	 * @param key the key
	 */
	public static synchronized void end(String key) {
		if (PERFORMANCE_LOGGER.isTraceEnabled()) {
			long endTime = System.nanoTime();
			PerformanceItem beginItem = PerformanceLogger.performanceItems.remove(key);
			if (beginItem == null) {
				return;
			}
			String beginMethodName = beginItem.getMethodName();
			String beginClassName = beginItem.getClassName();
			long beginTime = beginItem.getTime();

			Formatter format = new Formatter();
			format.format(perfStr, key, beginClassName, beginMethodName, Thread.currentThread().getStackTrace()[2].getClassName(),
					Thread.currentThread().getStackTrace()[2].getMethodName(), String.valueOf((endTime - beginTime) / Constants.SEC_TO_NANOSSEC_FACTOR),
					String.valueOf((endTime - beginTime) % Constants.SEC_TO_NANOSSEC_FACTOR));
			PERFORMANCE_LOGGER.trace(format.toString());
			format.close();
		}

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
		 * @param className the class name
		 * @param methodName the method name
		 * @param time the time
		 */
		public PerformanceItem(String className, String methodName, long time) {
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
