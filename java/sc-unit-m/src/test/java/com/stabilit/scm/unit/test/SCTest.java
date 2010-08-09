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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.stabilit.scm.common.net.DefaultEncoderDecoderTest;
import test.stabilit.scm.common.net.DefaultFrameDecoderTest;
import test.stabilit.scm.common.net.HttpFrameDecoderTest;
import test.stabilit.scm.common.net.KeepAliveMessageEncoderDecoderTest;
import test.stabilit.scm.common.net.LargeMessageEncoderDecoderTest;
import test.stabilit.scm.common.scmp.internal.SCMPCompositeTest;
import test.stabilit.scm.common.scmp.internal.SCMPLargeRequestTest;
import test.stabilit.scm.common.scmp.internal.SCMPLargeResponseTest;
import test.stabilit.scm.common.util.ValidatorUtilityTest;

import com.stabilit.scm.common.net.req.ConnectionPoolTest;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.attach.AttachTestCase;
import com.stabilit.scm.unit.test.attach.DetachTestCase;
import com.stabilit.scm.unit.test.messageId.MessageIdTestCase;
import com.stabilit.scm.unit.test.register.DeRegisterServiceTestCase;
import com.stabilit.scm.unit.test.register.RegisterServiceTestCase;
import com.stabilit.scm.unit.test.scVersion.SCVersionToSCTestCase;
import com.stabilit.scm.unit.test.scmpVersion.SCMPVersionTest;
import com.stabilit.scm.unit.test.session.ClnCreateSessionTestCase;
import com.stabilit.scm.unit.test.session.ClnDeleteSessionTestCase;
import com.stabilit.scm.unit.test.sessionTimeout.SessionTimeoutTest;
import com.stabilit.scm.unit.test.srvData.async.SrvDataAsyncTestCase;
import com.stabilit.scm.unit.test.srvData.async.SrvDataLargeAsyncTestCase;
import com.stabilit.scm.unit.test.srvData.sync.SrvDataLargeSyncTestCase;
import com.stabilit.scm.unit.test.srvData.sync.SrvDataSyncTestCase;

/**
 * @author JTraber
 */
@RunWith(Suite.class)
@SuiteClasses( { AttachTestCase.class, //
		DetachTestCase.class, // 
		ClnCreateSessionTestCase.class, // 
		ClnDeleteSessionTestCase.class, // 
		RegisterServiceTestCase.class, // 
		DeRegisterServiceTestCase.class, // 
		SrvDataSyncTestCase.class, // 
		SrvDataLargeSyncTestCase.class, // 
		SrvDataAsyncTestCase.class,// 
		SrvDataLargeAsyncTestCase.class, //  
		ConnectionPoolTest.class, // 
		MessageIdTestCase.class,// 
		SCVersionToSCTestCase.class, // 
		SCMPVersionTest.class, // 
		SessionTimeoutTest.class, //
		// SCImplTestCases
		DefaultFrameDecoderTest.class,//
		HttpFrameDecoderTest.class, //
		SCMPCompositeTest.class,//
		SCMPLargeRequestTest.class, //
		SCMPLargeResponseTest.class,//
		LargeMessageEncoderDecoderTest.class, //
		KeepAliveMessageEncoderDecoderTest.class,//
		DefaultEncoderDecoderTest.class,//
		ValidatorUtilityTest.class })
public class SCTest {

	private SCTest() {
	}

	public static void verifyError(SCMPMessage result, SCMPError error, SCMPMsgType msgType) {
		Assert.assertEquals(msgType.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(error.getErrorText(), result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		Assert.assertEquals(error.getErrorCode(), result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
	}

	public static void verifyError(String errorText, String errorCode, SCMPError expectedError) {
		Assert.assertEquals(errorText, expectedError.getErrorText());
		Assert.assertEquals(errorCode, expectedError.getErrorCode());
	}

	public static Map<String, String> splitStringToMap(String stringToSplit, String entryDelimiter, String keyDelimiter) {
		Map<String, String> map = new HashMap<String, String>();

		String[] rows = stringToSplit.split(entryDelimiter);

		for (String row : rows) {
			String[] keyValue = row.split(keyDelimiter, 2);
			map.put(keyValue[0], keyValue[1]);
		}
		return map;
	}

	public static void assertEqualsUnorderedStringIgnorePorts(String expected, String actual) {
		actual = actual.replaceAll("127.0.0.1/", "localhost/");
		actual = actual.replaceAll("localhost/127.0.0.1:\\d*", "localhost/127.0.0.1:");

		Map<String, String> expectedMap = splitStringToMap(expected, "\\|", "\\:");
		Map<String, String> actualMap = splitStringToMap(actual, "\\|", "\\:");

		// if (expectedMap.equals(actualMap) == false) {
		// System.out.println("actual : " + actual);
		// System.out.println("expected : " + expected);
		// }
		Assert.assertEquals(expectedMap, actualMap);
	}

	public static Map<String, String> convertInspectStringToMap(String string) {
		Map<String, String> map = new HashMap<String, String>();

		String[] values = string.split("@|&");
		for (int i = 0; i < values.length / 2; i++) {
			map.put(values[i * 2], values[i * 2 + 1]);
		}
		return map;
	}

	public static void checkReply(SCMPMessage message) throws Throwable {
		if (message.isFault()) {
			SCMPFault fault = (SCMPFault) message;
			Throwable th = fault.getCause();
			if (th != null) {
				throw th;
			}
			throw new Exception(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}
}