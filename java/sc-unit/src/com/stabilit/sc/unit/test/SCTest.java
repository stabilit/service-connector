/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.unit.test;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.unit.test.clnData.ClnDataLargeTestCase;
import com.stabilit.sc.unit.test.clnData.ClnDataTestCase;
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
	ClnCreateSessionTestCase.class,
	ClnDeleteSessionTestCase.class, 
	RegisterServiceTestCase.class, 
	DeRegisterServiceTestCase.class,
	ClnDataTestCase.class,
	ClnDataLargeTestCase.class,
	EchoSrvTestCase.class,
	EchoSCTestCase.class,
	ClientSingleLargeEchoSrvTestCase.class,
	ClientSingleLargeEchoSCTestCase.class})
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