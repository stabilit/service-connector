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

/**
 * The Class ConnectionTest. <br />
 * <br />
 * Check following keys in windows registry in case of an error: <br/>
 * <br/>
 * Browse to the HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters keys.<br />
 * <br />
 * MaxUserPort: This REG_DWORD parameter controls the maximum port number that is used when a program requests any available user
 * port
 * from the server. Typically, ephemeral (short-lived) ports are allocated between the values of 1024 and 5000 inclusive.<br />
 * <br />
 * TcpNumConnections: This REG_DWORD parameter limits the maximum number of connections that TCP can have open at the same time.<br />
 * <br />
 * Valid examples: MaxUserPort 60000, TcpNumConnections 60000
 * More Information: {@link http
 * ://publib.boulder.ibm.com/infocenter/iisinfsv/v8r1/index.jsp?topic=/com.ibm.swg.im.iis.productization
 * .iisinfsv.install.doc/topics/wsisinst_config_winregtcpip.html}
 */
public class ConnectionTest extends IntegrationSuperTest {

	/**
	 * Description: connect, send message and disconnect - 50'000 times the same connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ConnectSendAndDisconnect50000() throws Exception {
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

		for (int i = 0; i < 50000; i++) {
			connection.connect();
			TestCallback cbk = new TestCallback();
			connection.send(message, cbk);
			TestUtil.checkReply(cbk.getMessageSync(3000));
			connection.disconnect();
			if ((i + 1) % 1000 == 0) {
				testLogger.info("connection nr " + (i + 1) + "...");
			}
		}
	}

	/**
	 * Description: create 1000 connections, connect, send a message and disconnect them all<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_Connect1000SendAndDisconnect() throws Exception {
		int numberOfConnections = 1000;
		IConnection[] connections = new IConnection[numberOfConnections];

		String ldt = DateTimeUtility.getCurrentTimeZoneMillis();

		SCMPMessage message = new SCMPMessage(SCMPVersion.CURRENT);
		message.setMessageType(SCMPMsgType.ATTACH);
		message.setHeader(SCMPHeaderAttributeKey.SC_VERSION, SCVersion.CURRENT.toString());
		message.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, ldt);

		for (int i = 0; i < numberOfConnections; i++) {
			ConnectionFactory connectionFactory = AppContext.getConnectionFactory();
			IConnection connection = connectionFactory.createConnection(ConnectionType.NETTY_HTTP.getValue());
			connections[i] = connection;
			connection.setHost(TestConstants.HOST);
			connection.setPort(TestConstants.PORT_SC0_HTTP);
			connection.setIdleTimeoutSeconds(0);
			IIdleConnectionCallback idleCallback = new IdleCallback();
			ConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
			connection.setContext(connectionContext);
			connection.connect();
			TestCallback cbk = new TestCallback();
			connection.send(message, cbk);
			TestUtil.checkReply(cbk.getMessageSync(3000));
			if ((i + 1) % 100 == 0) {
				testLogger.info("connection nr " + (i + 1) + "...");
			}
		}
		for (int i = 0; i < numberOfConnections; i++) {
			connections[i].disconnect();
		}
	}

	/**
	 * The Class IdleCallback.
	 */
	private class IdleCallback implements IIdleConnectionCallback {
		/** {@inheritDoc} */
		@Override
		public void run() {
		}
	}
}
