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

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class SingleLargeEchoTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setTransitive(false);
		
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		Assert.assertNotNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

	public void invokeTestTransitive() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}
		echoCall.setBody(sb.toString());
		echoCall.setTransitive(true);
		
		SCMP result = echoCall.invoke();
		/*************************** verify echo session **********************************/
		Assert.assertNotNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.RES_ECHO.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getSessionId());
	}

}
