/*
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
 */
package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;

public class TestSessionClientFilippe extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestSessionClientFilippe.class);

	private String methodName;

	public static void main(String[] args) {
		try {
			TestSessionClientFilippe client = new TestSessionClientFilippe(args[0]);
			client.start();
		} catch (Exception e) {
			logger.error("incorrect parameters", e);
		}
	}

	public TestSessionClientFilippe(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public void run() {
		SCClient client = new SCClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);

		try {
			client.attach();

			if (getMethodName() == "createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo(" ");
				sessionService.createSession(60, scMessage);
				sessionService.deleteSession();

			} else if (getMethodName() == "createSession_arbitrarySpaceSessionInfo_sessionIdIsNotEmpty") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("The quick brown fox jumps over a lazy dog.");
				sessionService.createSession(60, scMessage);
				sessionService.deleteSession();

			} else if (getMethodName() == "createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage("a");
				scMessage.setSessionInfo("The quick brown fox jumps over a lazy dog.");
				sessionService.createSession(10, scMessage);
				sessionService.deleteSession();

			} else if (getMethodName() == "createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
				scMessage.setSessionInfo(TestConstants.stringLength256);
				sessionService.createSession(60, scMessage);
				sessionService.deleteSession();

			} else if (getMethodName() == "deleteSession_beforeCreateSession_noSessionId") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				sessionService.deleteSession();

			} else if (getMethodName() == "deleteSession_afterValidNewSessionService_noSessionId") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(60, scMessage);
				sessionService.deleteSession();

			} else if (getMethodName() == "createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

				try {
					SCMessage scMessage = new SCMessage("reject");
					scMessage.setSessionInfo("sessionInfo");
					sessionService.createSession(60, scMessage);
				} catch (Exception e) {
				}
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(10, scMessage);

				sessionService.execute(new SCMessage());
				sessionService.deleteSession();

			} else if (getMethodName() == "execute_messageData1MBArray_returnsTheSameMessageData") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(60, scMessage);

				SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);
				message.setCompressed(false);

				sessionService.execute(message);
				sessionService.deleteSession();

			} else if (getMethodName() == "createSessionExecuteDeleteSession_twice_6MessagesArrive") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);

				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(60, scMessage);
				sessionService.execute(new SCMessage(new byte[128]));
				sessionService.deleteSession();

				scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(60, scMessage);
				sessionService.execute(new SCMessage(new byte[128]));
				sessionService.deleteSession();

			} else if (getMethodName() == "echo_waitFor3EchoMessages_5MessagesArrive") {
				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
				SCMessage scMessage = new SCMessage();
				scMessage.setSessionInfo("sessionInfo");
				sessionService.createSession(1, scMessage);
				Thread.sleep(6000);
				sessionService.deleteSession();
			}

		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				client.detach();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}
}
