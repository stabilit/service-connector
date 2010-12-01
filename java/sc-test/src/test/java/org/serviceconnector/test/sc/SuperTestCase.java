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
package org.serviceconnector.test.sc;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.serviceconnector.conf.RequesterConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.test.sc.connectionPool.TestContext;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public abstract class SuperTestCase {

	protected String fileName;
	protected RequesterConfiguration config = null;
	protected IRequester req = null;
	protected RequesterContext testContext;
	protected SCMPMessageSequenceNr msgSequenceNr;

	public SuperTestCase(final String fileName) {
		this.fileName = fileName;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	@Parameters
	public static Collection<String[]> getParameters() {
		return Arrays.asList(new String[] { "sc-unit-netty-http.properties" }, new String[] { "sc-unit-netty-tcp.properties" });
	}

	public void setReq(IRequester req) {
		this.req = req;
	}

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupAll();
		try {
			AppContext.initConfiguration(fileName);
			this.config = AppContext.getRequesterConfiguration();
			this.testContext = new TestContext(this.config.getRequesterConfigList().get(0), this.msgSequenceNr);
			req = new SCRequester(this.testContext);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@After
	public void afterOneTest() throws Exception {
		this.testContext.getConnectionPool().destroy();
	}
}