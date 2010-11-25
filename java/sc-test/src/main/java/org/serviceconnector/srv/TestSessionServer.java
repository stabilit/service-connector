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
package org.serviceconnector.srv;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.srv.DemoSessionServer.KillThread;
import org.serviceconnector.srv.DemoSessionServer.SrvCallback;

public class TestSessionServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestSessionServer.class);

	private String[] serviceNames;
	private int port;
	private int listenerPort;
	private int maxSessions ;
	private int maxConnections;
	private ThreadSafeCounter ctr;


	/** start server process (wrapper for the case this will be started directly from CLI)
	 * @param args see runSessionServer
	 */
	public static void main(String[] args) throws Exception {
		TestSessionServer sessionServer = new TestSessionServer();
		sessionServer.runSessionServer(args);
	}

	/** start server process
	 * @param args
	 *  [0] listenerPort<br>	
	 *  [1] SC port<br>			
	 *  [2] maxSessions<br>			
	 *  [3] maxConnections<br>			
	 *  [4...] serviceNames<br>		
	 */
	public void runSessionServer(String[] args) {
		logger.log(Level.OFF, "TestSessionServer is running ...");
		this.listenerPort = Integer.parseInt(args[0]);
		this.port = Integer.parseInt(args[1]);
		this.maxSessions = Integer.parseInt(args[2]);
		this.maxConnections = Integer.parseInt(args[3]);
		this.serviceNames = new String[args.length - 4];
		ctr = new ThreadSafeCounter();
		
		SCServer sc = new SCServer("localhost", this.port, this.listenerPort);
		
		try {
			sc.startListener();
	
			SCSessionServer server = sc.newSessionServer(serviceName); // no other params possible

			int maxSess = 10;
			int maxConn = 5;
			SCSessionServerCallback cbk = new SrvCallback(server);
			try {
				server.registerServer(10, this.maxSessions, this.maxConnections, cbk); // regular
			} catch (Exception e) {
				logger.error("runSessionServer", e);
				server.deregisterServer(10);
			}

			
			this.scSrv = new SCSessionServer();
			try {
				System.arraycopy(args, 4, serviceNames, 0, args.length - 4);
			} catch (Exception e) {
				logger.error("incorrect parameters", e);
				shutdown();
			}
			

			// connect to SC as server
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startListener(TestConstants.HOST, listenerPort, 0);

			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());

			for (int i = 0; i < serviceNames.length; i++) {
				this.scSrv.registerServer(TestConstants.HOST, port, serviceNames[i], 1000, maxConnections, srvCallback);
			}


		} catch (Exception e) {
			logger.error("runSessionServer", e);
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			for (int i = 0; i < serviceNames.length; i++) {
				this.scSrv.deregisterServer(serviceNames[i]);
			}
		} catch (Exception e) {
			this.scSrv = null;
		}
	}

	
	class SrvCallback extends SCSessionServerCallback {

		private SCSessionServer scSessionServer;

		public SrvCallback(SCSessionServer server) {
			this.scSessionServer = server;
		}

		@Override
		public SCMessage createSession(SCMessage request, int operationTimeoutInMillis) {
			logger.info("Session created");
			return request;
		}

		@Override
		public void deleteSession(SCMessage request, int operationTimeoutInMillis) {
			logger.info("Session deleted");
		}

		@Override
		public void abortSession(SCMessage request, int operationTimeoutInMillis) {
			logger.info("Session aborted");
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			Object data = request.getData();

			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					KillThread kill = new KillThread(this.scSessionServer);
					kill.start();
				} else {
					logger.info("Message received: " + data);
				}
			}
			return request;
		}
	}

	
	
	class SrvCallback extends SCSessionServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
			logger.info("SessionServer.SrvCallback.createSession()\n" + message.getData());
			if (message.getData() != null && message.getData() instanceof String) {
				String dataString = (String) message.getData();
				if (dataString.equals("reject")) {
					SCMessageFault response = new SCMessageFault();
					response.setCompressed(message.isCompressed());
					response.setData(message.getData());
					response.setMessageInfo(message.getMessageInfo());
					try {
						response.setAppErrorCode(0);
						response.setAppErrorText("\"This is the app error text\"");
					} catch (SCMPValidatorException e) {
						logger.error("rejecting create session", e);
					}
					logger.info("rejecting session");
					return response;
				}
			}
			return message;
		}

		@Override
		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			ctr.increment();

			Object data = request.getData();

			if (data != null) {
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
					} else if (dataString.equals("executed")) {
						ctr.decrement();
						return new SCMessage(String.valueOf(ctr.value()));
					} else if (dataString.startsWith("timeout")) {
						int millis = Integer.parseInt(dataString.split(" ")[1]);
						try {
							logger.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
							Thread.sleep(millis);
						} catch (InterruptedException e) {
							logger.error("sleep in execute", e);
						}
					} else if (dataString.startsWith("register")) {
						String serviceName = dataString.split(" ")[1];
						boolean alreadyPresentService = false;
						for (int i = 0; i < serviceNames.length; i++) {
							if (serviceName.equals(serviceNames[i])) {
								alreadyPresentService = true;
								break;
							}
						}
						if (!alreadyPresentService) {
							if (!scSrv.isRegistered(serviceName)) {
								try {
									scSrv.registerServer(TestConstants.HOST, port, serviceName, 1000, maxConnections,
											new SrvCallback(new SessionServerContext()));
									String[] services = new String[serviceNames.length + 1];
									System.arraycopy(serviceNames, 0, services, 0, serviceNames.length);
									services[serviceNames.length] = serviceName;
									serviceNames = services;
								} catch (Exception e) {
									logger.error("register server " + serviceName, e);
								}
							}
						}
					} else if (dataString.startsWith("deregister")) {
						String serviceName = dataString.split(" ")[1];

						if (scSrv.isRegistered(serviceName)) {
							try {
								scSrv.deregisterServer(serviceName);
								String[] services = new String[serviceNames.length - 1];
								boolean alreadyDeleted = false;
								for (int i = 0; i < serviceNames.length; i++) {
									if (serviceName.equals(serviceNames[i])) {
										alreadyDeleted = true;
									} else if (alreadyDeleted) {
										services[i - 1] = serviceNames[i];
									} else {
										services[i] = serviceNames[i];
									}
								}
								serviceNames = services;
							} catch (Exception e) {
								logger.error("deregister server " + serviceName, e);
							}
						}
					}
				}
			}
			return request;
		}
	}

	private class SessionServerContext {
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
			// sleep before killing the server
			try {
				Thread.sleep(100);
				for (int i = 0; i < serviceNames.length; i++) {
					this.server.deregisterServer(serviceNames[i]);
				}
				this.server.destroy();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}
