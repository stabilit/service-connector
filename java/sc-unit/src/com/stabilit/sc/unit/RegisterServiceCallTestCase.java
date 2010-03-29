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

import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPDeRegisterServiceCall;
import com.stabilit.sc.service.SCMPMaintenanceCall;
import com.stabilit.sc.service.SCMPRegisterServiceCall;
import com.stabilit.sc.service.SCMPServiceException;

public class RegisterServiceCallTestCase {

	static ClientConfig config = null;
	static IClient client = null;

	@Before
	public void setup() {
		SetupTestCases.setup();
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
		//failRegisterServiceCall();
		RegisterServiceCallTestCase.registerServiceCall();
		secondRegisterServiceCall();
		RegisterServiceCallTestCase.deRegisterServiceCall();
		secondDeRegisterServiceCall();
	}

	private void failRegisterServiceCall() throws Exception {
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

	public static void registerServiceCall() throws Exception {
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
		String expectedScEntry = "P01_RTXS_RPRWS1:portNr=9100;maxSessions=10;msgType=REQ_REGISTER_SERVICE;multiThreaded=1;serviceName=P01_RTXS_RPRWS1;";
		String scEntry = (String) mainMsg.getAttribute("serviceRegistry");
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	private void secondRegisterServiceCall() throws Exception {
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
	}

	public static void deRegisterServiceCall() throws Exception {
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
		Assert.assertEquals("", scEntry);
	}

	private void secondDeRegisterServiceCall() throws Exception {
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

	}
}
