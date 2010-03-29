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
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPCreateSessionCall;
import com.stabilit.sc.service.SCMPMaintenanceCall;
import com.stabilit.sc.service.SCMPServiceException;

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
		RegisterServiceCallTestCase.registerServiceCall();
		failCreateSession();
		//createSession();
	}

	public void failCreateSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR,
					SCMPMsgType.REQ_CREATE_SESSION);
		}
	}

	public void createSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("P01_RTXS_RPRWS1");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		SCMP result = createSessionCall.invoke();
		/*************************** verify create session **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getMessageType(),
				SCMPMsgType.RES_CREATE_SESSION.getResponseName());
		Assert.assertNotNull(result.getSessionId());
		Assert.assertNotNull(result.getHeader(SCMPHeaderType.SERVICE_NAME
				.getName()));

		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL
				.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String expectedScEntry = ":com.stabilit.sc.registry.ServiceRegistryItem=portNr=9100;maxSessions=10;msgType=REQ_REGISTER_SERVICE;multiThreaded=1;serviceName=P01_RTXS_RPRWS1;;";		
		String scEntry = (String) mainMsg.getAttribute("sessionRegistry");
		scEntry = scEntry.substring(scEntry.indexOf(":"));
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	public void deleteSession() throws Exception {

	}
}
