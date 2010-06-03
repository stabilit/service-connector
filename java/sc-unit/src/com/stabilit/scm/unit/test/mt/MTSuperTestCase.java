/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import com.stabilit.scm.cln.client.Client;
import com.stabilit.scm.cln.client.ClientFactory;
import com.stabilit.scm.cln.client.IClient;
import com.stabilit.scm.cln.config.ClientConfig;
import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.util.ReflectionUtil;

/**
 * @author JTraber
 */
@RunWith(Parameterized.class)
public abstract class MTSuperTestCase {

	protected String fileName;
	protected int maxClients;
	protected ClientConfig config = null;
	protected List<IClient> clientList = null;

	public MTSuperTestCase(final String fileName) {
		this.fileName = fileName;
		this.clientList = new ArrayList<IClient>();
	}

	@Parameters
	public static Collection<String[]> getParameters() {
		return Arrays.asList(new String[] { "sc-unit-netty-http.properties" },
				new String[] { "sc-unit-netty-tcp.properties" }, new String[] { "sc-unit-nio-http.properties" },
				new String[] { "sc-unit-nio-tcp.properties" });
	}

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupAll();
	}

	public IClient newClient() {
		try {
			config = new ClientConfig();
			config.load(fileName);
			ClientFactory clientFactory = new ClientFactory();
			IClient client = new Client();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
			this.clientList.add(client);
			return client;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	@After
	public void tearDown() throws Exception {
		for (IClient client : this.clientList) {
			client.disconnect();
			client.destroy();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		for (IClient client : this.clientList) {
			client.disconnect();
			client.destroy();
		}
		ConnectionPoint.getInstance().clearAll();
		clientList = null;
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