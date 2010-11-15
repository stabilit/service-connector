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
package org.serviceconnector.test.sc.session;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.test.sc.attach.SuperAttachTestCase;
import org.serviceconnector.util.SynchronousCallback;



/**
 * @author JTraber
 */
public abstract class SuperSessionTestCase extends SuperAttachTestCase {

	protected String sessionId = null;
	protected TestSuperSessionCallback sessionCallback;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SuperSessionTestCase(String fileName) {
		super(fileName);
		this.sessionCallback = new TestSuperSessionCallback();
	}

	@Before
	public void setup() throws Exception {
		super.setup();
		clnCreateSessionBefore();
	}

	@After
	public void tearDown() throws Exception {
		clnDeleteSessionAfter();
		super.tearDown();
	}

	public void clnCreateSessionBefore() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoIntervalSeconds(3600);
		// create session and keep sessionId
		createSessionCall.invoke(this.sessionCallback, 1000);
		SCMPMessage resp = this.sessionCallback.getMessageSync();
		this.sessionId = resp.getSessionId();
	}

	public void clnDeleteSessionAfter() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, "simulation", this.sessionId);
		deleteSessionCall.invoke(this.sessionCallback, 1000);
		this.sessionCallback.getMessageSync();
	}

	protected class TestSuperSessionCallback extends SynchronousCallback {
	}
}