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
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SetupTestCases;
import com.stabilit.sc.unit.test.SuperTestCase;

public class ClientSingleLargeEchoSCTestCase extends SuperTestCase {

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
	public void invokeTest() throws Exception {		
		try {
			while (true) {
				SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 19000; i++) {
					sb.append(i);
					if (sb.length() > (60 << 10))
						break;
				}
				echoCall.setBody(sb.toString());
				SCMP result = echoCall.invoke();
				/*************************** verify echo session **********************************/
				Map<String, String> header = result.getHeader();
				Assert.assertEquals(sb.toString(), result.getBody());
				Assert.assertEquals("string", header.get(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName()));
				Assert.assertEquals(sb.length() + "", header.get(SCMPHeaderAttributeType.BODY_LENGTH
						.getName()));
				Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
}
