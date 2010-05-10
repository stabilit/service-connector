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
import com.stabilit.sc.log.SimpleLogger;
import com.stabilit.sc.scmp.SCMP;

/**
 * The Class SCMPLogger. Provides functionality of logging an <code>SCMP</code>.
 * 
 * @author JTraber
 */
public class SCMPLogger extends SimpleLogger {

	/**
	 * Instantiates a new sCMP logger.
	 * 
	 * @param dir
	 *            the dir
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public SCMPLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	/**
	 * Log.
	 * 
	 * @param scmp
	 *            the scmp
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void log(SCMP scmp) throws IOException {
		super.log(scmp.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}
}