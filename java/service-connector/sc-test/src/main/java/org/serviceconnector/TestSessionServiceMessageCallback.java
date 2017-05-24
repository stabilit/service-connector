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
package org.serviceconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCSessionService;

public class TestSessionServiceMessageCallback extends SCMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestSessionServiceMessageCallback.class);

	public TestSessionServiceMessageCallback(SCSessionService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
		LOGGER.info("Session client received=" + reply.getData());
	}

	@Override
	public void receive(Exception ex) {
		LOGGER.info("Session client received=" + ex);
	}
}
