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
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class EchoSrvTestCase extends SuperSessionTestCase {

	@Test
	public void invokeSingleEchoSrvTest() throws Exception {
		SCMP result = null;
		SCMPEchoSrvCall echoSrvCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
				scmpSession);
		echoSrvCall.setMaxNodes(2);
		echoSrvCall.setServiceName("simulation");
		echoSrvCall.setBody("hello world");
		result = echoSrvCall.invoke();
		
		Map<String, String> header = result.getHeader();
		Assert.assertEquals("hello world", result.getBody());
		Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
		Assert.assertNotNull(result.getSessionId());
		Assert.assertEquals(SCMPMsgType.ECHO_SRV.getResponseName(), result.getMessageType());
	}

	@Test
	public void invokeMultipleEchoSrvTest() throws Exception {

		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMP result = null;

		SCMPEchoSrvCall echoSrvCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
				scmpSession);
		echoSrvCall.setMaxNodes(2);
		echoSrvCall.setServiceName("simulation");

		for (int i = 0; i < anzMsg; i++) {
			echoSrvCall.setBody("hello world, index = " + i);
			result = echoSrvCall.invoke();
			Assert.assertEquals("hello world, index = " + i, result.getBody());
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
	}

	@Test
	public void invokeMultipleSessionEchoSrvTest() throws Exception {
		deleteSession();
		long startTime = System.currentTimeMillis();
		int anzMsg = 1000;
		SCMP result = null;	

		for (int i = 0; i < anzMsg; i++) {
			createSession();
			SCMPEchoSrvCall echoSrvCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client,
					scmpSession);
			echoSrvCall.setMaxNodes(2);
			echoSrvCall.setServiceName("simulation");
			echoSrvCall.setBody("hello world, index = " + i);
			result = echoSrvCall.invoke();
			Assert.assertEquals("hello world, index = " + i, result.getBody());			
			deleteSession();
		}
		System.out.println(anzMsg / ((System.currentTimeMillis() - startTime) / 1000D) + " msg pro sec");
		createSession();
	}
}