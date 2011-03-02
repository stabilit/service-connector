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

import org.apache.log4j.Logger;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;

@SuppressWarnings("unused")
public class TestSessionClientJan extends Thread {
	
	/** The Constant LOGGER. */

	private final static Logger LOGGER = Logger.getLogger(TestSessionClientJan.class);

	private ThreadSafeCounter ctr;
	private String scHost;
	private int scPort;
	private int maxConnections;
	private int keepAliveIntervalSeconds;

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] SC host<br>
	 *            [1] SC port<br>
	 *            [2] connectionType<br>
	 *            [3] maxConnections<br>
	 *            [4] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [5] serviceName
	 */
	public static void main(String[] args) throws Exception {
//		TestSessionClient server = new TestSessionClient();
//		server.setListenerPort(Integer.parseInt(args[0]));
//		server.setPort(Integer.parseInt(args[1]));
//		server.setMaxSessions(Integer.parseInt(args[2]));
//		server.setMaxConnections(Integer.parseInt(args[3]));
//		server.setServiceNames(args[4]);
//		server.run();
	}
}
		
//	@Override
//	public void run() {
//		LOGGER.log(Level.OFF, "TestSessionServer is running ...");
//		ctr = new ThreadSafeCounter();
//		SCClient sc = new SCClient(TestConstants.HOST, this.scPort);
//		try {
//			sc.setKeepAliveIntervalSeconds(this.keepAliveIntervalSeconds);
//
//<<<<<<< .mine		} catch (Exception e) {
//			LOGGER.error("runSessionServer", e);
//		} finally {
//			try {
//				sc.detach();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//=======			if (getMethodName() == "createSession_whiteSpaceSessionInfo_sessionIdIsNotEmpty") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage();
//				scMessage.setSessionInfo(" ");
//				sessionService.createSession(60, scMessage);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	public void setListenerPort(int listenerPort) {
//		this.scPort = listenerPort;
//	}
//=======			} else if (getMethodName() == "createSession_arbitrarySpaceSessionInfo_sessionIdIsNotEmpty") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage();
//				scMessage.setSessionInfo("The quick brown fox jumps over a lazy dog.");
//				sessionService.createSession(60, scMessage);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	public void setPort(int port) {
//		this.scPort = port;
//	}
//=======			} else if (getMethodName() == "createSession_arbitrarySpaceSessionInfoDataOneChar_sessionIdIsNotEmpty") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage("a");
//				scMessage.setSessionInfo("The quick brown fox jumps over a lazy dog.");
//				sessionService.createSession(10, scMessage);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	public void setMaxSessions(int maxSessions) {
//		this.keepAliveIntervalSeconds = maxSessions;
//	}
//=======			} else if (getMethodName() == "createSession_256LongSessionInfoData60kBByteArray_sessionIdIsNotEmpty") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage(new byte[TestConstants.dataLength60kB]);
//				scMessage.setSessionInfo(TestConstants.stringLength256);
//				sessionService.createSession(60, scMessage);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	public void setMaxConnections(int maxConnections) {
//		this.maxConnections = maxConnections;
//	}
//=======			} else if (getMethodName() == "deleteSession_beforeCreateSession_noSessionId") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	public void setServiceNames(String serviceNames) {
//		this.serviceName = serviceNames;
//	}
//=======			} else if (getMethodName() == "deleteSession_afterValidNewSessionService_noSessionId") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage();
//				scMessage.setSessionInfo("sessionInfo");
//				sessionService.createSession(60, scMessage);
//				sessionService.deleteSession();
//>>>>>>> .theirs
//<<<<<<< .mine	/**
//	 * Callback handling all server events
//	 * 
//	 * @author JTrnka
//	 */
//	class SrvCallback extends SCSessionServerCallback {
//=======			} else if (getMethodName() == "createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//>>>>>>> .theirs
//		public SrvCallback(SCSessionServer server) {
//			super(server);
//		}
//
//		@Override
//		public SCMessage createSession(SCMessage request, int operationTimeoutMillis) {
//			Object data = request.getData();
//
//<<<<<<< .mine			SCMessage response = request;
//			// watch out for kill server message
//			if (data.getClass() == String.class) {
//				String dataString = (String) data;
//=======			} else if (getMethodName() == "execute_messageData1MBArray_returnsTheSameMessageData") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage();
//				scMessage.setSessionInfo("sessionInfo");
//				sessionService.createSession(60, scMessage);
//>>>>>>> .theirs
//				if (dataString.equals(TestConstants.killServerCmd)) {
//					response = new SCMessageFault();
//					try {
//						((SCMessageFault) response).setAppErrorCode(1050);
//						((SCMessageFault) response).setAppErrorText("create session rejected - kill server requested!");
//					} catch (SCMPValidatorException e) {
//					}
//					KillThread kill = new KillThread(this.scSessionServer);
//					kill.start();
//				}
//			}
//			sessionLogger.logCreateSession(this.getClass().getName(), request.getSessionId());
//			return response;
//		}
//
//		@Override
//		public void deleteSession(SCMessage request, int operationTimeoutMillis) {
//			sessionLogger.logDeleteSession(this.getClass().getName(), request.getSessionId());
//		}
//
//<<<<<<< .mine		@Override
//		public void abortSession(SCMessage request, int operationTimeoutMillis) {
//			sessionLogger.logAbortSession(this.getClass().getName(), request.getSessionId());
//		}
//=======			} else if (getMethodName() == "createSessionExecuteDeleteSession_twice_6MessagesArrive") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//>>>>>>> .theirs
//		@Override
//		public SCMessage execute(SCMessage request, int operationTimeoutMillis) {
//			return request;
//		}
//	}
//
//	private class KillThread extends Thread {
//		private SCSessionServer server;
//
//<<<<<<< .mine		public KillThread(SCSessionServer server) {
//			this.server = server;
//		}
//=======			} else if (getMethodName() == "echo_waitFor3EchoMessages_5MessagesArrive") {
//				SCSessionService sessionService = client.newSessionService(TestConstants.sesServiceName1);
//				SCMessage scMessage = new SCMessage();
//				scMessage.setSessionInfo("sessionInfo");
//				sessionService.createSession(1, scMessage);
//				Thread.sleep(6000);
//				sessionService.deleteSession();
//			}
//>>>>>>> .theirs
//		@Override
//		public void run() {
//			// sleep for 2 seconds before killing the server
//			try {
//				Thread.sleep(2000);
//				this.server.deregister();
//				this.server.getSCServer().stopListener();
//				System.exit(0);
//			} catch (Exception e) {
//				LOGGER.error("run", e);
//			}
//		}
//	}
//}

