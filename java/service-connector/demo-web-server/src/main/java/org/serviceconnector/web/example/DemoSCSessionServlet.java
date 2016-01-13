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
package org.serviceconnector.web.example;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.web.SCBaseSessionServlet;

public class DemoSCSessionServlet extends SCBaseSessionServlet {

	private static final long serialVersionUID = 1L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DemoSCSessionServlet.class);

	public DemoSCSessionServlet() {
		super("/demo-web-server/DemoSCSessionServlet");
	}

	@Override
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session created");
		return message;
	}

	@Override
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session deleted");
	}

	@Override
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session aborted");
	}

	public void exceptionCaught(SCServiceException ex) {
		LOGGER.error("exception caught");
	}

	@Override
	public SCMessage execute(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("execute");
		if (message.getCacheId() != null) {
			Calendar time = Calendar.getInstance();
			time.add(Calendar.HOUR_OF_DAY, 1);
			message.setCacheExpirationDateTime(time.getTime());
		}
		return message;
	}
}
