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
package com.stabilit.sc.unit.techConnect;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.common.SCVersion;
import com.stabilit.sc.common.net.req.ConnectionContext;
import com.stabilit.sc.common.net.req.ConnectionFactory;
import com.stabilit.sc.common.net.req.IConnection;
import com.stabilit.sc.common.net.req.IConnectionContext;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMessage;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.util.DateTimeUtility;
import com.stabilit.sc.common.util.SynchronousCallback;
import com.stabilit.sc.srv.IIdleCallback;
import com.stabilit.sc.unit.SetupTestCases;

public class NettyConnectTestCase {

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupSCOverFile("scPerf.properties");
	}

	@Test
	public void connectDisconnect50000() throws Exception {
		IConnection connection = ConnectionFactory.getCurrentInstance().newInstance("netty.http");
		connection.setHost("localhost");
		connection.setPort(8080);
		connection.setIdleTimeout(0);
		IIdleCallback idleCallback = new IdleCallback();
		IConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
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
			IConnection connection = ConnectionFactory.getCurrentInstance().newInstance("netty.http");
			connections[i] = connection;
			connection.setHost("localhost");
			connection.setPort(8080);
			connection.setIdleTimeout(0);
			IIdleCallback idleCallback = new IdleCallback();
			IConnectionContext connectionContext = new ConnectionContext(connection, idleCallback, 0);
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

	private class IdleCallback implements IIdleCallback {

		/** {@inheritDoc} */
		@Override
		public void connectionIdle(IConnection connection) {
		}
	}
}
