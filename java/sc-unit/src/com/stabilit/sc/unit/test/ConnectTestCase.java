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
import com.stabilit.sc.cln.service.SCMPConnectCall;
import com.stabilit.sc.cln.service.SCMPDisconnectCall;
import com.stabilit.sc.cln.service.SCMPInspectCall;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.util.ValidatorUtility;

public class ConnectTestCase extends SuperTestCase {

	@Test
	public void failConnect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
		/******************* incompatible scmp version ******************/
		connectCall.setVersion("2.0-00");
		try {
			connectCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPServiceException ex) {
			SCTest.verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.CONNECT);
		}
	}

	@Test
	public void connect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);

		connectCall.setVersion("1.0-00");
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);

		SCMP result = connectCall.invoke();

		/*********************************** Verify connect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.CONNECT
				.getResponseName());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));

		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL
				.newInstance(client);
		SCMP inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = ":compression=false;localDateTime="
				+ ValidatorUtility.validateLocalDateTime(connectCall.getCall().getHeader(
						SCMPHeaderAttributeKey.LOCAL_DATE_TIME))
				+ ";scVersion=1.0-00;keepAliveTimeout=30,360;";
		String scEntry = (String) inspectMsg.getAttribute("connectionRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.substring(scEntry.indexOf(":") + 1);
		scEntry = scEntry.substring(scEntry.indexOf(":"));

		Assert.assertEquals(expectedScEntry, scEntry);

		SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
				.newInstance(client);
		disconnectCall.invoke();
	}
}
