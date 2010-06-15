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

import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.log.listener.ConnectionPoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public abstract class SuperTestCase {

	protected String fileName;
	protected RequesterConfig config = null;
	protected IRequester req = null;

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
		SetupTestCases.setupAll();
		try {
			config = new RequesterConfig();
			config.load(fileName);
			req = new Requester();
			req.setRequesterConfig(config.getRequesterConfig());
			req.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		req.disconnect();
		req.destroy();
	}

	@Override
	protected void finalize() throws Throwable {
		req.disconnect(); // physical disconnect
		req.destroy();
		ConnectionPoint.getInstance().clearAll();
		req = null;
	}
}