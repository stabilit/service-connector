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
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.cln.msg.impl.MaintenanceMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPCreateSessionCall;
import com.stabilit.sc.cln.service.SCMPDeleteSessionCall;
import com.stabilit.sc.cln.service.SCMPMaintenanceCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;


public class CreateSessionTestCase extends SuperConnectTestCase{

	private SCMPSession scmpSession = null;

	@Test
	public void failCreateSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		try {
			createSessionCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.REQ_CREATE_SESSION);
		}
	}

	@Test
	public void createSession() throws Exception {
		SCMPCreateSessionCall createSessionCall = (SCMPCreateSessionCall) SCMPCallFactory.CREATE_SESSION_CALL
				.newInstance(client);

		createSessionCall.setServiceName("simulation");
		createSessionCall.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");

		scmpSession = createSessionCall.invoke();
		/*************************** verify create session **********************************/
		Assert.assertNull(scmpSession.getBody());
		Assert.assertEquals(SCMPMsgType.RES_CREATE_SESSION.getResponseName(), scmpSession.getMessageType());
		Assert.assertNotNull(scmpSession.getSessionId());
		Assert.assertNotNull(scmpSession.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));

		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL
				.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String expectedScEntry = ":com.stabilit.sc.registry.ServiceRegistryItem=portNr=7000;maxSessions=1;msgType=REQ_REGISTER_SERVICE;serviceName=simulation;;";
		String scEntry = (String) mainMsg.getAttribute("sessionRegistry");
		scEntry = scEntry.substring(scEntry.indexOf(":"));
		Assert.assertEquals(expectedScEntry, scEntry);
		
		SCMPDeleteSessionCall deleteSessionCall = (SCMPDeleteSessionCall) SCMPCallFactory.DELETE_SESSION_CALL.newInstance(client, scmpSession);
		deleteSessionCall.invoke();
	}
}
