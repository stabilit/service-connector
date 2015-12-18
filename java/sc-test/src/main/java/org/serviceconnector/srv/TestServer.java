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
package org.serviceconnector.srv;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;

public class TestServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TestServer.class);

	/**
	 * start server process
	 * 
	 * @param args
	 *            [0] serverType ("session" or "publish")<br>
	 *            [1] serverName<br>
	 *            [2] listenerPort<br>
	 *            [3] SC port<br>
	 *            [4] maxSessions<br>
	 *            [5] maxConnections<br>
	 *            [6] connectionType ("netty.tcp" or "netty.http")<br>
	 *            [7] serviceNames (comma delimited list)<br>
	 *            [8] nics (comma separated list)<br>
	 */
	public static void main(String[] args) {
		LOGGER.log(Level.OFF, "TestServer starting ...");

		for (int i = 0; i < args.length; i++) {
			LOGGER.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}

		TestStatefulServer server = null;

		if (args[0].equals(TestConstants.COMMUNICATOR_TYPE_SESSION)) {
			server = new TestSessionServer();
		} else if (args[0].equals(TestConstants.COMMUNICATOR_TYPE_PUBLISH)) {
			server = new TestPublishServer();
		}
		server.setServerName(args[1]);
		server.setListenerPort(Integer.parseInt(args[2]));
		server.setPort(Integer.parseInt(args[3]));
		server.setMaxSessions(Integer.parseInt(args[4]));
		server.setMaxConnections(Integer.parseInt(args[5]));
		server.setConnectionType(args[6]);
		server.setServiceNames(args[7]);
		server.setNics(args[8]);
		server.start();
	}
}
