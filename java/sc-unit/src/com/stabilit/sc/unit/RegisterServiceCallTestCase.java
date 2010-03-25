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

import com.stabilit.sc.ServiceConnector;
import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.service.ISCMPCall;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPMaintenanceCall;
import com.stabilit.sc.service.SCMPRegisterServiceCall;
import com.stabilit.sc.util.ValidatorUtility;

public class RegisterServiceCallTestCase {

	static ClientConfig config = null;
	static IClient client = null;

	static {
		try {
			ServiceConnector.main(null);
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
		registerServiceCall();
	}

	private void registerServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL.newInstance(client);
		
		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMultithreaded(true);
		registerServiceCall.setPortNumber(9100);
		
		registerServiceCall.invoke();
		
		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String expectedScEntry = ":compression=0;localDateTime=;keepAliveTimeout=30,360;scmpVersion=1.0-00;";
		String scEntry = (String) mainMsg.getAttribute("serviceRegistry");
		// truncate /127.0.0.1:3640 because port may vary.

//		Assert.assertEquals(expectedScEntry, scEntry);
		
	}
}
