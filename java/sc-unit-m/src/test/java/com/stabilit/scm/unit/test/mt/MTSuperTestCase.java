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
package com.stabilit.scm.unit.test.mt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.util.ReflectionUtil;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public abstract class MTSuperTestCase {

	protected String fileName;
	protected int maxRequesters;
	protected RequesterConfigPool config = null;
	protected List<IRequester> reqList = null;
	private IRequester registerReq = null;
	protected IContext testContext = null;

	public MTSuperTestCase(final String fileName) {
		this.fileName = fileName;
		this.reqList = new ArrayList<IRequester>();
		this.testContext = new TestContext(this.config.getRequesterConfig());
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

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupSCSessionServer();
		RequesterConfigPool config = new RequesterConfigPool();
		config.load("session-server.properties");
		registerReq = new Requester(this.testContext);
		// scmp registerService
		SCMPRegisterServiceCall registerService = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(registerReq, "simulation");
		registerService.setMaxSessions(9);
		registerService.setPortNumber(7000);
		registerService.setImmediateConnect(true);
		registerService.setKeepAliveInterval(360);
		registerService.invoke();
	}

	public IRequester newReq() {
		try {
			config = new RequesterConfigPool();
			config.load(fileName);
			IRequester req = new Requester(this.testContext);
			this.reqList.add(req);
			return req;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	@After
	public void tearDown() throws Exception {		
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(registerReq, "simulation");

		deRegisterServiceCall.invoke();
		this.testContext.getConnectionPool().destroy();
	}

	@Override
	protected void finalize() throws Throwable {	
		this.testContext.getConnectionPool().destroy();
		ConnectionPoint.getInstance().clearAll();
		reqList = null;
	}

	public static class MTClientThread extends Thread {
		private Object obj;
		private String methodName;

		public MTClientThread(Object obj, String methodName) {
			this.obj = obj;
			this.methodName = methodName;
		}

		@Override
		public void run() {
			try {
				Method method = ReflectionUtil.getMethod(obj.getClass(), methodName);
				method.invoke(this.obj);
			} catch (Exception e) {
				Assert.fail(e.toString());
			}
		}
	}
}