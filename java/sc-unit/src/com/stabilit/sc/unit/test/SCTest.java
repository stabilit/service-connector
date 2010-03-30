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
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */

@RunWith(Suite.class)
@SuiteClasses( {
	RegisterServiceCallTestCase.class,
	ConnectDisconnectTestCase.class, 
	//SessionTestCase.class 
})

public class SCTest {
	
	public static void verifyError(SCMP result, SCMPErrorCode error,
			SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(
				result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), msgType
						.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_CODE
				.getName()), error.getErrorCode());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_TEXT
				.getName()), error.getErrorText());
	}
}
