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
package org.serviceconnector.test.sc.nettyConnect;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.SCVersion;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IIdleConnectionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SetupTestCases;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.SynchronousCallback;

public class NettyConnectTestCase {

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupSCOverFile("scPerf.properties");
	}

	@Test
	public void connectDisconnect50000() throws Exception {
		ConnectionFactory connectionFactory = AppContext.getCurrentContext().getConnectionFactory();
		IConnection connection = connectionFactory.createConnection("netty.http");
		connection.setHost(TestConstants.HOST);
		connection.setPort(TestConstants.PORT_HTTP);
		connection.setIdleTimeout(0);
		IIdleConnectionCallback idleCallback = new IdleCallback();
		ConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
		connection.setContext(connectionContext);
		String ldt = DateTimeUtility.getCurrentTimeZoneMillis();

		for (int i = 0; i < 50000; i++) {
			connection.connect();
			SCMPMessage message = new SCMPMessage();
			message.setMessageType(SCMPMsgType.ATTACH);
			message.setHeader(SCMPHeaderAttributeKey.SC_VERSION, SCVersion.CURRENT.toString());
			message.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, ldt);
			ConnectCallback callback = new ConnectCallback();
			connection.send(message, callback);
			callback.getMessageSync();
			connection.disconnect();
			if (i % 1000 == 0) {
				System.out.println("connection nr " + i + " is done!");
			}
		}
	}

	@Test
	public void connect1000WithoutDisconnect() throws Exception {
		IConnection[] connections = new IConnection[1000];
		for (int i = 0; i < 1000; i++) {
			ConnectionFactory connectionFactory = AppContext.getCurrentContext().getConnectionFactory();
			IConnection connection = connectionFactory.createConnection("netty.http");
			connections[i] = connection;
			connection.setHost(TestConstants.HOST);
			connection.setPort(TestConstants.PORT_HTTP);
			connection.setIdleTimeout(0);
			IIdleConnectionCallback idleCallback = new IdleCallback();
			ConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
			connection.setContext(connectionContext);
			String ldt = DateTimeUtility.getCurrentTimeZoneMillis();

			connection.connect();
			SCMPMessage message = new SCMPMessage();
			message.setMessageType(SCMPMsgType.ATTACH);
			message.setHeader(SCMPHeaderAttributeKey.SC_VERSION, SCVersion.CURRENT.toString());
			message.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, ldt);
			ConnectCallback callback = new ConnectCallback();
			connection.send(message, callback);
			callback.getMessageSync();
			if (i % 100 == 0) {
				System.out.println("connection nr " + i + " is done!");
			}
		}
		for (int i = 0; i < 1000; i++) {
			connections[i].disconnect();
		}
	}

	private class ConnectCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}

	private class IdleCallback implements IIdleConnectionCallback {

		/** {@inheritDoc} */
		@Override
		public void connectionIdle(IConnection connection) {
		}
	}
}
