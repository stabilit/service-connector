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
package com.stabilit.scm.unit;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.netty.http.NettyHttpConnection;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.net.res.netty.http.NettyHttpEndpoint;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.cmd.factory.impl.ServiceConnectorCommandFactory;

/**
 * @author JTraber
 */
public class Performance {

	public static void main(String[] args) {
		Performance test = new Performance();
		ServiceConnectorCommandFactory serviceCommandFactory = new ServiceConnectorCommandFactory();
		CommandFactory.setCurrentCommandFactory(serviceCommandFactory);
		try {
			test.startListening();
			test.startSending();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startListening() throws Exception {
		Responder resp = new Responder();
		resp.setResponderConfig(new CommunicatorConfig("", "localhost", 8080, "netty.http", 16, 100, 60, 10));
		resp.create();
		resp.runAsync();

		NettyHttpEndpoint endpoint = new NettyHttpEndpoint();
		endpoint.setHost("localhost");
		endpoint.setPort(8080);
		endpoint.setNumberOfThreads(16);

		endpoint.create();
		endpoint.runAsync();
	}

	public void startSending() throws Exception {
		NettyHttpConnection con = new NettyHttpConnection();
		con.setHost("localhost");
		con.setNumberOfThreads(10);
		con.setPort(8080);

		IContext testContext = new TestContext("localhost", 8080, "netty.http");

		IRequester req = new Requester(testContext);
		ICommunicatorConfig config = new CommunicatorConfig("Performance", "localhost", 8080, "netty.http", 16, 1000,
				60, 10);
		SCMPMessage request = null;
		SCMPMessage resp = null;

		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);
		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		SCMPMessage result = attachCall.invoke();

		// ISessionService session = new SCDataSession("simulation", req);
		// session.setMessageInfo("message info");
		// session.setSessionInfo("session info");
		// session.createSession();

		con.connect();
		double anzMsg = 100000;
		byte[] buffer = new byte[128];

		double startTime = System.currentTimeMillis();
		for (int i = 0; i < anzMsg; i++) {
			request = new SCMPMessage(buffer);
			request.setMessageType(SCMPMsgType.ECHO_SC.getName());
			// request.setSessionId(session.getSessionId());
			request.setHeader(SCMPHeaderAttributeKey.MAX_NODES, 2);
			resp = con.sendAndReceive(request);
		}
		double endTime = System.currentTimeMillis();

		double neededTime = endTime - startTime;
		System.out.println("Performance Test");
		System.out.println("Anz msg pro sec: " + anzMsg / ((neededTime / 1000)));

		con.disconnect();
	}
}
