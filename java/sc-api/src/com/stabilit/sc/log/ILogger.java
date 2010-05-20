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
package com.stabilit.sc.log;

import java.io.IOException;

import com.stabilit.sc.factory.IFactoryable;

/**
 * The Interface ILogger. Abstracts loggers.
 */
public interface ILogger extends IFactoryable {
	/**
	 * Logs an object.
	 * 
	 * @param obj
	 *            the obj to log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(Object obj) throws IOException;

	/**
	 * Logs a buffer.
	 * 
	 * @param buffer
	 *            the buffer to log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(byte[] buffer) throws IOException;

	/**
	 * Logs a buffer.
	 * 
	 * @param buffer
	 *            the buffer to log
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void log(byte[] buffer, int offset, int length) throws IOException;

	/**
	 * Logs a string.
	 * 
	 * @param msg
	 *            the msg to log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(String msg) throws IOException;

	/**
	 * Logs a Throwable.
	 * 
	 * @param t
	 *            the t to log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(Throwable t) throws IOException;

	/**
	 * Log a string.
	 * 
	 * @param level
	 *            the level of logging
	 * @param msg
	 *            the msg to log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(Level level, String msg) throws IOException;

	/**
	 * Log error.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logError(String msg) throws IOException;

	/**
	 * Log warn.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logWarn(String msg) throws IOException;

	/**
	 * Log info.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logInfo(String msg) throws IOException;

	/**
	 * Log debug.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logDebug(String msg) throws IOException;

	/**
	 * Log trace.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logTrace(String msg) throws IOException;

	public ILogger newInstance(ILoggerDecorator loggerDecorator);

	@Override
	public ILogger newInstance();
}
