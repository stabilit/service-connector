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

import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoSCCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPBodyType;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SetupTestCases;
import com.stabilit.sc.unit.test.SuperTestCase;

public class ClientPartLargeEchoSCTestCase extends SuperTestCase {

	@Before
	@Override
	public void setup() throws Exception {
		SetupTestCases.setupSC();
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

	@Test
	public void invokePartEchoSCTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
		echoCall.setPartMessage(true);

		int bodyLength = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			String s = "Hello part " + i;
			sb.append(s);
			echoCall.setBody(s);
			SCMP result = echoCall.invoke();

			Map<String, String> header = result.getHeader();
			Assert.assertEquals(echoCall.getCall().getHeader(
					SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()), header
					.get(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()));

			Assert.assertEquals("0", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
			Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
			Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.SCMP_OFFSET.getName()));
			bodyLength += s.length();
		}
		String s = "This is the end";
		bodyLength += s.length();
		sb.append(s);
		echoCall.setBody(s);
		echoCall.setPartMessage(false);
		SCMP result = echoCall.invoke();
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
		Assert.assertEquals(sb.toString(), result.getBody());
	}
}
