/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln.casc1;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIExecuteAndSendCasc1Test extends APISystemSuperSessionClientTest {

	public APIExecuteAndSendCasc1Test() {
		APISystemSuperSessionClientTest.setUp1CascadedServiceConnectorAndServer();
	}

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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not empty", Constants.EMPTY_APP_ERROR_CODE, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not empty", Constants.EMPTY_APP_ERROR_CODE, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one 1MB uncompressed message, part size 64KB<br>
	 * Expectation: passes
	 */
	@Test
	public void t003_executeLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setPartSize(1 << 16); // 64KB
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not empty", Constants.EMPTY_APP_ERROR_CODE, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one 1MB compressed message, part size 64KB<br>
	 * Expectation: passes
	 */
	@Test
	public void t004_executeLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(true);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setPartSize(1 << 16); // 64KB
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		Assert.assertEquals("appErrorCode is not empty", Constants.EMPTY_APP_ERROR_CODE, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message, return APP error code and text<br>
	 * Expectation: passes
	 */
	@Test
	public void t005_executeAPPError() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message, return APP error code only<br>
	 * Expectation: passes
	 */
	@Test
	public void t006_executeAPPErrorCodeOnly() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppError1Cmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not the same", TestConstants.appErrorCode, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message, return APP error code = 0<br>
	 * Expectation: passes
	 */
	@Test
	public void t007_executeAPPErrorCodeZero() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppError4Cmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not 0", 0, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message, return missing APP error code only<br>
	 * Expectation: passes
	 */
	@Test
	public void t008_executeAPPMissingErrorCodeOnly() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppError3Cmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not missing", Constants.EMPTY_APP_ERROR_CODE, response.getAppErrorCode());
		Assert.assertEquals("appErrorText is not null", null, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange one message, return only APP error text<br>
	 * Expectation: passes
	 */
	@Test
	public void t009_executeAPPErrorTextOnly() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppError2Cmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("appErrorCode is not " + Constants.EMPTY_APP_ERROR_CODE, Constants.EMPTY_APP_ERROR_CODE, response
				.getAppErrorCode());
		Assert.assertEquals("appErrorText is not the same", TestConstants.appErrorText, response.getAppErrorText());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange message with body = new Object<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t010_executeBodyEmptyObject() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData(new Object());
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t011_executeMessageInfoEmty() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo("");
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t012_executeMessageInfoBlank() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo(" ");
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with messageInfo = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t013_executeMessageInfoTooLong() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setMessageInfo(TestConstants.stringLength257);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = ""<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t014_executeCacheIdEmpty() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId("");
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = " "<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t015_executeCacheIdBlank() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId(" ");
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange message with cacheId = 257char<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t016_executeCacheIdTooLong() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		request.setCacheId(TestConstants.stringLength257);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t030_disabledService() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// execute
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: exchange before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t040_executeBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: screw up sessionId after create session but before message exchange<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t050_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setDataLength(TestConstants.pangram.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		String sessionId = sessionService1.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		response = sessionService1.execute(request);
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: continue message exchange after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t060_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);

		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectCmd);
		msgCallback1 = new MsgCallback(sessionService1);
		try {
			response = sessionService1.createSession(request, msgCallback1);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		response = sessionService1.execute(request);
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t070_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		response = sessionService1.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t071_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = sessionService1.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms,
			Thread.sleep(5000); // wait 5000ms to allow server sleep request completion
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediatelly<br>
	 * Expectation: passes
	 */
	@Test
	public void t072_operationTimeout() throws Exception {
		SCMessage request = new SCMessage();
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = sessionService1.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms
			// continue immediatelly
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		sessionService1.send(request);
		msgCallback1.waitForMessage(10);
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService1.send(request);
		msgCallback1.waitForMessage(10);
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed 1MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t103_sendLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService1.send(request);
		msgCallback1.waitForMessage(20);
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: send 1 compressed 1MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t104_sendLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setDataLength(TestConstants.dataLength1MB);
		request.setCompressed(true);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService1.send(request);
		msgCallback1.waitForMessage(20);
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", sessionService1.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: send message before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t120_sendBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		sessionService1.send(request);
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// send
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService1.send(request);
		msgCallback1.waitForMessage(10);
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: send message after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t140_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectCmd);
		msgCallback1 = new MsgCallback(sessionService1);
		try {
			response = sessionService1.createSession(request, msgCallback1);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		sessionService1.send(request);
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		String sessionId = sessionService1.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		sessionService1.send(request);
		msgCallback1.waitForMessage(10);
		response = msgCallback1.getResponse();
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: passes, gets back a fault response
	 */
	@Test
	public void t150_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService1.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback1.waitForMessage(10);
		response = msgCallback1.getResponse();
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
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService1.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback1.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback1.getResponse();
		Thread.sleep(4000); // wait 4000ms to allow server sleep request completion

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo"); // send second message
		request.setDataLength(((String) request.getData()).length());
		sessionService1.send(request);
		msgCallback1.waitForMessage(10); // will wait max 10 seconds for the second response
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediately<br>
	 * Expectation: passes
	 */
	@Test
	public void t152_operationTimeout() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(request, msgCallback1);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		sessionService1.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		msgCallback1.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback1.getResponse();

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("gaga");
		request.setDataLength(((String) request.getData()).length());
		sessionService1.send(request);
		msgCallback1.waitForMessage(10); // will wait max 10 seconds for response
		response = msgCallback1.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());

		// third message (synchronous)
		request.setData("abraka-dabra");
		request.setDataLength(((String) request.getData()).length());
		response = sessionService1.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		sessionService1.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed 20MB message with Constants.MAX_MESSAGE_SIZE parts<br>
	 * Expectation: passes
	 */
	@Test
	public void t155_sendMessageMaxPartSize() throws Exception {
		SCMessage request = new SCMessage();

		String string10MB = TestUtil.get10MBString();
		StringBuilder sb = new StringBuilder();
		sb.append(string10MB);
		sb.append(string10MB);

		request.setData(sb.toString());
		request.setPartSize(Constants.MAX_MESSAGE_SIZE);
		request.setDataLength(sb.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		long startTime = System.currentTimeMillis();
		sessionService1.execute(request);
		System.out.println("Sent string " + sb.length() + " bytes long in " + (System.currentTimeMillis() - startTime) + " millis");
		response = msgCallback1.getResponse();
		sessionService1.deleteSession();
	}
	
	/**
	 * Description: send 1 uncompressed 20MB message 1MB parts<br>
	 * Expectation: passes
	 */
	@Test
	public void t156_sendMessage1MBPartSize() throws Exception {
		SCMessage request = new SCMessage();

		String string10MB = TestUtil.get10MBString();
		StringBuilder sb = new StringBuilder();
		sb.append(string10MB);
		sb.append(string10MB);

		request.setData(sb.toString());
		request.setPartSize(1048576);
		request.setDataLength(sb.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		long startTime = System.currentTimeMillis();
		sessionService1.execute(request);
		System.out.println("Sent string " + sb.length() + " bytes long in " + (System.currentTimeMillis() - startTime) + " millis");
		response = msgCallback1.getResponse();
		sessionService1.deleteSession();
	}
	
	/**
	 * Description: send 1 uncompressed 20MB message 100KB parts<br>
	 * Expectation: passes
	 */
	@Test
	public void t157_sendMessage100KBPartSize() throws Exception {
		SCMessage request = new SCMessage();

		String string10MB = TestUtil.get10MBString();
		StringBuilder sb = new StringBuilder();
		sb.append(string10MB);
		sb.append(string10MB);

		request.setData(sb.toString());
		request.setPartSize(102400);
		request.setDataLength(sb.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		long startTime = System.currentTimeMillis();
		sessionService1.execute(request);
		System.out.println("Sent string " + sb.length() + " bytes long in " + (System.currentTimeMillis() - startTime) + " millis");
		response = msgCallback1.getResponse();
		sessionService1.deleteSession();
	}
	
	/**
	 * Description: send 1 uncompressed 20MB message 200KB parts<br>
	 * Expectation: passes
	 */
	@Test
	public void t157_sendMessage200KBPartSize() throws Exception {
		SCMessage request = new SCMessage();

		String string10MB = TestUtil.get10MBString();
		StringBuilder sb = new StringBuilder();
		sb.append(string10MB);
		sb.append(string10MB);

		request.setData(sb.toString());
		request.setPartSize(204800);
		request.setDataLength(sb.length());
		request.setCompressed(false);
		SCMessage response = null;
		sessionService1 = client.newSessionService(TestConstants.sesServiceName1);
		msgCallback1 = new MsgCallback(sessionService1);
		response = sessionService1.createSession(new SCMessage(), msgCallback1);
		long startTime = System.currentTimeMillis();
		sessionService1.execute(request);
		System.out.println("Sent string " + sb.length() + " bytes long in " + (System.currentTimeMillis() - startTime) + " millis");
		response = msgCallback1.getResponse();
		sessionService1.deleteSession();
	}
}
