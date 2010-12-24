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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;

@RunWith(Parameterized.class)
public class SCMPAttachDetachTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPAttachDetachTest.class);

	private int port;
	private ConnectionType connectionType;

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private SCRequester requester;
	private int threadCount = 0;

	public SCMPAttachDetachTest(Integer port, ConnectionType connectionType) {
		this.port = port;
		this.connectionType = connectionType;
	}

	@Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList( //
				new Object[] { new Integer(TestConstants.PORT_SC_TCP), ConnectionType.NETTY_TCP }, //
				new Object[] { new Integer(TestConstants.PORT_SC_HTTP), ConnectionType.NETTY_HTTP });
	}

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		try {
			requester.destroy();
		} catch (Exception e) {
		}
		requester = null;
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {
		}
		ctrl = null;
	}

	/**
	 * Description: Attach and detach one time to SC on localhost<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_AttachDetach() throws Exception {
		this.requester = new SCRequester(new RequesterContext(TestConstants.HOST, this.port, this.connectionType.getValue(), 0));
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);

		TestCallback callback = new TestCallback();
		attachCall.invoke(callback, 1000);
		SCMPMessage result = callback.getMessageSync(3000);
		TestUtil.checkReply(result);

		Assert.assertNull(result.getBody());
		Assert.assertNull(result.getMessageSequenceNr());
		Assert.assertEquals(SCMPMsgType.ATTACH.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));

		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
		detachCall.invoke(callback, 1000);
		result = callback.getMessageSync(3000);
		TestUtil.checkReply(result);
		Assert.assertNull(result.getBody());
		Assert.assertNull(result.getMessageSequenceNr());
		Assert.assertEquals(SCMPMsgType.DETACH.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
	}
}
