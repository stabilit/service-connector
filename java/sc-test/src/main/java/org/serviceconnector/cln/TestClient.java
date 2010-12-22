/*
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
 */
package org.serviceconnector.cln;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;

public class TestClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestClient.class);

	/**
	 * start test process
	 * 
	 * @param args
	 *            [0] clientType ("session" or "publish")<br>
	 *            [1] client name<br>
	 *            [2] SC host<br>
	 *            [3] SC port<br>
	 *            [4] connectionType ("netty.tcp" or "netty.http")<br>
	 *            [5] maxConnections<br>
	 *            [6] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [7] serviceName<br>
	 *            [8] echoIntervalInSeconds<br>
	 *            [9] echoTimeoutInSeconds<br>
	 *            [10] methodsToInvoke
	 */
	public static void main(String[] args) {
		logger.log(Level.OFF, "TestClient starting ...");
		for (int i = 0; i < args.length; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestAbstractClient client = null;
		if (args[0].equals(TestConstants.COMMUNICATOR_TYPE_SESSION)) {
			client = new TestSessionClient();
			((TestSessionClient) client).setEchoIntervalInSeconds(Integer.parseInt(args[8]));
			((TestSessionClient) client).setEchoTimeoutInSeconds(Integer.parseInt(args[9]));
		} else if (args[0].equals(TestConstants.COMMUNICATOR_TYPE_PUBLISH)) {
			client = new TestPublishClient();
		}
		client.setClientName(args[1]);
		client.setHost(args[2]);
		client.setPort(Integer.parseInt(args[3]));
		client.setConnectionType(args[4]);
		client.setMaxConnections(Integer.parseInt(args[5]));
		client.setKeepAliveIntervalSeconds(Integer.parseInt(args[6]));
		client.setServiceName(args[7]);
		client.setMethodsToInvoke(Arrays.asList(args[10].split("\\|")));
		client.run();
	}
}
