/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.unit.concurrency;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.unit.test.mt.MTSuperTestCase;

/**
 * @author JTraber
 *
 */
public class CreateSessionConccurrent extends MTSuperTestCase{

	private IRequester requester;
	
	/**
	 * @param fileName
	 */
	public CreateSessionConccurrent(String fileName) {
		super(fileName);
	}
	
	
	@Test
	public void createSessionConcurrent() throws Exception {
		Map<CreateSessionConccurrent, Thread> map = new HashMap<CreateSessionConccurrent, Thread>();

		for (int i = 0; i < 3; i++) {
			CreateSessionConccurrent createSessionConccurrent = new CreateSessionConccurrent(fileName);
			createSessionConccurrent.setReq(this.newReq());
			createSessionConccurrent.clnAttachBefore();
			Thread th = new MTClientThread(createSessionConccurrent, "createSession");
			th.start();
			map.put(createSessionConccurrent, th);
		}

		for (CreateSessionConccurrent createSessionConccurrent : map.keySet()) {
			map.get(createSessionConccurrent).join();
		}
	}
	
	public void createSession() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(requester, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		// create session and keep sessionId
		SCMPMessage resp = createSessionCall.invoke();
	}
	
	public void clnAttachBefore() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(requester);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		attachCall.invoke();
	}
	
	/**
	 * @param requester the requester to set
	 */
	public void setReq(IRequester requester) {
		this.requester = requester;
	}	
}
