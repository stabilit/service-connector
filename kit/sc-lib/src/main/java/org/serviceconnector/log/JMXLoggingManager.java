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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;


/**
 * The Class JMXLoggingManager. Provides access for controlling logging over JMX console.
 * 
 * @author JTraber
 */
public class JMXLoggingManager implements ILoggingManagerMXBean{

	/**
	 * Sets the connection logger level.
	 * 
	 * @param level
	 *            the new connection logger level
	 */
	public void setConnectionLoggerLevel(String levelValue) {
		Level level = JMXLoggingManager.convertLevelValue(levelValue);
		LogManager.getLogger("ConnectionLogger").setLevel(level);
	}

	/**
	 * Gets the connection logger level.
	 * 
	 * @return the connection logger level
	 */
	public String getConnectionLoggerLevel() {
		return LogManager.getLogger("ConnectionLogger").getLevel().toString();
	}

	/**
	 * Convert level value.
	 * 
	 * @param levelValue
	 *            the level value
	 * @return the level
	 */
	public static Level convertLevelValue(String levelValue) {
		Level level = null;

		if ("DEBUG".equals(levelValue)) {
			level = Level.DEBUG;
		} else if ("INFO".equals(levelValue)) {
			level = Level.INFO;
		} else if ("WARN".equals(levelValue)) {
			level = Level.WARN;
		} else if ("ERROR".equals(levelValue)) {
			level = Level.ERROR;
		} else if ("FATAL".equals(levelValue)) {
			level = Level.FATAL;
		} else if ("TRACE".equals(levelValue)) {
			level = Level.TRACE;
		} else if ("OFF".equals(levelValue)) {
			level = Level.OFF;
		} else {
			return Level.INFO;
		}
		return level;
	}
}