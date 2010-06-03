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
import com.stabilit.sc.cln.call.SCMPAttachCall;
import com.stabilit.sc.cln.call.SCMPDetachCall;
import com.stabilit.sc.cln.scmp.SCMPClientSession;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Class StressTest.
 */
public class StressCase extends SuperTestCase {

	/**
	 * Instantiates a new stress test.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public StressCase(String fileName) {
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
				SCMPAttachCall connectCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);
				connectCall.setCompression(false);
				connectCall.setKeepAliveTimeout(30);
				connectCall.setKeepAliveInterval(360);
				SCMPMessage result = connectCall.invoke();
				SCMPDetachCall disconnectCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL
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
			SCMPAttachCall connectCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);
			connectCall.setCompression(false);
			connectCall.setKeepAliveTimeout(30);
			connectCall.setKeepAliveInterval(360);
			SCMPMessage result = connectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 10000; i++) {
				SCMPClientSession localSession = new SCMPClientSession(client);
				localSession.setServiceName("simulation");
				localSession.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
				localSession.createSession();
				localSession.deleteSession();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SCMPDetachCall disconnectCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL
					.newInstance(client);
			disconnectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
