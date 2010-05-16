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

import test.stabilit.sc.test.SCImplTest;

import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.unit.test.connect.ConnectTestCase;
import com.stabilit.sc.unit.test.connect.DisconnectTestCase;
import com.stabilit.sc.unit.test.echo.EchoSCLargeTestCase;
import com.stabilit.sc.unit.test.echo.EchoSCTestCase;
import com.stabilit.sc.unit.test.echo.SrvEchoLargeTestCase;
import com.stabilit.sc.unit.test.echo.SrvEchoTestCase;
import com.stabilit.sc.unit.test.register.DeRegisterServiceTestCase;
import com.stabilit.sc.unit.test.register.RegisterServiceTestCase;
import com.stabilit.sc.unit.test.session.ClnCreateSessionTestCase;
import com.stabilit.sc.unit.test.session.ClnDeleteSessionTestCase;
import com.stabilit.sc.unit.test.srvData.SrvDataLargeTestCase;
import com.stabilit.sc.unit.test.srvData.SrvDataTestCase;
import com.stabilit.sc.unit.test.worse.WorseSCServerToClientTestCase;
import com.stabilit.sc.unit.test.worse.WorseSCServerToServiceTestCase;
import com.stabilit.sc.unit.test.worse.WorseScenarioSimulationServerTestCase;

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
	
	public static void verifyError(SCMPMessage result, SCMPErrorCode error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), msgType.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT), error.getErrorText());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE), error.getErrorCode());
	}
	
	public static void verifyError(String errorText, String errorCode, SCMPErrorCode expectedError) {
		Assert.assertEquals(errorText, expectedError.getErrorText());
		Assert.assertEquals(errorCode, expectedError.getErrorCode());
	}
}