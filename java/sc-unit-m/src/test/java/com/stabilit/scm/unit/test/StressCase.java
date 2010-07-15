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
package com.stabilit.scm.unit.test;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPAttachCall;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDetachCall;
import com.stabilit.scm.common.scmp.SCMPMessage;

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
	 * Attach detach. Test to find the limit of available sockets in operating system by connecting and disconnecting
	 * client from SC.
	 */
	@Test
	public void connectDisconnect() {
		for (int i = 0; i < 10000; i++) {
			try {
				SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);
				attachCall.setKeepAliveInterval(360);
				SCMPMessage result = attachCall.invoke();
				SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
				detachCall.invoke();
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
			SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);
			attachCall.setKeepAliveInterval(360);
			SCMPMessage result = attachCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 10000; i++) {
//				ISCSession localSession = new SCDataSession("simulation", req);
//				localSession.setMessageInfo("messageInfo");
//				localSession.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
//				localSession.createSession();
//				localSession.deleteSession();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
			detachCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
