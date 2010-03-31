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
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;

public class SingleEchoTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);

		if (index == null) {
			echoCall.setBody("hello world");
		} else {
			echoCall.setBody("hello world, index = " + index++);
		}
		echoCall.setTransitive(false);
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		Assert.assertNotNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());

		SCMPEchoCall echoCallTransitive = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client,
				scmpSession);
		if (index == null) {
			echoCallTransitive.setBody("hello TRANSITIVE world");
		} else {
			echoCallTransitive.setBody("hello TRANSITIVE world, index = " + index++);
		}
		echoCallTransitive.setTransitive(true);
		echoCallTransitive.setServiceName("simulation");
		result = echoCallTransitive.invoke();
		System.out.println("result echo = " + result.getBody());
	}

}
