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
package org.serviceconnector.util;

import org.apache.log4j.Logger;

/**
 * The Class TimeoutWrapper. Wraps various Timeouts. An instance of TimeoutWrapper might be hand over to a Executer which runs the
 * Wrapper at the timeout.
 */
public class TimeoutWrapper implements Runnable {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TimeoutWrapper.class);
	/** The target. */
	private ITimeout target;

	/**
	 * Instantiates a new timeout wrapper.
	 * 
	 * @param target
	 *            the target
	 */
	public TimeoutWrapper(ITimeout target) {
		this.target = target;
	}

	/** Time run out, call target. */
	@Override
	public void run() {
		try {
			// call target
			this.target.timeout();
		} catch (Exception e) {
			LOGGER.error("running timeout procedure failed", e);
		}
	}
}
