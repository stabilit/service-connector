/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.service.ISCMessage;
import org.serviceconnector.srv.ISCSessionServer;
import org.serviceconnector.srv.ISCSessionServerCallback;
import org.serviceconnector.srv.SCSessionServer;


public class DemoSessionServer {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionServer.class);
	
	private ISCSessionServer scSrv = null;
	private String serviceName = "simulation";

	public static void main(String[] args) throws Exception {
		DemoSessionServer sessionServer = new DemoSessionServer();
		sessionServer.runSessionServer();
	}

	public void runSessionServer() {
		try {
			this.scSrv = new SCSessionServer();

			// connect to SC as server
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startListener("localhost", 7100, 0);
			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());
			this.scSrv.registerService("localhost", 9000, serviceName, 10, 10, srvCallback);
		} catch (Exception e) {
			logger.error("runSessionServer", e);
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			this.scSrv.deregisterService(serviceName);
		} catch (Exception e) {
			logger.error("shutdown", e);
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
			logger.info("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public void deleteSession(ISCMessage message) {
			logger.info("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(ISCMessage message) {
			logger.info("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
			Object data = request.getData();
			int executionTimeout = request.getOperationTimeout();
			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						KillThread kill = new KillThread(this.outerContext.getServer());
						kill.start();
					} catch (Exception e) {
						logger.error("execute", e);
					}
				} else {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logger.error("execute", e);
					}
				}
			}
			return request;
		}
	}

	private class SessionServerContext {
		public ISCSessionServer getServer() {
			return scSrv;
		}
	}

	private class KillThread extends Thread {

		private ISCSessionServer server;

		public KillThread(ISCSessionServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep for 2 seconds before killing the server
			try {
				Thread.sleep(2000);
				this.server.deregisterService(serviceName);
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}