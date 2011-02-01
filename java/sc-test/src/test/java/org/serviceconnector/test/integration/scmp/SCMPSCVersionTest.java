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
package org.serviceconnector.test.integration.scmp;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.integration.IntegrationSuperTest;
import org.serviceconnector.util.DateTimeUtility;

public class SCMPSCVersionTest extends IntegrationSuperTest {

	private SCRequester requester;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.requester = new SCRequester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
				TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP.getValue(), 1, 0));
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			requester.destroy();
		} catch (Exception e) {
		}
		requester = null;
		super.afterOneTest();
	}

	/**
	 * Description: attach call - SC version is empty<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_AttachCallSCVersionIsEmpty() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(this.requester) {
			@Override
			public void invoke(ISCMPMessageCallback scmpCallback, int timeoutInMillis) throws Exception {
				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};
		TestCallback cbk = new TestCallback(true);
		attachCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.verifyError(result, SCMPError.HV_WRONG_SC_VERSION_FORMAT, SCMPMsgType.ATTACH);
	}

	/**
	 * Description: attach call - SC version is incompatible<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_AttachCallSCVersionIsIncompatible() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(this.requester) {
			@Override
			public void invoke(ISCMPMessageCallback scmpCallback, int timeoutInMillis) throws Exception {
				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "2.0-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};
		TestCallback cbk = new TestCallback(true);
		attachCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.verifyError(result, SCMPError.HV_WRONG_SC_RELEASE_NR, SCMPMsgType.ATTACH);
	}

	/**
	 * Description: attach call - SC version is wrong format<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_AttachCallSCVersionWrongFormat() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(this.requester) {
			@Override
			public void invoke(ISCMPMessageCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "1.1000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};
		TestCallback cbk = new TestCallback(true);
		attachCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		TestUtil.verifyError(result, SCMPError.HV_WRONG_SC_VERSION_FORMAT, SCMPMsgType.ATTACH);
	}

	/**
	 * Description: attach call - SC version is compatible<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_AttachCallSCVersionCompatible() throws Exception {
		SCMPAttachCall attachCall = new SCMPAttachCall(this.requester) {
			@Override
			public void invoke(ISCMPMessageCallback scmpCallback, int timeoutInMillis) throws Exception {

				String dateTime = DateTimeUtility.getCurrentTimeZoneMillis();
				String version = "1.0-000";
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
				this.requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, dateTime);
				this.requestMessage.setMessageType(this.getMessageType());
				this.requester.send(this.requestMessage, timeoutInMillis, scmpCallback);
				return;
			}
		};
		TestCallback cbk = new TestCallback(true);
		attachCall.invoke(cbk, 1000);
		SCMPMessage result = cbk.getMessageSync(3000);
		Assert.assertFalse(result.isFault());
	}
}
