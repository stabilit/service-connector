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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.net.ConnectionType;

public class DemoSessionServer extends Thread {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemoSessionServer.class);

	/**
	 * Main method if you like to start in debug mode.
	 */
	public static void main(String[] args) throws Exception {
		DemoSessionServer sessionServer = new DemoSessionServer();
		sessionServer.run();
	}

	public SCSessionServerCallback newSrvCallback(SCSessionServer server) {
		SCSessionServerCallback cbk = new SrvCallback(server);
		return cbk;
	}

	@Override
	public void run() {

		List<String> nics = new ArrayList<String>();
		nics.add("localhost");

		SCServer sc = new SCServer("localhost", 9000, nics, 9002, ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE);

		try {
			sc.setKeepAliveIntervalSeconds(10); // can be set before register
			sc.setImmediateConnect(true); // can be set before register
			sc.startListener(); // regular

			String serviceName = "session-1";
			SCSessionServer server = sc.newSessionServer(serviceName); // no other params possible
			int maxSess = 100;
			int maxConn = 10;
			SCSessionServerCallback cbk = newSrvCallback(server);
			try {
				server.register(maxSess, maxConn, cbk); // regular
				server.checkRegistration();
				// server.registerServer(10, maxSess, maxConn, cbk); //
				// alternative with operation timeout
			} catch (Exception e) {
				LOGGER.error("runSessionServer", e);
				server.deregister();
				throw e;
			}
		} catch (Exception e) {
			LOGGER.error("runSessionServer", e);
			sc.stopListener();
			sc.destroy();
		}
	}

	class SrvCallback extends SCSessionServerCallback {

		public SrvCallback(SCSessionServer server) {
			super(server);
		}

		@Override
		public SCMessage createSession(SCMessage request, int operationTimeoutMillis) {
			LOGGER.info("Session created");
			return request;
		}

		@Override
		public void deleteSession(SCMessage request, int operationTimeoutMillis) {
			LOGGER.info("Session deleted");
			String sessionInfo = request.getSessionInfo();
			// watch out for kill server message
			if (sessionInfo != null) {
				if (sessionInfo.equals("kill server")) {
					System.out.println("DemoSessionServer.SrvCallback.deleteSession() kill server received");
					KillThread kill = new KillThread(this.scSessionServer);
					kill.start();
				}
			}
		}

		@Override
		public void abortSession(SCMessage request, int operationTimeoutMillis) {
			LOGGER.info("Session aborted");
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutMillis) {
			Object data = request.getData();
			if (request.getCacheId() != null) {
				Calendar time = Calendar.getInstance();
				time.add(Calendar.HOUR_OF_DAY, 1);
				request.setCacheExpirationDateTime(time.getTime());
			}
			System.out.println("DemoSessionServer.SrvCallback.execute() " + data);
			return request;
		}

		@Override
		public void exceptionCaught(SCServiceException ex) {
			LOGGER.error("exception caught");
		}
	}

	protected class KillThread extends Thread {
		private SCSessionServer server;

		public KillThread(SCSessionServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep before killing the server
			try {
				Thread.sleep(200);
				this.server.deregister();
			} catch (Exception e) {
				LOGGER.error("run", e);
			} finally {
				SCServer sc = server.getSCServer();
				sc.stopListener();
				sc.destroy();
			}
		}
	}
}
