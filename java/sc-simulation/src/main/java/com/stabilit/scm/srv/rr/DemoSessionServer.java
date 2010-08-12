/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.srv.rr;

import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SCServer;

public class DemoSessionServer {
	private ISCServer scSrv = null;
	private String serviceName = "simulation";

	public static void main(String[] args) throws Exception {
		DemoSessionServer sessionServer = new DemoSessionServer();
		sessionServer.runSessionServer();
	}

	public void runSessionServer() {
		try {
			this.scSrv = new SCServer("localhost", 9000);

			// connect to SC as server
			this.scSrv.setMaxSessions(10);
			this.scSrv.setKeepAliveInterval(0);
			this.scSrv.setRunningPortNr(7000);
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startServer("localhost");
			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());
			this.scSrv.registerService(serviceName, srvCallback);
		} catch (Exception e) {
			e.printStackTrace();
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			this.scSrv.deregisterService(serviceName);
		} catch (Exception e) {
			this.scSrv = null;
		}
	}

	class SrvCallback implements ISCSessionServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public void deleteSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
			Object data = request.getData();
			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						KillThread kill = new KillThread(this.outerContext.getServer(), serviceName);
						kill.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			return request;
		}
	}

	private class SessionServerContext {
		public ISCServer getServer() {
			return scSrv;
		}
	}

	private class KillThread extends Thread {

		private ISCServer server;
		private String serviceName;

		public KillThread(ISCServer server, String serviceName) {
			this.server = server;
			this.serviceName = serviceName;
		}

		@Override
		public void run() {
			// sleep for 2 seconds before killing the server
			try {
				Thread.sleep(2000);
				this.server.deregisterService(this.serviceName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}