/*
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
 */
package org.serviceconnector.srv;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.log.SessionLogger;

public class TestSessionServer extends Thread {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestSessionServer.class);
	/** The Constant sessionLogger. */
	private final static SessionLogger sessionLogger = SessionLogger.getInstance();

	private ThreadSafeCounter ctr;
	
	private int listenerPort;
	private int port;
	private int maxSessions;
	private int maxConnections;
	private String serviceNames;
	
	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *  [0] listenerPort<br>	
	 *  [1] SC port<br>			
	 *  [2] maxSessions<br>			
	 *  [3] maxConnections<br>			
	 *  [4] serviceNames (comma delimited list)<br>		
	 */
	public static void main(String[] args) throws Exception {
		TestSessionServer server = new TestSessionServer();
		server.setListenerPort(Integer.parseInt(args[0]));
		server.setPort(Integer.parseInt(args[1]));
		server.setMaxSessions(Integer.parseInt(args[2]));
		server.setMaxConnections(Integer.parseInt(args[3]));
		server.setServiceNames(args[4]);	
		server.run();
	}
	
	@Override
	public void run() {
		logger.log(Level.OFF, "TestSessionServer is running ...");
		ctr = new ThreadSafeCounter();		
		SCServer sc = new SCServer("localhost", this.port, this.listenerPort);
		try {		
			sc.setKeepAliveIntervalInSeconds(10);
			sc.setImmediateConnect(true);
			sc.startListener();
			
			String serviceName = this.serviceNames;	//TODO TRN handle multiple services
//			for (int i = 0; i < serviceNames.length; i++) {
//			}
			SCSessionServer server = sc.newSessionServer(serviceName);
			SCSessionServerCallback cbk = new SrvCallback(server);
			try {
				server.register(10, this.maxSessions, this.maxConnections, cbk);
			} catch (Exception e) {
				logger.error("runSessionServer", e);
				server.deregister();
			}
			// server.destroy();
		} catch (Exception e) {
			logger.error("runSessionServer", e);
		} finally {
			// sc.stopListener();
		}
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setServiceNames(String serviceNames) {
		this.serviceNames = serviceNames;
	}

	
	/**
	 * Callback handling all server events
	 * 
	 * @author JTrnka
	 *
	 */
	class SrvCallback extends SCSessionServerCallback {
		private SCSessionServer scSessionServer;
		
		public SrvCallback(SCSessionServer server) {
			this.scSessionServer = server;
		}

		@Override
		public SCMessage createSession(SCMessage request, int operationTimeoutInMillis) {
			sessionLogger.logCreateSession(this.getClass().getName(), request.getSessionId());
			return request;
		}

		@Override
		public void deleteSession(SCMessage request, int operationTimeoutInMillis) {
			sessionLogger.logDeleteSession(this.getClass().getName(), request.getSessionId());
		}

		@Override
		public void abortSession(SCMessage request, int operationTimeoutInMillis) {
			sessionLogger.logAbortSession(this.getClass().getName(), request.getSessionId());
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			Object data = request.getData();

			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals(TestConstants.killServerCmd)) {
					KillThread kill = new KillThread(this.scSessionServer);
					kill.start();
				} else {
					logger.info("Message received: " + data);
				}
			}
			return request;
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
				this.server.deregister();
				//SCServer sc = server.getSCServer().stopListener();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}
	
//	class SrvCallback extends SCSessionServerCallback {
//		private SessionServerContext outerContext;
//		public SrvCallback(SessionServerContext context) {
//			this.outerContext = context;
//		}
//
//		@Override
//		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
//			logger.info("SessionServer.SrvCallback.createSession()\n" + message.getData());
//			if (message.getData() != null && message.getData() instanceof String) {
//				String dataString = (String) message.getData();
//				if (dataString.equals("reject")) {
//					SCMessageFault response = new SCMessageFault();
//					response.setCompressed(message.isCompressed());
//					response.setData(message.getData());
//					response.setMessageInfo(message.getMessageInfo());
//					try {
//						response.setAppErrorCode(0);
//						response.setAppErrorText("\"This is the app error text\"");
//					} catch (SCMPValidatorException e) {
//						logger.error("rejecting create session", e);
//					}
//					logger.info("rejecting session");
//					return response;
//				}
//			}
//			return message;
//		}
//
//		@Override
//		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
//			logger.trace("SessionServer.SrvCallback.deleteSession()");
//		}
//
//		@Override
//		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
//			logger.trace("SessionServer.SrvCallback.abortSession()");
//		}
//
//		@Override
//		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
//			ctr.increment();
//
//			Object data = request.getData();
//
//			if (data != null) {
//				// watch out for kill server message
//				if (data.getClass() == String.class) {
//					String dataString = (String) data;
//					if (dataString.equals("kill server")) {
//						try {
//							KillThread kill = new KillThread(this.outerContext.getServer());
//							kill.start();
//						} catch (Exception e) {
//							logger.error("execute", e);
//						}
//					} else if (dataString.equals("executed")) {
//						ctr.decrement();
//						return new SCMessage(String.valueOf(ctr.value()));
//					} else if (dataString.startsWith("timeout")) {
//						int millis = Integer.parseInt(dataString.split(" ")[1]);
//						try {
//							logger.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
//							Thread.sleep(millis);
//						} catch (InterruptedException e) {
//							logger.error("sleep in execute", e);
//						}
//					} else if (dataString.startsWith("register")) {
//						String serviceName = dataString.split(" ")[1];
//						boolean alreadyPresentService = false;
//						for (int i = 0; i < serviceNames.length; i++) {
//							if (serviceName.equals(serviceNames[i])) {
//								alreadyPresentService = true;
//								break;
//							}
//						}
//						if (!alreadyPresentService) {
//							if (!scSrv.isRegistered(serviceName)) {
//								try {
//									scSrv.registerServer(TestConstants.HOST, port, serviceName, 1000, maxConnections,
//											new SrvCallback(new SessionServerContext()));
//									String[] services = new String[serviceNames.length + 1];
//									System.arraycopy(serviceNames, 0, services, 0, serviceNames.length);
//									services[serviceNames.length] = serviceName;
//									serviceNames = services;
//								} catch (Exception e) {
//									logger.error("register server " + serviceName, e);
//								}
//							}
//						}
//					} else if (dataString.startsWith("deregister")) {
//						String serviceName = dataString.split(" ")[1];
//
//						if (scSrv.isRegistered(serviceName)) {
//							try {
//								scSrv.deregisterServer(serviceName);
//								String[] services = new String[serviceNames.length - 1];
//								boolean alreadyDeleted = false;
//								for (int i = 0; i < serviceNames.length; i++) {
//									if (serviceName.equals(serviceNames[i])) {
//										alreadyDeleted = true;
//									} else if (alreadyDeleted) {
//										services[i - 1] = serviceNames[i];
//									} else {
//										services[i] = serviceNames[i];
//									}
//								}
//								serviceNames = services;
//							} catch (Exception e) {
//								logger.error("deregister server " + serviceName, e);
//							}
//						}
//					}
//				}
//			}
//			return request;
//		}
//	}
