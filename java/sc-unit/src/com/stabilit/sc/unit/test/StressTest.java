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
package com.stabilit.sc.unit.test;

import org.junit.Test;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.call.SCMPConnectCall;
import com.stabilit.sc.cln.call.SCMPDisconnectCall;
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Class StressTest.
 */
public class StressTest extends SuperTestCase {

	/**
	 * Instantiates a new stress test.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public StressTest(String fileName) {
		super(fileName);
	}

	/**
	 * Connect disconnect. Test to find the limit of available sockets in operating system by connecting and
	 * disconnecting client from SC.
	 */
	// @Test
	public void connectDisconnect() {
		for (int i = 0; i < 10000; i++) {
			try {
				SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
				connectCall.setVersion("1.0-00");
				connectCall.setCompression(false);
				connectCall.setKeepAliveTimeout(30);
				connectCall.setKeepAliveInterval(360);
				SCMPMessage result = connectCall.invoke();
				SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
						.newInstance(client);
				disconnectCall.invoke();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the delete session. Test to find the limit of available sockets in operating system by creating and
	 * deleting session from Client to SC. Creating / deleting session causes connect / disconnect between SC and
	 * backend server.
	 */
	@Test
	public void createDeleteSession() {
		try {
			SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
			connectCall.setVersion("1.0-00");
			connectCall.setCompression(false);
			connectCall.setKeepAliveTimeout(30);
			connectCall.setKeepAliveInterval(360);
			SCMPMessage result = connectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 10000; i++) {
				SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
						.newInstance(client);
				createSessionCall.setServiceName("simulation");
				createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
				SCMPSession scmpSession = createSessionCall.invoke();

				SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
						.newInstance(client, scmpSession);
				deleteSessionCall.invoke();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
					.newInstance(client);
			disconnectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
