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
package com.stabilit.sc.common.log;

import com.stabilit.sc.common.factory.Factory;

public class LoggerFactory extends Factory {
	private static LoggerFactory loggerFactory = new LoggerFactory();

	private LoggerFactory() {
		ILogger logger;
		try {
			logger = new SCMPLogger("", "scmp.log");
			this.add(SCMPLogger.class, logger);
			logger = new ConnectionLogger("", "con.log");
			this.add(ConnectionLogger.class, logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ILogger getLogger() {
		return (ILogger) this.getInstance(SimpleLogger.class);
	}

	public static LoggerFactory getLoggerFactory() {
		if (loggerFactory == null) {
			loggerFactory = new LoggerFactory();
		}
		return loggerFactory;
	}

	public ILogger getLogger(Object key) {
		return (ILogger) this.factoryMap.get(key);
	}

	public ILogger getConnectionLogger() {
		return (ILogger) this.factoryMap.get(ConnectionLogger.class);
	}

}
