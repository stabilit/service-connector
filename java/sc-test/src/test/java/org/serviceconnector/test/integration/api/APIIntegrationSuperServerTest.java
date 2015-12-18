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
package org.serviceconnector.test.integration.api;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.test.integration.IntegrationSuperTest;

public class APIIntegrationSuperServerTest extends IntegrationSuperTest {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(APIIntegrationSuperServerTest.class);
	
	protected SCServer server;
	protected SCSessionServer sessionServer = null;
	protected SCPublishServer publishServer = null;
	
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			if (publishServer != null) publishServer.deregister();
		} catch (Exception e) {}
		publishServer = null;
		try {
			if (sessionServer != null) sessionServer.deregister();
		} catch (Exception e) {}
		sessionServer = null;
		try {
			server.stopListener();
		} catch (Exception e) {}
		try {
			server.destroy();
		} catch (Exception e) {}

		server = null;
		super.afterOneTest();
	}
	
	protected class SesSrvCallback extends SCSessionServerCallback {
		public SesSrvCallback(SCSessionServer server) {
			super(server);
		}
		@Override
		public SCMessage createSession(SCMessage request, int operationTimeoutMillis) {
			return request;
		}

		@Override
		public void deleteSession(SCMessage request, int operationTimeoutMillis) {
		}

		@Override
		public void abortSession(SCMessage request, int operationTimeoutMillis) {
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutMillis) {
			return request;
		}
		@Override
		public void exceptionCaught(SCServiceException ex) {			
		}
	}

	protected class PubSrvCallback extends SCPublishServerCallback {

		public PubSrvCallback(SCPublishServer server) {
			super(server);
		}
		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		}
		@Override
		public void exceptionCaught(SCServiceException ex) {		
		}
	}
}
