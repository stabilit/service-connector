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
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.clnData.ClnDataTestCase;
import com.stabilit.sc.unit.test.echo.ClientPartLargeEchoSCTestCase;
import com.stabilit.sc.unit.test.echo.ClientPartLargeEchoSrvTestCase;
import com.stabilit.sc.unit.test.echo.ClientSingleLargeEchoSCTestCase;
import com.stabilit.sc.unit.test.echo.ClientSingleLargeEchoSrvTestCase;
import com.stabilit.sc.unit.test.echo.EchoSCTestCase;
import com.stabilit.sc.unit.test.echo.EchoSrvTestCase;

/**
 * @author JTraber
 * 
 */

@RunWith(Suite.class)
@SuiteClasses( { 
	ConnectTestCase.class, 
	DisconnectTestCase.class, 
	CreateSessionTestCase.class,
	DeleteSessionTestCase.class, 
	RegisterServiceTestCase.class, 
	DeRegisterServiceTestCase.class,
	ClnDataTestCase.class,
	EchoSrvTestCase.class,
	EchoSCTestCase.class,
	ClientSingleLargeEchoSrvTestCase.class,
	ClientSingleLargeEchoSCTestCase.class,
	ClientPartLargeEchoSrvTestCase.class,
	ClientPartLargeEchoSCTestCase.class})
public class SCTest {

	public static void verifyError(SCMP result, SCMPErrorCode error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), msgType.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT), error.getErrorText());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE), error.getErrorCode());
	}

	public static String getExpectedOffset(int runIndex, int characterNumber) {
		if (runIndex > 10) {
			if (runIndex % 10 == 0) {
				return (characterNumber * 10 + (((runIndex / 10) - 1) * (characterNumber + 1)) * 10) + "";
			} else {
				if (runIndex / 10 > 0) {
					return (runIndex % 10 * (characterNumber + 1) + (characterNumber * 10) + (((runIndex / 10) - 1) * (characterNumber + 1)) * 10)
							+ "";
				} else {
					return (runIndex % 10 * (characterNumber + 1) + (characterNumber * 10)) + "";
				}
			}
		} else {
			return (runIndex * characterNumber) + "";
		}
	}
}