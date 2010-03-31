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

import com.stabilit.sc.cln.msg.impl.MaintenanceMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPDeRegisterServiceCall;
import com.stabilit.sc.cln.service.SCMPMaintenanceCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPMsgType;

public class DeRegisterServiceTestCase extends SuperRegisterTestCase {

	@Test
	public void deRegisterServiceCall() throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);

		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();

		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL
				.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String scEntry = (String) mainMsg.getAttribute("serviceRegistry");
		String expectedEnty = "simulation:portNr=7000;maxSessions=1;msgType=REQ_REGISTER_SERVICE;serviceName=simulation;";
		Assert.assertEquals(expectedEnty, scEntry);
		super.registerService();
	}

	@Test
	public void secondDeRegisterServiceCall() throws Exception {
		super.deRegisterService();
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);

		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");

		try {
			deRegisterServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException e) {
			SCTest.verifyError(e.getFault(), SCMPErrorCode.NOT_REGISTERED,
					SCMPMsgType.REQ_DEREGISTER_SERVICE);
		}
		super.registerService();
	}
}
