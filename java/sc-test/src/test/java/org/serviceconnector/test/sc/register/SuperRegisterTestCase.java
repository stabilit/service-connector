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
package org.serviceconnector.test.sc.register;

import org.junit.After;
import org.junit.Before;
import org.serviceconnector.TestConstants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.RequesterConfiguration;
import org.serviceconnector.conf.ResponderConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.attach.SuperAttachTestCase;
import org.serviceconnector.test.sc.connectionPool.TestContext;

/**
 * @author JTraber
 */
public abstract class SuperRegisterTestCase extends SuperAttachTestCase {

	protected IRequester registerRequester;
	private RequesterContext registerContext;
	private String registerFileName = TestConstants.SCProperties;
	private RequesterConfiguration registerConfig = null;
	private ResponderConfiguration responderConfig = null;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 */
	public SuperRegisterTestCase(String fileName) {
		super(fileName);
	}

	@Before
	public void setup() throws Exception {
		super.setup();
		AppContext.initConfiguration(registerFileName);
		this.registerConfig = AppContext.getRequesterConfiguration();
		this.responderConfig = AppContext.getResponderConfiguration();
		this.registerContext = new RegisterServerContext(responderConfig.getResponderConfigList().get(0), this.msgSequenceNr);
		this.registerRequester = new SCRequester(registerContext);
		registerServerBefore();
	}

	@After
	public void afterOneTest() throws Exception {
		deRegisterServerAfter();
		super.afterOneTest();
	}

	public void registerServerBefore() throws Exception {
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(
				registerRequester, "publish-1");
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(this.responderConfig.getResponderConfigList().get(0).getPort());
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.attachCallback, 1000);
		SCTest.checkReply(this.attachCallback.getMessageSync());
	}

	public void deRegisterServerAfter() throws Exception {
		this.deRegisterServerAfter("publish-1");
	}

	public void deRegisterServerAfter(String serviceName) throws Exception {
		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(registerRequester, serviceName);
		deRegisterServerCall.invoke(this.attachCallback, 1000);
		SCTest.checkReply(this.attachCallback.getMessageSync());
	}

	private class RegisterServerContext extends TestContext {

		public RegisterServerContext(CommunicatorConfig config, SCMPMessageSequenceNr msgSequenceNr) {
			super(config, msgSequenceNr);
			// for register only 1 connection is allowed
			this.connectionPool.setMaxConnections(1);
		}
	}
}