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

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoSrvCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPBodyType;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class ClientPartLargeEchoSrvTestCase extends SuperSessionTestCase {
	
	@Test
	public void invokePartEchoSrvTest() throws Exception {
		SCMPEchoSrvCall echoCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
				scmpSession);
		echoCall.setPartMessage(true);
		echoCall.setMaxNodes(2);
		for (int i = 0; i < 100; i++) {
			String s = "Hello part " + i;
			echoCall.setBody(s);
			SCMP result = echoCall.invoke();

			Map<String, String> header = result.getHeader();
			Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName()));
			Assert.assertEquals(echoCall.getCall().getHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()),
					header.get(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()));
			Assert.assertNotNull(header.get(SCMPHeaderAttributeKey.SESSION_ID.getName()));

			if (i < 10) {
				Assert.assertEquals("12", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
				Assert.assertEquals("12", header.get(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH.getName()));
			} else {
				Assert.assertEquals("13", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
				Assert.assertEquals("13", header.get(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH.getName()));
			}
			Assert.assertEquals(SCTest.getExpectedOffset(i, 12), header.get(SCMPHeaderAttributeKey.SCMP_OFFSET
					.getName()));
			Assert.assertEquals(SCMPMsgType.ECHO_SRV.getResponseName(), result.getMessageType());
		}
		String s = "This is the end";
		echoCall.setBody(s);
		echoCall.setMaxNodes(1);
		echoCall.setPartMessage(false);
		echoCall.invoke();
	}
}
