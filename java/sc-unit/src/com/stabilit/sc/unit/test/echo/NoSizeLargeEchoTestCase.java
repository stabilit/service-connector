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

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

public class NoSizeLargeEchoTestCase extends SuperSessionTestCase {

	protected Integer index = null;

	@Test
	public void invokeTest() throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmpSession);
		echoCall.setPartMessage(true);
		for (int i = 0; i < 100; i++) {
			String s = "Hello part " + i;
			echoCall.setBody(s);
			echoCall.setTransitive(false);
			SCMP result = echoCall.invoke();
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
		}
		String s = "This is the end";
		echoCall.setBody(s);
		echoCall.setPartMessage(false);
		SCMP result = echoCall.invoke();
	}


}
