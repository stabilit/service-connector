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
package org.serviceconnector.test.integration;

import org.junit.Test;
import org.serviceconnector.SCVersion;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IIdleConnectionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.util.DateTimeUtility;

public class SendTest extends IntegrationSuperTest {

	/**
	 * Description: connect, send 50000 message and disconnect<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_Send50000() throws Exception {
		ConnectionFactory connectionFactory = AppContext.getConnectionFactory();
		IConnection connection = connectionFactory.createConnection(ConnectionType.NETTY_HTTP.getValue());
		connection.setHost(TestConstants.HOST);
		connection.setPort(TestConstants.PORT_SC0_HTTP);
		connection.setIdleTimeoutSeconds(0); // idle timeout inactive
		IIdleConnectionCallback idleCallback = new IdleCallback();
		ConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
		connection.setContext(connectionContext);
		String ldt = DateTimeUtility.getCurrentTimeZoneMillis();

		SCMPMessage message = new SCMPMessage(SCMPVersion.CURRENT);
		message.setMessageType(SCMPMsgType.ATTACH);
		message.setHeader(SCMPHeaderAttributeKey.SC_VERSION, SCVersion.CURRENT.toString());
		message.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, ldt);
		connection.connect();

		for (int i = 0; i < 50000; i++) {
			TestCallback cbk = new TestCallback();
			connection.send(message, cbk);
			TestUtil.checkReply(cbk.getMessageSync(3000));
			if ((i + 1) % 10000 == 0) {
				testLogger.info("message nr " + (i + 1) + "...");
			}
		}
		connection.disconnect();
	}

	/**
	 * The Class IdleCallback.
	 */
	private class IdleCallback implements IIdleConnectionCallback {
		@Override
		public void run() {
		}
	}
}
