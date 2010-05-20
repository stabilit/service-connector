/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.sc.log.impl;

import java.io.IOException;

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.listener.IRuntimeListener;
import com.stabilit.sc.listener.RuntimeEvent;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

public class RuntimeLogger implements IRuntimeListener, ILoggerDecorator {

	private ILogger logger;

	RuntimeLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
	}

	public synchronized void runtimeEvent(RuntimeEvent runtimeEvent) {
		try {
			this.logger.log(runtimeEvent.getText());
			this.logger.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ILoggerDecorator newInstance() {
		return this;
	}
	
	@Override
	public String getLogDir() {
		return IConstants.LOG_DIR;
	}

	@Override
	public String getLogFileName() {
		return IConstants.RUNTIME_LOG_FILE_NAME;
	}
}
