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
package com.stabilit.sc.listener;

import java.util.EventListener;

import com.stabilit.sc.log.Level;

public class LoggerListenerSupport extends ListenerSupport<ILoggerListener> {

	private static LoggerListenerSupport loggerListenerSupport = new LoggerListenerSupport();

	private Level level = null;
	
	private LoggerListenerSupport() {
		this.level = Level.EXCEPTION;
	}

	public static LoggerListenerSupport getInstance() {
		return loggerListenerSupport;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	public static void fireError(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.ERROR);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public static void fireException(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.EXCEPTION);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public static void fireWarn(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.WARN);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public static void fireInfo(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.INFO);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public static void fireDebug(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.DEBUG);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public static void fireTrace(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			LoggerEvent loggerEvent = new LoggerEvent(source, text, Level.TRACE);
			LoggerListenerSupport.getInstance().fireLog(loggerEvent);
		}
	}

	public void fireLog(LoggerEvent loggerEvent) {
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

	public boolean isError() {
        return this.level.supportsLevel(Level.ERROR);
	}

	public boolean isException() {
        return this.level.supportsLevel(Level.EXCEPTION);
	}

	public boolean isWarn() {
        return this.level.supportsLevel(Level.WARN);
	}

	public boolean isInfo() {
        return this.level.supportsLevel(Level.INFO);
	}

	public boolean isDebug() {
        return this.level.supportsLevel(Level.DEBUG);
	}
	
	public boolean isTrace() {
        return this.level.supportsLevel(Level.TRACE);
	}

}
