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
package com.stabilit.scm.common.listener;

import java.util.EventListener;

import com.stabilit.scm.common.log.Level;

/**
 * The Class LoggerPoint. Allows logging - fire debug/info/warn/exception/error/log/trace.
 */
public final class LoggerPoint extends ListenerSupport<ILoggerListener> {

	/** The logger point. */
	private static LoggerPoint loggerPoint = new LoggerPoint();
	
	/** The level of logging. */
	private Level level = null;

	/**
	 * Instantiates a new logger point.
	 */
	private LoggerPoint() {
		this.level = Level.ERROR;
	}
	
	/**
	 * Gets the single instance of LoggerPoint.
	 * 
	 * @return single instance of LoggerPoint
	 */
	public static LoggerPoint getInstance() {
		return loggerPoint;
	}

	/**
	 * Sets the level.
	 * 
	 * @param level the new level
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	public void fireFatal(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.FATAL);
			LoggerPoint.getInstance().fireLog(loggerEvent);
		}
	}

	/**
	 * Fire exception.
	 * 
	 * @param source the source
	 * @param text the text
	 */
	public void fireException(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.ERROR);
			LoggerPoint.getInstance().fireLog(loggerEvent);
		}
	}

	/**
	 * Fire warn.
	 * 
	 * @param source the source
	 * @param text the text
	 */
	public void fireWarn(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.WARN);
			LoggerPoint.getInstance().fireLog(loggerEvent);
		}
	}

	/**
	 * Fire info.
	 * 
	 * @param source the source
	 * @param text the text
	 */
	public void fireInfo(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.INFO);
			LoggerPoint.getInstance().fireLog(loggerEvent);
		}
	}

	/**
	 * Fire debug.
	 * 
	 * @param source the source
	 * @param text the text
	 */
	public void fireDebug(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.DEBUG);
			LoggerPoint.getInstance().fireLog(loggerEvent);
		}
	}

	/**
	 * Fire log.
	 * 
	 * @param loggerEvent the logger event
	 */
	private void fireLog(LoggerEvent loggerEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ILoggerListener loggerListener = (ILoggerListener) localArray[i];
				loggerListener.logEvent(loggerEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks if is error.
	 * 
	 * @return true, if is error
	 */
	public boolean isError() {
		return this.level.supportsLevel(Level.FATAL);
	}

	/**
	 * Checks if is exception.
	 * 
	 * @return true, if is exception
	 */
	public boolean isException() {
		return this.level.supportsLevel(Level.ERROR);
	}

	/**
	 * Checks if is warn.
	 * 
	 * @return true, if is warn
	 */
	public boolean isWarn() {
		return this.level.supportsLevel(Level.WARN);
	}

	/**
	 * Checks if is info.
	 * 
	 * @return true, if is info
	 */
	public boolean isInfo() {
		return this.level.supportsLevel(Level.INFO);
	}

	/**
	 * Checks if is debug.
	 * 
	 * @return true, if is debug
	 */
	public boolean isDebug() {
		return this.level.supportsLevel(Level.DEBUG);
	}
}