// class SrvCallback extends SCSessionServerCallback {
// private SessionServerContext outerContext;
// public SrvCallback(SessionServerContext context) {
// this.outerContext = context;
// }
//
// @Override
// public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
// LOGGER.info("SessionServer.SrvCallback.createSession()\n" + message.getData());
// if (message.getData() != null && message.getData() instanceof String) {
// String dataString = (String) message.getData();
// if (dataString.equals("reject")) {
// SCMessageFault response = new SCMessageFault();
// response.setCompressed(message.isCompressed());
// response.setData(message.getData());
// response.setMessageInfo(message.getMessageInfo());
// try {
// response.setAppErrorCode(0);
// response.setAppErrorText("\"This is the app error text\"");
// } catch (SCMPValidatorException e) {
// LOGGER.error("rejecting create session", e);
// }
// LOGGER.info("rejecting session");
// return response;
// }
// }
// return message;
// }
//
// @Override
// public void deleteSession(SCMessage message, int operationTimeoutMillis) {
// LOGGER.trace("SessionServer.SrvCallback.deleteSession()");
// }
//
// @Override
// public void abortSession(SCMessage message, int operationTimeoutMillis) {
// LOGGER.trace("SessionServer.SrvCallback.abortSession()");
// }
//
// @Override
// public SCMessage execute(SCMessage request, int operationTimeoutMillis) {
// ctr.increment();
//
// Object data = request.getData();
//
// if (data != null) {
// // watch out for kill server message
// if (data.getClass() == String.class) {
// String dataString = (String) data;
// if (dataString.equals("kill server")) {
// try {
// KillThread kill = new KillThread(this.outerContext.getServer());
// kill.start();
// } catch (Exception e) {
// LOGGER.error("execute", e);
// }
// } else if (dataString.equals("executed")) {
// ctr.decrement();
// return new SCMessage(String.valueOf(ctr.value()));
// } else if (dataString.startsWith("timeout")) {
// int millis = Integer.parseInt(dataString.split(" ")[1]);
// try {
// LOGGER.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
// Thread.sleep(millis);
// } catch (InterruptedException e) {
// LOGGER.error("sleep in execute", e);
// }
// } else if (dataString.startsWith("register")) {
// String serviceName = dataString.split(" ")[1];
// boolean alreadyPresentService = false;
// for (int i = 0; i < serviceNames.length; i++) {
// if (serviceName.equals(serviceNames[i])) {
// alreadyPresentService = true;
// break;
// }
// }
// if (!alreadyPresentService) {
// if (!scSrv.isRegistered(serviceName)) {
// try {
// scSrv.registerServer(TestConstants.HOST, port, serviceName, 1000, maxConnections,
// new SrvCallback(new SessionServerContext()));
// String[] services = new String[serviceNames.length + 1];
// System.arraycopy(serviceNames, 0, services, 0, serviceNames.length);
// services[serviceNames.length] = serviceName;
// serviceNames = services;
// } catch (Exception e) {
// LOGGER.error("register server " + serviceName, e);
// }
// }
// }
// } else if (dataString.startsWith("deregister")) {
// String serviceName = dataString.split(" ")[1];
//
// if (scSrv.isRegistered(serviceName)) {
// try {
// scSrv.deregisterServer(serviceName);
// String[] services = new String[serviceNames.length - 1];
// boolean alreadyDeleted = false;
// for (int i = 0; i < serviceNames.length; i++) {
// if (serviceName.equals(serviceNames[i])) {
// alreadyDeleted = true;
// } else if (alreadyDeleted) {
// services[i - 1] = serviceNames[i];
// } else {
// services[i] = serviceNames[i];
// }
// }
// serviceNames = services;
// } catch (Exception e) {
// LOGGER.error("deregister server " + serviceName, e);
// }
// }
// }
// }
// }
// return request;
// }
// }
