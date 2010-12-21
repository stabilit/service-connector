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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.SCVersion;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IIdleConnectionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.DateTimeUtility;

public class ConnectionTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ConnectionTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: Gets a connection send a message and frees it - 50'000 times the same connection<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ConnectSendAndDisconnect50000() throws Exception {
		ConnectionFactory connectionFactory = AppContext.getConnectionFactory();
		IConnection connection = connectionFactory.createConnection(ConnectionType.NETTY_HTTP.getValue());
		connection.setHost(TestConstants.HOST);
		connection.setPort(TestConstants.PORT_HTTP);
		connection.setIdleTimeoutSeconds(0); // idle timeout inactive
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
			TestCallback cbk = new TestCallback();
			connection.send(message, cbk);
			TestUtil.checkReply(cbk.getMessageSync(3000));
			connection.disconnect();
			if (i % 1000 == 0) {
				testLogger.info("connection nr " + i + " is done!");
			}
		}
	}

	/**
	 * Description: Gets a connection send a message 1000 times - after all disconnect the 500 connections<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_Connect500SendAndDisconnect() throws Exception {
		int numberOfConnections = 500;
		IConnection[] connections = new IConnection[numberOfConnections];
		for (int i = 0; i < numberOfConnections; i++) {
			ConnectionFactory connectionFactory = AppContext.getConnectionFactory();
			IConnection connection = connectionFactory.createConnection(ConnectionType.NETTY_HTTP.getValue());
			connections[i] = connection;
			connection.setHost(TestConstants.HOST);
			connection.setPort(TestConstants.PORT_HTTP);
			connection.setIdleTimeoutSeconds(0);
			IIdleConnectionCallback idleCallback = new IdleCallback();
			ConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
			connection.setContext(connectionContext);
			String ldt = DateTimeUtility.getCurrentTimeZoneMillis();

			connection.connect();
			SCMPMessage message = new SCMPMessage();
			message.setMessageType(SCMPMsgType.ATTACH);
			message.setHeader(SCMPHeaderAttributeKey.SC_VERSION, SCVersion.CURRENT.toString());
			message.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, ldt);
			TestCallback cbk = new TestCallback();
			connection.send(message, cbk);
			TestUtil.checkReply(cbk.getMessageSync(3000));
			if (i % 100 == 0) {
				testLogger.info("connection nr " + i + " is done!");
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
		public void connectionIdle(IConnection connection) {
		}
	}
}
