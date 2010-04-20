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

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPDeleteSessionCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;

public class DeleteSessionTestCase extends SuperSessionTestCase {

	@Test
	public void deleteSession() throws Exception {
		SCMPDeleteSessionCall deleteSessionCall = (SCMPDeleteSessionCall) SCMPCallFactory.DELETE_SESSION_CALL
				.newInstance(client, scmpSession);
		SCMP result = deleteSessionCall.invoke();

		/*************************** verify create session **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(SCMPMsgType.DELETE_SESSION.getResponseName(), result.getMessageType());
		Assert.assertNotNull(result.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(client);
		SCMP inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String scEntry = (String) inspectMsg.getAttribute("sessionRegistry");
		Assert.assertEquals("", scEntry);
		super.createSession();
	}
}
