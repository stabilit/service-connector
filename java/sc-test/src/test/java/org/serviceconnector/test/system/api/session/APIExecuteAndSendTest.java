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
package org.serviceconnector.test.system.api.session;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;

@SuppressWarnings("unused")
public class APIExecuteAndSendTest extends APISystemSuperSessionClientTest {

	private SCSessionService service;

	@After
	public void afterOneTest() throws Exception {
		try {
			service.deleteSession();
		} catch (Exception e1) {
		}
		service = null;
		super.afterOneTest();
	}

	/**
	 * Description: exchange one uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t001_executeUncompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange one compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t002_executeCompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange one 1MB uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t003_executeLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(new SCMessage(), cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange one 1MB compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t004_executeLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(new SCMessage(), cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t010_disabledService() throws Exception {
		SCMessage request = new SCMessage("tesst");
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// execute
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: exchange before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t020_executeBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		response = service.execute(request);
	}

	/**
	 * Description: screw up sessionId after create session but before message exchange<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t030_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		String sessionId = service.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		response = service.execute(request);
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
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
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		cbk = new MsgCallback(service);
		try {
			response = service.createSession(request, cbk);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		response = service.execute(request);
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t050_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		response = service.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t051_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = service.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms,
			Thread.sleep(5000); // wait 5000ms to allow server sleep request completion
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
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
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		try {
			response = service.execute(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		} catch (SCServiceException e) {
			// will get here after 3000 ms
			// continue immediatelly
		}
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo");
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t101_sendUncompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoAppErrorCmd);
		service.send(request);
		cbk.waitForMessage(10);
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send 1 compressed message<br>
	 * Expectation: passes
	 */
	@Test
	public void t102_sendCompressed() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		service.send(request);
		cbk.waitForMessage(10);
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send 1 uncompressed 10MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t103_sendLargeUncompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(false);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(new SCMessage(), cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		service.send(request);
		cbk.waitForMessage(20);
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send 1 compressed 10MB message<br>
	 * Expectation: passes
	 */
	@Test
	public void t104_sendLargeCompressed() throws Exception {
		SCMessage request = new SCMessage(new byte[TestConstants.dataLength1MB]);
		request.setCompressed(true);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(new SCMessage(), cbk);
		request.setMessageInfo(TestConstants.echoCmd);
		service.send(request);
		cbk.waitForMessage(20);
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("message info is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("sessionId is not the same", service.getSessionId(), response.getSessionId());
		Assert.assertEquals("service name is not the same", request.getServiceName(), response.getServiceName());
		Assert.assertEquals("session info is not the same", request.getSessionInfo(), response.getSessionInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send message before create session<br>
	 * Expectation: throws SCServiceException
	 */
	@Test(expected = SCServiceException.class)
	public void t120_sendBeforeCreateSession() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		service.send(request);
	}

	/**
	 * Description: exchange messages on service which has been disabled<br>
	 * Expectation: passes
	 */
	@Test
	public void t110_disabledService() throws Exception {
		SCMessage request = new SCMessage(new byte[128]);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);

		// disable service
		SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP);
		clientMgmt.attach();
		clientMgmt.disableService(TestConstants.sesServiceName1);
		clientMgmt.detach();

		// send
		request.setMessageInfo(TestConstants.echoCmd);
		service.send(request);
		cbk.waitForMessage(10);
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: send message after session rejection<br>
	 * Expectation: throws SCserviceException
	 */
	@Test(expected = SCServiceException.class)
	public void t140_rejectSession() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		request.setSessionInfo(TestConstants.rejectSessionCmd);
		cbk = new MsgCallback(service);
		try {
			response = service.createSession(request, cbk);
		} catch (Exception e) {
			// ignore rejection
		}
		request.setMessageInfo(TestConstants.echoCmd);
		service.send(request);
	}

	/**
	 * Description: screw up sessionId before message send<br>
	 * Expectation: passes because sessionId is set internally again.
	 */
	@Test
	public void t130_sessionId() throws Exception {
		SCMessage request = new SCMessage(TestConstants.pangram);
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		String sessionId = service.getSessionId();
		request.setMessageInfo(TestConstants.echoCmd);
		request.setSessionId("aaaa0000-bb11-cc22-dd33-eeeeee444444");
		service.send(request);
		cbk.waitForMessage(10);
		response = cbk.getResponse();
		Assert.assertEquals("sessionId is not the same", sessionId, response.getSessionId());
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution<br>
	 * Expectation: passes, gets back a fault response
	 */
	@Test
	public void t150_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		service.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		cbk.waitForMessage(10);
		response = cbk.getResponse();
		// TODO TRN check SC error
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue after a while<br>
	 * Expectation: passes
	 */
	@Test
	public void t151_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		service.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		cbk.waitForMessage(10); // will wait max 10 seconds for response
		response = cbk.getResponse();
		Thread.sleep(4000); // wait 4000ms to allow server sleep request completion

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("hallo"); // send second message
		service.send(request);
		cbk.waitForMessage(10); // will wait max 10 seconds for the second response
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

	/**
	 * Description: operation timeout expired during execution, catch exception and continue immediately<br>
	 * Expectation: passes
	 */
	@Test
	public void t152_operationTimeout() throws Exception {
		SCMessage request = new SCMessage("hallo");
		SCMessage response = null;
		service = client.newSessionService(TestConstants.sesServiceName1);
		cbk = new MsgCallback(service);
		response = service.createSession(request, cbk);
		request.setMessageInfo(TestConstants.sleepCmd);
		request.setData("5000"); // server will sleep 5000ms
		service.send(3, request); // SC oti = 3*0.8*1000 = 2400ms => will return exception
		cbk.waitForMessage(10); // will wait max 10 seconds for response
		response = cbk.getResponse();

		// second message
		messageReceived = false;
		request.setMessageInfo(TestConstants.echoCmd);
		request.setData("gaga");
		service.send(request);
		cbk.waitForMessage(10); // will wait max 10 seconds for response
		response = cbk.getResponse();
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());

		// third message (synchronous)
		request.setData("abraka-dabra");
		response = service.execute(request);
		Assert.assertEquals("message body is not the same length", request.getDataLength(), response.getDataLength());
		Assert.assertEquals("messageInfo is not the same", request.getMessageInfo(), response.getMessageInfo());
		Assert.assertEquals("compression is not the same", request.isCompressed(), response.isCompressed());
		service.deleteSession();
	}

}
