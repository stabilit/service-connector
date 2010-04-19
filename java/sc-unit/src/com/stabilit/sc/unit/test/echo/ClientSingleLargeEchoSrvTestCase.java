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
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class ClientSingleLargeEchoSrvTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoSrvCall echoCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
				scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setMaxNodes(2);
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		int start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
		int bodyLength = sb.length() - start;
		String lastPartBody = sb.substring(start);
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(lastPartBody, result.getBody());
		Assert.assertEquals(SCMPBodyType.text.getName(), header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName()));
		Assert.assertNull(header.get(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()));
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SRV.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

	public void invokeTestTransitive() throws Exception {
		SCMPEchoSrvCall echoCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
				scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 89840; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setMaxNodes(2);
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		int start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
		int bodyLength = sb.length() - start;
		String lastPartBody = sb.substring(start);
		Map<String, String> header = result.getHeader();
		Assert.assertEquals(lastPartBody, result.getBody());
		Assert.assertEquals("string", header.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName()));
		Assert.assertNull(header.get(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName()));
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SRV.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}
}
