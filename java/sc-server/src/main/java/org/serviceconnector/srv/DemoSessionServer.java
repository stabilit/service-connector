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
package org.serviceconnector.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.api.srv.SCSessionServer;

public class DemoSessionServer {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionServer.class);

	private SCSessionServer scSrv = null;
	private String serviceName = "simulation";

	public static void main(String[] args) throws Exception {
		DemoSessionServer sessionServer = new DemoSessionServer();
		sessionServer.runSessionServer();
	}

	/*
	public void runSessionServer() {
	
		SCServer sc = new SCServer("localhost", 9000, 9001);		// regular, defaults documented in javadoc
		SCServer sc = new SCServer("localhost", 9000, 9001, ConnectionType.NETTY-HTTP);	// alternative with connection type
	
		try {
			sc.setConnectionType(ConnectionType.NETTY-HTTP);		// can be set before start listener
			sc.setHost("localhost");								// can be set before start listener
			sc.setPort(9000);										// can be set before start listener
			sc.setListenerPort(9001);								// can be set before start listener
			sc.setKeepaliveIntervalInSeconds(10);					// can be set before register
			sc.setImmediateConnect(true);							// can be set before register
			
			sc.startListener()										// regular
			sc.startListener(10);									// alternative with operation timeout
			
			String serviceName = "simulation";
			SCSessionServer server = sc.newSessionServer(serviceName);	// no other params possible
			
			int maxSessions = 10;
			int maxConnections = 5;
			SCSessionServerCallback cbk = new SrvCallback(server);
			try {
				server.register(maxSessions, maxConnections, cbk);	//	regular
				server.register(maxSessions, maxConnections, cbk, 10);	// alternative with operation timeout
			} catch	(Exception e) {
				logger.error("runSessionServer", e);
				server.deregister();
			}
		} catch (Exception e) {
			logger.error("runSessionServer", e);
		} finally {
			sc.stopListener();
		}
	}
	 */
	
	
	public void runSessionServer() {
		try {
			this.scSrv = new SCSessionServer();

			// connect to SC as server
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startListener("localhost", 9001, 0);
			SrvCallback srvCallback = new SrvCallback(new SrvContext());
			this.scSrv.registerServer("localhost", 9000, serviceName, 10, 10, srvCallback);
		} catch (Exception e) {
			logger.error("runSessionServer", e);
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			this.scSrv.deregisterServer(serviceName);
		} catch (Exception e) {
			logger.error("shutdown", e);
			this.scSrv = null;
		}
	}

	class SrvCallback extends SCSessionServerCallback {

		private SrvContext outerContext;

		public SrvCallback(SrvContext context) {
			this.outerContext = context;
		}

		@Override
		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
			logger.info("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
			logger.info("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
			logger.info("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			Object data = request.getData();
			logger.info("Message received: " + data);
			// TODO JOT
			// int executionTimeout = request.getOperationTimeout();
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

	private class SrvContext {
		public SCSessionServer getServer() {
			return scSrv;
		}
	}

	private class KillThread extends Thread {

		private SCSessionServer server;

		public KillThread(SCSessionServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep for 2 seconds before killing the server
			try {
				Thread.sleep(2000);
				this.server.deregisterServer(serviceName);
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}