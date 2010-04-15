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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoSCCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class ClientSingleLargeEchoSCTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	public void invokeTest() throws Exception {
		SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		int start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
		int bodyLength = sb.length() - start;
		String lastPartBody = sb.substring(start);
		Map<String, String> header = result.getHeader();
		System.out.println(lastPartBody.length());
		System.out.println(result.getBodyLength());
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("expected.txt")));
		bw.write(lastPartBody);
		bw.flush();
		bw.close();
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("actual.txt")));
		bw1.write(result.getBody().toString());
		bw1.flush();
		bw1.close();
		Assert.assertEquals(lastPartBody, result.getBody());
		Assert.assertEquals("string", header.get(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName()));
		Assert.assertNull(header.get(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName()));
		Assert.assertEquals(bodyLength + "", header.get(SCMPHeaderAttributeType.BODY_LENGTH.getName()));
		Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
	}

	@Test
	public void invokeTestTransitive() throws Exception {
		//SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);

		int start = 0;
		int bodyLength = 0;
		String lastPartBody = null;
		Map<String, String> header = null;
		SCMP result = null;
		StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < 89840; i++) {
		// sb.append("ABC");
		// }
		sb.append("ABC");
		try {
			while (true) {
				SCMPEchoSCCall echoCall = (SCMPEchoSCCall) SCMPCallFactory.ECHO_SC_CALL.newInstance(client);
				echoCall.setBody(sb.toString());
				result = echoCall.invoke();
				/*************************** verify echo session **********************************/
				start = (sb.length() / SCMP.LARGE_MESSAGE_LIMIT) * SCMP.LARGE_MESSAGE_LIMIT;
				bodyLength = sb.length() - start;
				lastPartBody = sb.substring(start);
				header = result.getHeader();
				System.out.println(lastPartBody.length());
				System.out.println(result.getBodyLength());
				Assert.assertEquals(lastPartBody, result.getBody());
				Assert.assertEquals("string", header.get(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName()));
				Assert.assertNull(header.get(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName()));
				Assert.assertEquals(bodyLength + "", header
						.get(SCMPHeaderAttributeType.BODY_LENGTH.getName()));
				Assert.assertEquals(SCMPMsgType.ECHO_SC.getResponseName(), result.getMessageType());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
