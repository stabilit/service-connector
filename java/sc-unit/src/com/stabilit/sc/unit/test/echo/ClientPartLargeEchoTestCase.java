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
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SCTest;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class ClientPartLargeEchoTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTestNotTransitive() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);
		echoCall.setPartMessage(true);
		for (int i = 0; i < 100; i++) {
			String s = "Hello part " + i;
			echoCall.setBody(s);
			echoCall.setTransitive(false);
			SCMP result = echoCall.invoke();

			Map<String, String> header = result.getHeader();
			Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
			Assert.assertEquals("0", header.get(SCMPHeaderType.SCMP_MESSAGE_ID.getName()));
			Assert.assertNotNull(header.get(SCMPHeaderType.SESSION_ID.getName()));

			if (i < 10) {
				Assert.assertEquals("12", header.get(SCMPHeaderType.BODY_LENGTH.getName()));
				Assert.assertEquals("12", header.get(SCMPHeaderType.SCMP_CALL_LENGTH.getName()));
			} else {
				Assert.assertEquals("13", header.get(SCMPHeaderType.BODY_LENGTH.getName()));
				Assert.assertEquals("13", header.get(SCMPHeaderType.SCMP_CALL_LENGTH.getName()));
			}
			Assert.assertEquals(SCTest.getExpectedOffset(i, 12), header.get(SCMPHeaderType.SCMP_OFFSET
					.getName()));
			Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
			Assert.assertEquals(i + "", header.get(SCMPHeaderType.SEQUENCE_NR.getName()));
		}
		String s = "This is the end";
		echoCall.setBody(s);
		echoCall.setTransitive(false);
		echoCall.setPartMessage(false);
		SCMP result = echoCall.invoke();
	}

	@Test
	public void invokeTestTransitive() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);
		echoCall.setPartMessage(true);
		echoCall.setTransitive(true);
		for (int i = 0; i < 100; i++) {
			String s = "Hello part " + i;
			echoCall.setBody(s);
			SCMP result = echoCall.invoke();

			Map<String, String> header = result.getHeader();
			Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
			Assert.assertEquals("1", header.get(SCMPHeaderType.SCMP_MESSAGE_ID.getName()));
			Assert.assertNotNull(header.get(SCMPHeaderType.SESSION_ID.getName()));

			if (i < 10) {
				Assert.assertEquals("12", header.get(SCMPHeaderType.BODY_LENGTH.getName()));
				Assert.assertEquals("12", header.get(SCMPHeaderType.SCMP_CALL_LENGTH.getName()));
			} else {
				Assert.assertEquals("13", header.get(SCMPHeaderType.BODY_LENGTH.getName()));
				Assert.assertEquals("13", header.get(SCMPHeaderType.SCMP_CALL_LENGTH.getName()));
			}
			Assert.assertEquals(SCTest.getExpectedOffset(i, 12), header.get(SCMPHeaderType.SCMP_OFFSET
					.getName()));
			Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
			Assert.assertEquals(i + "", header.get(SCMPHeaderType.SEQUENCE_NR.getName()));
		}
		String s = "This is the end";
		echoCall.setBody(s);
		echoCall.setPartMessage(false);
		SCMP result = echoCall.invoke();
	}
}
