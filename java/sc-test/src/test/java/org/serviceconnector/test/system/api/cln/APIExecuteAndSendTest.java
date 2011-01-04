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
package org.serviceconnector.test.system.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIExecuteAndSendTest extends APISystemSuperSessionClientTest {

	/**
	 * Description: exchange one uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t001_executeUncompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange one compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t002_executeCompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(true);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange one 1MB uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t003_executeLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(new SCMessage(), msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange one 1MB compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t004_executeLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(true);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(new SCMessage(), msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange one message, return APP error<br>
	 * Expectation: passes
	 */
	@Test
	public void t005_executeAPPError() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, response.getAppErrorText());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange message with body = new Object<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t006_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData(new Object());
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t007_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo("");
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t008_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo(" ");
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t009_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo(TestConstants.stringLength257);
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t010_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId("");
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t011_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId(" ");
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t012_execute() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId(TestConstants.stringLength257);
		response = sessionService.execute(request);
	}

	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t010_disabledService() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// execute
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: exchange before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t020_executeBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService.execute(request);
	}

	/**
	 * Description: screw up sessionId after create session but before message exchange<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t030_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		String sessionId = sessionService.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		response = sessionService.execute(request);
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: continue message exchange after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t040_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);

		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		msgCallback = new MsgCallback(sessionService);
		try {
			response = sessionService.createSession(request, msgCallback);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService.execute(request);
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t050_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		response = sessionService.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t051_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = sessionService.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms,
			Thread.sleep(5000); // wait 5000ms to allow server sleep request completion
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediatelly<br>
	 * Expectation: passes
	 */
	@Test
	public void t052_operationTimeout() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = sessionService.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms
			// continue immediatelly
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t101_sendUncompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		sessionService.send(request);
		msgCallback.waitForMessage(10);
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send 1 compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t102_sendCompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(true);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService.send(request);
		msgCallback.waitForMessage(10);
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed 10MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t103_sendLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(new SCMessage(), msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService.send(request);
		msgCallback.waitForMessage(20);
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send 1 compressed 10MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t104_sendLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(true);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(new SCMessage(), msgCallback);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService.send(request);
		msgCallback.waitForMessage(20);
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send message before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t120_sendBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		sessionService.send(request);
	}

	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t110_disabledService() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// send
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService.send(request);
		msgCallback.waitForMessage(10);
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: send message after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t140_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		msgCallback = new MsgCallback(sessionService);
		try {
			response = sessionService.createSession(request, msgCallback);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService.send(request);
	}

	/**
	 * Description: screw up sessionId before message send<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t130_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		String sessionId = sessionService.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		sessionService.send(request);
		msgCallback.waitForMessage(10);
		response = msgCallback.getResponse();
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: passes, gets back a fault response
	 */
	@Test
	public void t150_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback.waitForMessage(10);
		response = msgCallback.getResponse();
		// TODO TRN check SC error
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t151_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback.getResponse();
		Thread.sleep(4000); // wait 4000ms to allow server sleep request completion

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo"); // send second message
		request.setDataLength(((String) request.getData()).length());
		sessionService.send(request);
		msgCallback.waitForMessage(10); // will wait max 10 seconds for the second response
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediately<br>
	 * Expectation: passes
	 */
	@Test
	public void t152_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback = new MsgCallback(sessionService);
		response = sessionService.createSession(request, msgCallback);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback.getResponse();

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("gaga");
		request.setDataLength(((String) request.getData()).length());
		sessionService.send(request);
		msgCallback.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());

		// third message (synchronous)
		request.setData("abraka-dabra");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService.deleteSession();
	}
}
