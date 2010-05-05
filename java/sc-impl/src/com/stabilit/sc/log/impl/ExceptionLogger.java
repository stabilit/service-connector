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
package com.stabilit.sc.log.impl;

import java.io.IOException;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionEvent;
import com.stabilit.sc.listener.IExceptionListener;
import com.stabilit.sc.log.SimpleLogger;

public class ExceptionLogger extends SimpleLogger implements IExceptionListener {

	public ExceptionLogger() throws Exception {
		this("log/", "exc.log");
	}

	public ExceptionLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	public void log(byte[] buffer, int offset, int length) throws IOException {
		super.log(buffer, offset,length);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	@Override
	public synchronized void exceptionEvent(ExceptionEvent exceptionEvent) {
		try {
			this.log(exceptionEvent.getThrowable());
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
