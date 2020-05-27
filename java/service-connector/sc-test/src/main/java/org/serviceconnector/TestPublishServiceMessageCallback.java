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

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPublishServiceMessageCallback extends SCMessageCallback {

	public static int receivedMsg;
	public static int lastNumber = -1;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPublishServiceMessageCallback.class);

	public TestPublishServiceMessageCallback(SCPublishService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
		synchronized (this) {
			receivedMsg++;
			String responseString = ((String) reply.getData());
			LOGGER.debug("Publish client sid=" + this.service.getSessionId() + " received=" + reply + " body=" + reply.getData());
			String number = responseString.substring(responseString.indexOf(":") + 1);
			int currentNumber = Integer.valueOf(number);
			if (currentNumber != lastNumber + 1) {
				LOGGER.debug("Publish client sid=" + this.service.getSessionId() + " received messages not in sequence wrong NR=" + currentNumber);
			}
			lastNumber = currentNumber;
		}
	}

	@Override
	public void receive(Exception ex) {
		synchronized (this) {
			receivedMsg++;
			LOGGER.error("Publish client sid=" + this.service.getSessionId() + " received=" + ex);
		}
	}
}
