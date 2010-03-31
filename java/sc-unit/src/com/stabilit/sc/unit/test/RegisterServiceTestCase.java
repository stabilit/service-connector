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
import com.stabilit.sc.cln.service.SCMPRegisterServiceCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPMsgType;

public class RegisterServiceTestCase extends SuperTestCase {

	@Test
	public void failRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		/*********************** serviceName not set *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR,
					SCMPMsgType.REQ_REGISTER_SERVICE);
		}
		/*********************** maxSessions 0 value *******************/
		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(0);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR,
					SCMPMsgType.REQ_REGISTER_SERVICE);
		}
		/*********************** port too high 10000 *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(910000);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR,
					SCMPMsgType.REQ_REGISTER_SERVICE);
		}
	}

	@Test
	public void registerServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		registerServiceCall.invoke();
		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL
				.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String expectedScEntry = "P01_RTXS_RPRWS1:portNr=9100;maxSessions=10;msgType=REQ_REGISTER_SERVICE;multiThreaded=1;serviceName=P01_RTXS_RPRWS1;simulation:portNr=7000;maxSessions=1;msgType=REQ_REGISTER_SERVICE;serviceName=simulation;";
		String scEntry = (String) mainMsg.getAttribute("serviceRegistry");
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	@Test
	public void secondRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException e) {
			SCTest.verifyError(e.getFault(), SCMPErrorCode.ALREADY_REGISTERED,
					SCMPMsgType.REQ_REGISTER_SERVICE);
		}

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);
		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();
	}
}
