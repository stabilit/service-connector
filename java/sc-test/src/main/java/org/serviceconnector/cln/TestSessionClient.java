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
package org.serviceconnector.cln;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCSessionService;

public class TestSessionClient extends TestAbstractClient {

	private int echoIntervalSeconds;
	private int echoTimeoutSeconds;
	private SCSessionService service;

	static {
		TestAbstractClient.LOGGER = Logger.getLogger(TestSessionClient.class);
	}

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] client name<br>
	 *            [1] SC host<br>
	 *            [2] SC port<br>
	 *            [3] connectionType ("netty.tcp" or "netty.http")<br>
	 *            [4] maxConnections<br>
	 *            [5] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [6] serviceName<br>
	 *            [7] echoIntervalSeconds<br>
	 *            [8] echoTimeoutSeconds<br>
	 *            [9] noDataIntervalSeconds<br>
	 *            [10] methodsToInvoke
	 */
	public static void main(String[] args) throws Exception {
		LOGGER.log(Level.OFF, "TestSessionClient is starting ...");
		for (int i = 0; i < args.length; i++) {
			LOGGER.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestSessionClient testClient = new TestSessionClient();
		testClient.setClientName(args[0]);
		testClient.setHost(args[1]);
		testClient.setPort(Integer.parseInt(args[2]));
		testClient.setConnectionType(args[3]);
		testClient.setMaxConnections(Integer.parseInt(args[4]));
		testClient.setKeepAliveIntervalSeconds(Integer.parseInt(args[5]));
		testClient.setServiceName(args[6]);
		testClient.setEchoIntervalSeconds(Integer.parseInt(args[7]));
		testClient.setEchoTimeoutSeconds(Integer.parseInt(args[8]));
		// args[9] can be ignored in session client (noDataIntervalSeconds)
		testClient.setMethodsToInvoke(Arrays.asList(args[10].split("\\|")));
		testClient.run();
	}

	public void p_createSession() throws Exception {
		service = client.newSessionService(this.serviceName);
		service.setEchoIntervalSeconds(this.echoIntervalSeconds);
		service.setEchoTimeoutSeconds(this.echoTimeoutSeconds);
		service.createSession(new SCMessage(), new TestSessionServiceMessageCallback(service));
	}

	public void p_execute1000() throws Exception {
		for (int i = 0; i < 1000; i++) {
			service.execute(new SCMessage());
		}
	}

	public void p_execute10MBMessage() throws Exception {
		SCMessage message = new SCMessage();
		String string10MB = TestUtil.get10MBString();
		message.setData(string10MB);
		message.setCompressed(false);
		message.setMessageInfo(TestConstants.echoCmd);
		SCMessage response = service.execute(message);
		if (response.getData().equals(string10MB) == false) {
			LOGGER.error("response body not equal request body");
		}
	}

	public void p_execute100000() throws Exception {
		for (int i = 0; i < 100000; i++) {
			service.execute(new SCMessage());
			if (i % 10000 == 0) {
				LOGGER.log(Level.OFF, this.clientName + " sent message number " + i);
			}
		}
	}

	public void p_deleteSession() throws Exception {
		service.deleteSession();
	}

	public void f_execute1000MessagesAndExit() throws Exception {
		this.p_initAttach();
		this.p_createSession();
		this.p_execute1000();
		this.p_deleteSession();
		this.p_detach();
		this.p_exit();
	}

	public void f_execute100000MessagesAndExit() throws Exception {
		this.p_initAttach();
		this.p_createSession();
		this.p_execute100000();
		this.p_deleteSession();
		this.p_detach();
		this.p_exit();
	}

	public void f_execute10MBMessageAndExit() throws Exception {
		this.p_initAttach();
		this.p_createSession();
		this.p_execute10MBMessage();
		this.p_deleteSession();
		this.p_detach();
		this.p_exit();
	}

	public void setEchoIntervalSeconds(int echoIntervalSeconds) {
		this.echoIntervalSeconds = echoIntervalSeconds;
	}

	public void setEchoTimeoutSeconds(int echoTimeoutSeconds) {
		this.echoTimeoutSeconds = echoTimeoutSeconds;
	}
}
