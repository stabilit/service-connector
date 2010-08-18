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

import test.stabilit.scm.common.SCVersionTestCase;
import test.stabilit.scm.common.net.DefaultEncoderDecoderTestCase;
import test.stabilit.scm.common.net.DefaultFrameDecoderTestCase;
import test.stabilit.scm.common.net.HttpFrameDecoderTestCase;
import test.stabilit.scm.common.net.KeepAliveMessageEncoderDecoderTestCase;
import test.stabilit.scm.common.net.LargeMessageEncoderDecoderTestCase;
import test.stabilit.scm.common.scmp.SCMPVersionTestCase;
import test.stabilit.scm.common.scmp.internal.SCMPCompositeTestCase;
import test.stabilit.scm.common.scmp.internal.SCMPLargeRequestTestCase;
import test.stabilit.scm.common.scmp.internal.SCMPLargeResponseTestCase;
import test.stabilit.scm.common.util.LinkedQueueTestCase;
import test.stabilit.scm.common.util.ValidatorUtilityTestCase;

import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.test.attach.AttachTestCase;
import com.stabilit.scm.unit.test.attach.DetachTestCase;
import com.stabilit.scm.unit.test.group.GroupCallTestCase;
import com.stabilit.scm.unit.test.manage.ManageTestCase;
import com.stabilit.scm.unit.test.messageId.MessageIdTestCase;
import com.stabilit.scm.unit.test.pool.ConnectionPoolTestCase;
import com.stabilit.scm.unit.test.register.DeRegisterServiceTestCase;
import com.stabilit.scm.unit.test.register.RegisterServiceTestCase;
import com.stabilit.scm.unit.test.scVersion.SCVersionToSCTestCase;
import com.stabilit.scm.unit.test.scmpVersion.DecodeSCMPVersionTestCase;
import com.stabilit.scm.unit.test.session.ClnCreateSessionTestCase;
import com.stabilit.scm.unit.test.session.ClnDeleteSessionTestCase;
import com.stabilit.scm.unit.test.sessionTimeout.SessionTimeoutTestCase;
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
		ManageTestCase.class, //
		ConnectionPoolTestCase.class, // 
		MessageIdTestCase.class,// 
		SCVersionToSCTestCase.class, // 
		DecodeSCMPVersionTestCase.class, // 
		SessionTimeoutTestCase.class, //
		GroupCallTestCase.class, //
		// SCImplTestCases
		DefaultFrameDecoderTestCase.class,//
		HttpFrameDecoderTestCase.class, //
		SCMPCompositeTestCase.class,//
		SCMPLargeRequestTestCase.class, //
		SCMPLargeResponseTestCase.class,//
		LargeMessageEncoderDecoderTestCase.class, //
		KeepAliveMessageEncoderDecoderTestCase.class,//
		DefaultEncoderDecoderTestCase.class,//
		ValidatorUtilityTestCase.class, //
		LinkedQueueTestCase.class, //
		// SCAPITestCases
		SCVersionTestCase.class, //
		SCMPVersionTestCase.class })
public class SCTest {

	private SCTest() {
	}

	public static void verifyError(SCMPMessage result, SCMPError error, String additionalInfo, SCMPMsgType msgType) {
		Assert.assertEquals(msgType.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(error.getErrorText() + additionalInfo, result
				.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		Assert.assertEquals(error.getErrorCode(), result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
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
		actual = actual.replaceAll("localhost/\\d*:", "localhost/:");

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

	public static void checkReply(SCMPMessage message) throws Exception {
		if (message.isFault()) {
			SCMPFault fault = (SCMPFault) message;
			Exception ex = fault.getCause();
			if (ex != null) {
				throw ex;
			}
			throw new Exception(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}
}