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
package com.stabilit.scm.examples;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.config.ClientConfig;
import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.cln.req.RequesterFactory;
import com.stabilit.scm.cln.scmp.SCMPClientSession;
import com.stabilit.scm.scmp.SCMPMessage;


/**
 * @author JTraber
 */
public class SCApiExample {

	private String fileName;
	private ClientConfig config = null;
	private IRequester client = null;
	private SCMPClientSession scmpSession = null;

	public void runExample() throws Exception {

		config = new ClientConfig();
		config.load(fileName);
		RequesterFactory clientFactory = new RequesterFactory();
		client = clientFactory.newInstance(config.getClientConfig());
		client.connect(); // physical connect

		// attach
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);
		attachCall.setCompression(false);
		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		attachCall.invoke();

		this.scmpSession = new SCMPClientSession(client, "simulation", "Session Info");
		this.scmpSession.createSession();

		// data call - session is stored inside client!!
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client);
		clnDataCall.setMessagInfo("message info");
		clnDataCall.setRequestBody("hello world body!");
		SCMPMessage scmpReply = clnDataCall.invoke();

		// response
		System.out.println(scmpReply.getBody());

		this.scmpSession.deleteSession();

		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(client);
		detachCall.invoke();

		client.disconnect(); // physical disconnect
		client.destroy();
	}
}
