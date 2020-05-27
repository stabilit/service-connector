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

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.web.SCBasePublishServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoSCPublishServlet extends SCBasePublishServlet {

	private static final long serialVersionUID = 1L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoSCPublishServlet.class);

	public DemoSCPublishServlet() {
		super("/demo-web-server/DemoSCPublishServlet");
	}

	@Override
	public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription created");
		PublishThread publish = new PublishThread();
		publish.start();
		return message;
	}

	@Override
	public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription changed");
		return message;
	}

	@Override
	public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription deleted");
	}

	@Override
	public void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis) {
		LOGGER.info("Subscription aborted");
	}

	@Override
	public void exceptionCaught(SCServiceException ex) {
		LOGGER.error("exception caught");
	}

	private class PublishThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				SCPublishMessage pubMessage = new SCPublishMessage();
				for (int i = 0; i < 5; i++) {
					pubMessage.setData("publish message nr : " + i);
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					DemoSCPublishServlet.this.publish(pubMessage);
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				LOGGER.warn("publish failed");
			}
		}
	}
}
