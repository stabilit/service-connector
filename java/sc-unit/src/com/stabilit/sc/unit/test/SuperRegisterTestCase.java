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
/**
 * 
 */
package com.stabilit.sc.unit.test;

import org.junit.After;
import org.junit.Before;

import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPDeRegisterServiceCall;
import com.stabilit.sc.cln.service.SCMPRegisterServiceCall;

/**
 * @author JTraber
 * 
 */
public abstract class SuperRegisterTestCase extends SuperTestCase {

	protected SCMPSession scmpSession = null;

	public SuperRegisterTestCase() {
		super();
	}

	@Before
	public void setup() throws Exception {
		super.setup();
		registerService();
	}

	@After
	public void tearDown() throws Exception {
		deRegisterService();
		super.tearDown();
	}

	public void registerService() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(client);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.invoke();
	}

	public void deRegisterService() throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(client);
		deRegisterServiceCall.setServiceName("P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();
	}
}