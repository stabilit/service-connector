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
package org.serviceconnector.test.sc.scVersion;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.SynchronousCallback;



public class SCVersionToSCTest extends SuperTestCase {

	public SCVersionToSCTest(String fileName) {
		super(fileName);
	}

	@Test
	public void scVersionIsEmpty() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(req) {
			@Override
			public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};

		TestSCMPVersionCallback callback = new TestSCMPVersionCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		SCTest.verifyError(result, SCMPError.HV_WRONG_SC_VERSION_FORMAT, " []", SCMPMsgType.ATTACH);
	}

	@Test
	public void scVersionIsIncompatible() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(req) {
			@Override
			public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "2.0-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};

		TestSCMPVersionCallback callback = new TestSCMPVersionCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		SCTest.verifyError(result, SCMPError.HV_WRONG_SC_RELEASE_NR, " [2.0-000]", SCMPMsgType.ATTACH);
	}

	@Test
	public void scVersion1_1_000() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(req) {
			@Override
			public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "1.1-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};

		TestSCMPVersionCallback callback = new TestSCMPVersionCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		SCTest.verifyError(result, SCMPError.HV_WRONG_SC_VERSION_FORMAT, " [1.1-000]", SCMPMsgType.ATTACH);
	}

	@Test
	public void scVersion0_9_000() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(req) {
			@Override
			public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "0.9-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};

		TestSCMPVersionCallback callback = new TestSCMPVersionCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		SCTest.verifyError(result, SCMPError.HV_WRONG_SC_RELEASE_NR, " [0.9-000]", SCMPMsgType.ATTACH);
	}

	@Test
	public void scVersionCompatible() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(req) {
			@Override
			public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "1.0-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};

		TestSCMPVersionCallback callback = new TestSCMPVersionCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		Assert.assertFalse(result.isFault());

		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
		detachCall.invoke(callback, 1000);
		result = callback.getMessageSync(3000);
		Assert.assertFalse(result.isFault());
	}

	private class TestSCMPVersionCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
