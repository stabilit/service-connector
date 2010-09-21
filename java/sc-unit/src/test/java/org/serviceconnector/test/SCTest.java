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
package org.serviceconnector.test;

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
import org.serviceconnector.test.SCVersionTestCase;
import org.serviceconnector.test.attach.AttachTestCase;
import org.serviceconnector.test.attach.DetachTestCase;
import org.serviceconnector.test.group.GroupCallTestCase;
import org.serviceconnector.test.manage.ManageTestCase;
import org.serviceconnector.test.messageId.MessageIdTestCase;
import org.serviceconnector.test.net.DefaultEncoderDecoderTestCase;
import org.serviceconnector.test.net.DefaultFrameDecoderTestCase;
import org.serviceconnector.test.net.HttpFrameDecoderTestCase;
import org.serviceconnector.test.net.KeepAliveMessageEncoderDecoderTestCase;
import org.serviceconnector.test.net.LargeMessageEncoderDecoderTestCase;
import org.serviceconnector.test.pool.ConnectionPoolTestCase;
import org.serviceconnector.test.register.DeRegisterServerTestCase;
import org.serviceconnector.test.register.RegisterServerTestCase;
import org.serviceconnector.test.scVersion.SCVersionToSCTestCase;
import org.serviceconnector.test.scmp.SCMPVersionTestCase;
import org.serviceconnector.test.scmp.internal.SCMPCompositeTestCase;
import org.serviceconnector.test.scmp.internal.SCMPLargeRequestTestCase;
import org.serviceconnector.test.scmp.internal.SCMPLargeResponseTestCase;
import org.serviceconnector.test.scmpVersion.DecodeSCMPVersionTestCase;
import org.serviceconnector.test.session.ClnCreateSessionTestCase;
import org.serviceconnector.test.session.ClnDeleteSessionTestCase;
import org.serviceconnector.test.sessionTimeout.SessionTimeoutTestCase;
import org.serviceconnector.test.srvExecute.async.SrvExecuteAsyncTestCase;
import org.serviceconnector.test.srvExecute.async.SrvExecuteLargeAsyncTestCase;
import org.serviceconnector.test.srvExecute.sync.SrvExecuteLargeSyncTestCase;
import org.serviceconnector.test.srvExecute.sync.SrvExecuteSyncTestCase;
import org.serviceconnector.test.util.LinkedQueueTestCase;
import org.serviceconnector.test.util.ValidatorUtilityTestCase;



/**
 * @author JTraber
 */
@RunWith(Suite.class)
@SuiteClasses( { AttachTestCase.class, //
		DetachTestCase.class, // 
		ClnCreateSessionTestCase.class, // 
		ClnDeleteSessionTestCase.class, // 
		RegisterServerTestCase.class, // 
		DeRegisterServerTestCase.class, // 
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