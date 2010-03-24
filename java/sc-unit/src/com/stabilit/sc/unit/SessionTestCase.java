/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
package com.stabilit.sc.unit;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.msg.impl.CreateSessionMessage;

public class SessionTestCase {

	static ClientConfig config = null;
	static IClient client = null;

	static {
		try {
			config = new ClientConfig();
			config.load("sc-unit.properties");

			ClientFactory clientFactory = new ClientFactory();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		client.disconnect(); // physical disconnect
		client = null;
	}

	@Test
	public void runTests() throws Exception {
		// guarantees test sequence
		failCreateSession();
	}

	public void failCreateSession() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_CREATE_SESSION.getRequestName());
		CreateSessionMessage sessionMsg = new CreateSessionMessage();
		scmp.setBody(sessionMsg);

		sessionMsg.setServiceName("P01_RTXS_RPRWS1");
		sessionMsg.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		/*********************************** wrong format of ip list *****************************/
		sessionMsg.setIpAddressList("10.0.4.32/243.43.1/192.243.43.1");

		SCMP result = client.sendAndReceive(scmp);
		verifyError(result, SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.RES_CREATE_SESSION);
	}

	public void createSession() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_CREATE_SESSION.getRequestName());
		CreateSessionMessage sessionMsg = new CreateSessionMessage();

		sessionMsg.setServiceName("P01_RTXS_RPRWS1");
		sessionMsg.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
		sessionMsg.setIpAddressList("127.0.0.1");

		scmp.setBody(sessionMsg);
		SCMP result = client.sendAndReceive(scmp);

		/*************************** Verify create session response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()),
				SCMPMsgType.RES_CREATE_SESSION.getResponseName());
		Assert.assertNotNull(result.getHeader(SCMPHeaderType.SESSION_ID.getName()));
		Assert.assertNotNull(result.getHeader(SCMPHeaderType.SERVICE_NAME.getName())); //?? why?

//		/*************** scmp maintenance ********/
//		MaintenanceMessage msgMain = new MaintenanceMessage();
//		scmp.setMessageType(SCMPMsgType.REQ_MAINTENANCE.getRequestName());
//		scmp.setBody(msgMain);
//		SCMP maintenance = client.sendAndReceive(scmp);
//
//		/*********************************** Verify registry entries in SC ********************************/
//		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
//		String expectedScEntry = ":compression=false;keepAliveTimeout=30,360;scmpVersion=1.0-00;";
//		String scEntry = (String) mainMsg.getAttribute("sessionRegistry");
//		Assert.assertEquals(expectedScEntry, scEntry);
	}

	public void secondCreateSession() throws Exception {
		
	}

	public void deleteSession() throws Exception {

	}

	public void secondDeleteSession() throws Exception {
	}

	/**
	 * @param result
	 */
	private void verifyError(SCMP result, SCMPErrorCode error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), msgType.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_CODE.getName()), error.getErrorCode());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_TEXT.getName()), error.getErrorText());
	}
}
