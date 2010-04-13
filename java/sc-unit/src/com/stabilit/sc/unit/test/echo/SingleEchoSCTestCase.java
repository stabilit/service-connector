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
package com.stabilit.sc.unit.test.echo;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.ServiceConnector;
import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.cln.service.SCMPEchoSCCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.unit.UnitCommandFactory;
import com.stabilit.sc.unit.test.SuperTestCase;

public class SingleEchoSCTestCase extends SuperTestCase {

	@Before
	@Override
	public void setup() throws Exception {
		try {
			CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
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

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
		echoCall.setTransitive(false);

		SCMP result = null;
		Map<String, String> header = null;

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			echoCall.setBody("hello world " + i);
			result = echoCall.invoke();
		//	System.out.println("result = " + result.getBody());
		}
		System.out.println((System.currentTimeMillis() - startTime) / 1000 + " sec.");
		header = result.getHeader();
		//Assert.assertEquals("hello world 9", result.getBody());
		Assert.assertNotNull(header.get(SCMPHeaderType.BODY_LENGTH.getName()));

		/*************************** verify echo session **********************************/
		Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO.getResponseName(), result.getMessageType());
	}
}