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
package com.stabilit.scm.unit.test;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.echo.mt.MTSrvEchoTestCase;

/**
 * The Class SCMultipleClientAndStressTest.
 * 
 * @author JTraber
 */
@RunWith(Suite.class)
@SuiteClasses( {
// multiple clients test
		// StressTest.class,
		MTSrvEchoTestCase.class })
public final class SCMultipleClientAndStressTestCases {

	/**
	 * Instantiates a new sC multiple client and stress test.
	 */
	private SCMultipleClientAndStressTestCases() {
	}

	/**
	 * Verify error.
	 * 
	 * @param result
	 *            the result
	 * @param error
	 *            the error
	 * @param msgType
	 *            the msg type
	 */
	public static void verifyError(SCMPMessage result, SCMPError error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), msgType.getValue());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT), error.getErrorText());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE), error.getErrorCode());
	}
}