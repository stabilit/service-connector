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

public class SingleEchoTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);
		echoCall.setTransitive(false);
	
		SCMP result = null;
		Map<String, String> header = null;
	
		if (index == null) {
			echoCall.setBody("hello world");
			result = echoCall.invoke();
			header = result.getHeader();
			Assert.assertEquals("hello world" , result.getBody());
			Assert.assertEquals("11", header.get(SCMPHeaderType.BODY_LENGTH.getName()));		
		} else {
			echoCall.setBody("hello world, index = " + index);
			result = echoCall.invoke();
			header = result.getHeader();
			Assert.assertEquals("hello world, index = " + index , result.getBody());
			Assert.assertNotNull(header.get(SCMPHeaderType.BODY_LENGTH.getName()));	
			index++;
		}		
		
		/*************************** verify echo session **********************************/		
		Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
		Assert.assertNotNull(result.getSessionId());		
		Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());

		SCMPEchoCall echoCallTransitive = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client,
				scmpSession);
		
		echoCallTransitive.setTransitive(true);
		echoCallTransitive.setServiceName("simulation");
		if (index == null) {
			echoCallTransitive.setBody("hello TRANSITIVE world");
			result = echoCallTransitive.invoke();
			result = echoCallTransitive.invoke();
			Assert.assertEquals("hello TRANSITIVE world" , result.getBody());
		} else {
			echoCallTransitive.setBody("hello TRANSITIVE world, index = " + index);
			result = echoCallTransitive.invoke();
			result = echoCallTransitive.invoke();
			Assert.assertEquals("hello TRANSITIVE world, index = " + index , result.getBody());
			index++;
		}
		echoCallTransitive.setTransitive(true);
		echoCallTransitive.setServiceName("simulation");
		Assert.assertEquals("string", header.get(SCMPHeaderType.SCMP_BODY_TYPE.getName()));
		Assert.assertNotNull(result.getSessionId());
		Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
	}
}