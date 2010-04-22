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
/**
 * 
 */
package com.stabilit.sc.unit.test.clnData;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPClnDataCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPBodyType;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.SuperSessionTestCase;

/**
 * @author JTraber
 * 
 */
public class ClnDataTestCase extends SuperSessionTestCase {

	@Test
	public void multipleClnDataTest() throws Exception {

		for (int i = 0; i < 100; i++) {
			SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client,
					scmpSession);
			clnDataCall.setMessagInfo("message info");
			SCMP scmpReply = clnDataCall.invoke();

			Assert.assertEquals("Message number " + i, scmpReply.getBody());
			Assert.assertEquals(SCMPBodyType.text.getName(), scmpReply
					.getHeader(SCMPHeaderAttributeKey.SCMP_BODY_TYPE));
			int bodyLength = (i + "").length() + 15;
			Assert.assertEquals(bodyLength + "", scmpReply.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH));
			Assert.assertNotNull(scmpReply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
			Assert.assertEquals(SCMPMsgType.CLN_DATA.getResponseName(), scmpReply.getMessageType());
			String serviceName = clnDataCall.getCall().getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
			String sessionId = clnDataCall.getCall().getSessionId();
			Assert.assertEquals(serviceName, scmpReply.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
			Assert.assertEquals(sessionId, scmpReply.getSessionId());
		}
	}
}