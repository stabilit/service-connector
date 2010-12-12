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

import java.lang.reflect.Method;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.util.FileUtility;

@SuppressWarnings("unused")
public class TestPublishServer extends TestStatefulServer {

	static {
		TestStatefulServer.logger = Logger.getLogger(TestPublishServer.class);
	}

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] serverName<br>
	 *            [1] listenerPort<br>
	 *            [2] SC port<br>
	 *            [3] maxSessions<br>
	 *            [4] maxConnections<br>
	 *            [5] connectionType ("netty.tcp" or "netty.http")<br> 
	 *            [6] serviceNames (comma delimited list)<br>
	 */
	public static void main(String[] args) throws Exception {
		logger.log(Level.OFF, "TestPublishServer is starting ...");
		for (int i = 0; i < 7; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestPublishServer server = new TestPublishServer();
		server.setServerName(args[0]);
		server.setListenerPort(Integer.parseInt(args[1]));
		server.setPort(Integer.parseInt(args[2]));
		server.setMaxSessions(Integer.parseInt(args[3]));
		server.setMaxConnections(Integer.parseInt(args[4]));
		server.setConnectionType(args[5]);
		server.setServiceNames(args[6]);
		server.run();
	}

	@Override
	public void run() {
		// add exit handler
		try {
			this.addExitHandler(FileUtility.getPath() + fs + this.serverName + ".pid");
		} catch (SCMPValidatorException e1) {
			logger.fatal("unable to get path to pid file", e1);
		}

		ctr = new ThreadSafeCounter();
		SCServer sc = new SCServer(TestConstants.HOST, this.port, this.listenerPort, this.connectionType);
		try {
			sc.setKeepAliveIntervalSeconds(10);
			sc.setImmediateConnect(true);
			sc.startListener();

			String[] serviceNames = this.serviceNames.split(",");
			for (String serviceName : serviceNames) {
				SCPublishServer server = sc.newPublishServer(serviceName);
				SCPublishServerCallback cbk = new SrvCallback(server);
				try {
					server.register(10, this.maxSessions, this.maxConnections, cbk);
				} catch (Exception e) {
					logger.error("runPublishServer", e);
					server.deregister();
				}
			}
			FileUtility.createPIDfile(FileUtility.getPath() + fs + this.serverName + ".pid");
			logger.log(Level.OFF, "TestPublishServer is running ...");
		} catch (Exception e) {
			logger.error("runPublishServer", e);
		} finally {
			try {
				// publishSrv.deregisterServer();
				// publishSrv.deregisterServer(10, serviceName);
			} catch (Exception e1) {
				logger.error("run", e1);
			}
			// sc.stopListener();
		}
	}

	private class SrvCallback extends SCPublishServerCallback {

		public SrvCallback(SCPublishServer publishSrv) {
			super(publishSrv);
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage request, int operationTimeoutInMillis) {
			SCMessage response = request;
			String sessionInfo = request.getSessionInfo();
			if (sessionInfo != null) {
				// watch out for kill server message
				if (sessionInfo.equals(TestConstants.killServerCmd)) {
					logger.log(Level.OFF, "Kill request received, exiting ...");
					try {
						response.setAppErrorCode(1050);
						response.setAppErrorText("kill server requested!");
					} catch (SCMPValidatorException e) {
					}
					KillThread<SCPublishServer> kill = new KillThread<SCPublishServer>(this.scPublishServer);
					kill.start();
				// watch out for reject request
				} else if (sessionInfo.equals(TestConstants.rejectSessionCmd)) {
					try {
						response.setReject(true);
						response.setAppErrorCode(4000);
						response.setAppErrorText("session rejected intentionaly!");
					} catch (SCMPValidatorException e) {
					}
				} else {
					String methodName = request.getSessionInfo();
					PublishThread publishThread = new PublishThread(this.scPublishServer, methodName, request,
							operationTimeoutInMillis);
					publishThread.start();
					return response;
				}
			}
			subscriptionLogger.logSubscribe("publish-1", request.getSessionId(), request.getMask());
			return response;
		}

		@Override
		public SCMessage changeSubscription(SCSubscribeMessage request, int operationTimeoutInMillis) {
			SCMessage response = request;
			String sessionInfo = request.getSessionInfo();
			if (sessionInfo != null) {
				// watch out for reject request
				if (sessionInfo.equals(TestConstants.rejectSessionCmd)) {
					try {
						response.setReject(true);
						response.setAppErrorCode(4000);
						response.setAppErrorText("session rejected intentionaly!");
					} catch (SCMPValidatorException e) {
					}
				}
			}
			subscriptionLogger.logChangeSubscribe("publish-1", request.getSessionId(), request.getMask());
			return response;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage request, int operationTimeoutInMillis) {
			subscriptionLogger.logUnsubscribe("publish-1", request.getSessionId());
		}
	}

	private class PublishThread extends Thread {
		SCPublishServer publishSrv;
		String methodName;
		SCMessage request;
		int operationTimeoutInMillis = 0;

		public PublishThread(SCPublishServer publishSrv, String methodName, SCMessage request, int operationTimeoutInMillis) {
			this.publishSrv = publishSrv;
			this.methodName = methodName;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			try {
				Method method = this.getClass().getMethod(methodName, SCMessage.class, int.class);
				// first sleep 1 second to give test client time to stay ready
				Thread.sleep(1000);
				method.invoke(this, request, operationTimeoutInMillis);
			} catch (Exception e1) {
				logger.warn("cannot not invoke " + methodName);
				return;
			}
		}

		/**
		 * This method might get invoked by reflection if client requests it in sessionInfo of a subscribe message
		 * 
		 * @param request
		 * @param operationTimeoutInMillis
		 */
		public void doNothing(SCMessage request, int operationTimeoutInMillis) {
		}

		public void publish100Message(SCMessage request, int operationTimeoutInMillis) {
			SCPublishMessage pubMessage = new SCPublishMessage();
			for (int i = 0; i < 100; i++) {
				try {
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					pubMessage.setData("publish message nr : " + i);
					this.publishSrv.publish(pubMessage); // regular
					Thread.sleep(1000);
				} catch (Exception e) {
					// quit loop in case of a publish error
					break;
				}
			}
		}

	}
}
