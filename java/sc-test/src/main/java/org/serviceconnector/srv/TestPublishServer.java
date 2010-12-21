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
import org.serviceconnector.log.Loggers;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.util.FileUtility;

@SuppressWarnings("unused")
public class TestPublishServer extends TestStatefulServer {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

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
						response.setAppErrorCode(TestConstants.appErrorCode);
						response.setAppErrorText(TestConstants.appErrorText);
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
			SubscriptionLogger.logSubscribe("publish-1", request.getSessionId(), request.getMask());
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
						response.setAppErrorCode(TestConstants.appErrorCode);
						response.setAppErrorText(TestConstants.appErrorText);
					} catch (SCMPValidatorException e) {
					}
				}
			}
			SubscriptionLogger.logChangeSubscribe("publish-1", request.getSessionId(), request.getMask());
			return response;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage request, int operationTimeoutInMillis) {
			SubscriptionLogger.logUnsubscribe("publish-1", request.getSessionId());
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
			this.request = request;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			try {
				// sleep for 1/2 seconds giving time to pass back the original response
				Thread.sleep(500);
				Method method = this.getClass().getMethod(methodName, SCMessage.class, int.class);
				method.invoke(this, request, operationTimeoutInMillis);
			} catch (Exception e1) {
				logger.error("cannot not invoke method:" + methodName, e1);
				return;
			}
		}

		// ==================================================================================
		// methods invoked by name (passed in messageInfo)

		// do nothing
		public void doNothing(SCMessage request, int operationTimeoutInMillis) {
		}

		// publish n compressed messages 128 byte long. n is defined in the request body
		public void publishMessagesCompressed(SCMessage request, int operationTimeoutInMillis) {
			String dataString = (String) request.getData();
			int count = Integer.parseInt(dataString);
			SCPublishMessage pubMessage = new SCPublishMessage(new byte[128]);
			pubMessage.setCompressed(true);
			for (int i = 0; i < count; i++) {
				try {
					pubMessage.setMask(TestConstants.maskSrv);
					pubMessage.setData("publish message nr:" + i);
					this.publishSrv.publish(pubMessage);
					if (((i + 1) % 1000) == 0) {
						TestPublishServer.testLogger.info("Publishing message nr. " + (i + 1));
					}
				} catch (Exception e) {
					logger.error("cannot publish", e);
					break;
				}
			}
		}

		// publish n uncompressed messages 128 byte long. n is defined in the request body
		public void publishMessagesUncompressed(SCMessage request, int operationTimeoutInMillis) {
			String dataString = (String) request.getData();
			int count = Integer.parseInt(dataString);
			SCPublishMessage pubMessage = new SCPublishMessage(new byte[128]);
			pubMessage.setCompressed(false);
			for (int i = 0; i < count; i++) {
				try {
					pubMessage.setMask(TestConstants.maskSrv);
					pubMessage.setData("publish message nr:" + i);
					this.publishSrv.publish(pubMessage);
					if (((i + 1) % 1000) == 0) {
						TestPublishServer.testLogger.info("Publishing message nr. " + (i + 1));
					}
				} catch (Exception e) {
					logger.error("cannot publish", e);
					break;
				}
			}
		}

		// publish n messages 128 byte long with delay w. n is defined in the request body, w in messageInfo
		public void publishMessagesWithDelay(SCMessage request, int operationTimeoutInMillis) {
			String[] dataString = ((String) request.getData()).split("\\|");
			int count = Integer.parseInt(dataString[0]);
			int waitTime = Integer.parseInt(dataString[1]);
			SCPublishMessage pubMessage = new SCPublishMessage(new byte[128]);
			pubMessage.setCompressed(false);
			for (int i = 0; i < count; i++) {
				try {
					pubMessage.setMask(TestConstants.maskSrv);
					pubMessage.setData("publish message nr:" + i);
					this.publishSrv.publish(pubMessage);
					Thread.sleep(waitTime);
					if (((i + 1) % 100) == 0) {
						TestPublishServer.testLogger.info("Publishing message nr. " + (i + 1));
					}
				} catch (Exception e) {
					logger.error("cannot publish", e);
					break;
				}
			}
		}

		// sleep for time defined in the body and send back the same message
		public SCMessage sleep(SCMessage request, int operationTimeoutInMillis) {
			String dataString = (String) request.getData();
			int millis = Integer.parseInt(dataString);
			try {
				logger.info("Sleeping " + millis + "ms");
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				logger.warn("sleep interrupted " + e.getMessage());
			} catch (Exception e) {
				logger.error("sleep error", e);
			}
			return request;
		}
	}
}
