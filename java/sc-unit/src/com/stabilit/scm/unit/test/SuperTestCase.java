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

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.unit.TestContext;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public abstract class SuperTestCase {

	protected String fileName;
	protected RequesterConfigPool config = null;
	protected IRequester req = null;
	protected IContext testContext;

	public SuperTestCase(final String fileName) {
		this.fileName = fileName;
	}

	// @Parameters
	// public static Collection<String[]> getParameters() {
	// return Arrays.asList(new String[] { "sc-unit-netty-http.properties" },
	// new String[] { "sc-unit-netty-tcp.properties" }, new String[] { "sc-unit-nio-http.properties" },
	// new String[] { "sc-unit-nio-tcp.properties" });
	// }

	@Parameters
	public static Collection<String[]> getParameters() {
		return Arrays.asList(new String[] { "sc-unit-netty-http.properties" },
				new String[] { "sc-unit-netty-tcp.properties" });
	}

	public void setReq(IRequester req) {
		this.req = req;
	}

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupSCSessionServer();
		try {
			this.config = new RequesterConfigPool();
			this.config.load(fileName);
			this.testContext = new TestContext(this.config.getRequesterConfig());
			req = new Requester(this.testContext);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		System.out.println(SetupTestCases.statisticsListener);
		SetupTestCases.statisticsListener.clearAll();
		this.testContext.getConnectionPool().destroy();
	}

	@Override
	protected void finalize() throws Throwable {
		this.testContext.getConnectionPool().destroy();
		req = null;
	}
}