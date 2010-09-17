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
package test.serviceconnector.unit;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

import test.serviceconnector.SCVersionTestCase;
import test.serviceconnector.attach.AttachTestCase;
import test.serviceconnector.attach.DetachTestCase;
import test.serviceconnector.group.GroupCallTestCase;
import test.serviceconnector.manage.ManageTestCase;
import test.serviceconnector.messageId.MessageIdTestCase;
import test.serviceconnector.net.DefaultEncoderDecoderTestCase;
import test.serviceconnector.net.DefaultFrameDecoderTestCase;
import test.serviceconnector.net.HttpFrameDecoderTestCase;
import test.serviceconnector.net.KeepAliveMessageEncoderDecoderTestCase;
import test.serviceconnector.net.LargeMessageEncoderDecoderTestCase;
import test.serviceconnector.pool.ConnectionPoolTestCase;
import test.serviceconnector.register.DeRegisterServiceTestCase;
import test.serviceconnector.register.RegisterServiceTestCase;
import test.serviceconnector.scVersion.SCVersionToSCTestCase;
import test.serviceconnector.scmp.SCMPVersionTestCase;
import test.serviceconnector.scmp.internal.SCMPCompositeTestCase;
import test.serviceconnector.scmp.internal.SCMPLargeRequestTestCase;
import test.serviceconnector.scmp.internal.SCMPLargeResponseTestCase;
import test.serviceconnector.scmpVersion.DecodeSCMPVersionTestCase;
import test.serviceconnector.session.ClnCreateSessionTestCase;
import test.serviceconnector.session.ClnDeleteSessionTestCase;
import test.serviceconnector.sessionTimeout.SessionTimeoutTestCase;
import test.serviceconnector.srvExecute.async.SrvExecuteAsyncTestCase;
import test.serviceconnector.srvExecute.async.SrvExecuteLargeAsyncTestCase;
import test.serviceconnector.srvExecute.sync.SrvExecuteLargeSyncTestCase;
import test.serviceconnector.srvExecute.sync.SrvExecuteSyncTestCase;
import test.serviceconnector.util.LinkedQueueTestCase;
import test.serviceconnector.util.ValidatorUtilityTestCase;


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
		SrvExecuteSyncTestCase.class, // 
		SrvExecuteLargeSyncTestCase.class, // 
		SrvExecuteAsyncTestCase.class,// 
		SrvExecuteLargeAsyncTestCase.class, //
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
		// TODO TRN refine SCMPErrors
		/*
		 * text must not be compared! It may be chinese
		Assert.assertEquals(error.getErrorText() + additionalInfo, result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		*/
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
		//actual = actual.replaceAll("127.0.0.1/", "localhost/");
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