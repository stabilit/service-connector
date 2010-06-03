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

import test.stabilit.sc.test.SCImplTest;

import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.attach.AttachTestCase;
import com.stabilit.scm.unit.test.attach.DetachTestCase;
import com.stabilit.scm.unit.test.echo.EchoSCLargeTestCase;
import com.stabilit.scm.unit.test.echo.EchoSCTestCase;
import com.stabilit.scm.unit.test.echo.SrvEchoLargeTestCase;
import com.stabilit.scm.unit.test.echo.SrvEchoTestCase;
import com.stabilit.scm.unit.test.register.DeRegisterServiceTestCase;
import com.stabilit.scm.unit.test.register.RegisterServiceTestCase;
import com.stabilit.scm.unit.test.session.ClnCreateSessionTestCase;
import com.stabilit.scm.unit.test.session.ClnDeleteSessionTestCase;
import com.stabilit.scm.unit.test.srvData.SrvDataLargeTestCase;
import com.stabilit.scm.unit.test.srvData.SrvDataTestCase;
import com.stabilit.scm.unit.test.worse.WorseSCServerToClientTestCase;
import com.stabilit.scm.unit.test.worse.WorseSCServerToServiceTestCase;
import com.stabilit.scm.unit.test.worse.WorseScenarioSimulationServerTestCase;

/**
 * @author JTraber
 * 
 */

@RunWith(Suite.class)
@SuiteClasses( { 
	AttachTestCase.class,
	DetachTestCase.class, 
	ClnCreateSessionTestCase.class,
	ClnDeleteSessionTestCase.class, 
	RegisterServiceTestCase.class, 
	DeRegisterServiceTestCase.class,
	SrvDataTestCase.class,
	SrvDataLargeTestCase.class,
	SrvEchoTestCase.class,
	SrvEchoLargeTestCase.class,
	EchoSCTestCase.class,
	EchoSCLargeTestCase.class,
	SCImplTest.class,
	WorseScenarioSimulationServerTestCase.class,
	WorseSCServerToServiceTestCase.class,
	WorseSCServerToClientTestCase.class})
public class SCTest {
	
	private SCTest() {
	}
	
	public static void verifyError(SCMPMessage result, SCMPError error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), msgType.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT), error.getErrorText());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE), error.getErrorCode());
	}
	
	public static void verifyError(String errorText, String errorCode, SCMPError expectedError) {
		Assert.assertEquals(errorText, expectedError.getErrorText());
		Assert.assertEquals(errorCode, expectedError.getErrorCode());
	}
}