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
package com.stabilit.scm.unit.test.session;

import org.junit.After;
import org.junit.Before;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

/**
 * @author JTraber
 */
public abstract class SuperSessionTestCase extends SuperAttachTestCase {

	protected String sessionId = null;
	protected SuperSessionCallback sessionCallback;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SuperSessionTestCase(String fileName) {
		super(fileName);
		this.sessionCallback = new SuperSessionCallback();
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
		createSessionCall.invoke(this.sessionCallback, 3);
		SCMPMessage resp = this.sessionCallback.getMessageSync();
		this.sessionId = resp.getSessionId();
	}

	public void clnDeleteSessionAfter() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, "simulation", this.sessionId);
		deleteSessionCall.invoke(this.sessionCallback, 3);
		this.sessionCallback.getMessageSync();
	}

	protected class SuperSessionCallback extends SynchronousCallback {
	}
}