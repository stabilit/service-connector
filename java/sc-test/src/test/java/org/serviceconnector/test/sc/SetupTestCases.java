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
package org.serviceconnector.test.sc;

import java.io.File;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.SC;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;

/**
 * @author JTraber
 */
public class SetupTestCases {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SetupTestCases.class);

	private static SetupTestCases setupTestCases = null;
	private static boolean killPublishServer = false;
	private static boolean large = false;
	private static SCServer scSim1ConSrv;
	private static SCSessionServer scSessionSim1ConSrv;
	private static SCServer scSim10ConSrv;
	private static SCSessionServer scSessionSim10ConSrv;
	private static SCServer scSimEnableSrv;
	private static SCSessionServer scSessionSimEnableSrv;
	private static SCServer scSim1Sess;
	private static SCSessionServer scSessionSim1Sess;

	private SetupTestCases() {
	}

	public static void init() throws Exception {
		AppContext.initConfiguration("sc.properties");
		deleteLog();
		scSimEnableSrv = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, 7001);
		scSimEnableSrv.setKeepAliveIntervalSeconds(0);
		// connect to SC as server
		scSimEnableSrv.setImmediateConnect(true);
		scSimEnableSrv.startListener();
	}

	public static void deleteLog() {
		File logDir = new File("log");
		if (logDir.isDirectory() == false) {
			return;
		}
		for (File file : logDir.listFiles()) {
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".log")) {
					file.delete();
				}
			}
		}
	}

	public static void setupSCSessionServer10Connections() {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
				SetupTestCases.startSessionServer10Connections();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupSCLargeSessionServer10Connections() {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
				SetupTestCases.startLargeSessionServer10Connections();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupSCSessionServer10ConnectionsOverFile(String propertyFileName) {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, propertyFileName });
				SetupTestCases.startSessionServer10Connections();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupSCOverFile(String propertyFileName) {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, propertyFileName });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupSCSessionServer1Connections() throws Exception {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
				SetupTestCases.startSessionServer1Connection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupAll() {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
				SetupTestCases.startSessionServer10Connections();
				SetupTestCases.startSessionServer1Connection();
				SetupTestCases.startSessionServer1Session();
				SetupTestCases.startPublishServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setupSC() {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void startSessionServer1Session() throws Exception {
		scSim1Sess = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, 42000);
		// connect to SC as server
		scSim1Sess.setImmediateConnect(true);
		scSim1Sess.setKeepAliveIntervalSeconds(0);
		scSim1Sess.startListener();
		scSessionSim1Sess = scSim1Sess.newSessionServer("session-1");
		SessionServerCallback srvCallback = new SessionServerCallback(scSessionSim1Sess);
		scSessionSim1Sess.register(1, 1, srvCallback);
	}

	private static void startSessionServer1Connection() throws Exception {
		scSim1ConSrv = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, 41000);
		// connect to SC as server
		scSim1ConSrv.setImmediateConnect(true);
		scSim1ConSrv.setKeepAliveIntervalSeconds(0);
		scSim1ConSrv.startListener();
		scSessionSim1ConSrv = scSim1ConSrv.newSessionServer("session-1");
		SessionServerCallback srvCallback = new SessionServerCallback(scSessionSim1ConSrv);
		scSessionSim1ConSrv.register(1, 1, srvCallback);
	}

	private static void startSessionServer10Connections() throws Exception {
		scSim10ConSrv = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
		// connect to SC as server
		scSim10ConSrv.setImmediateConnect(true);
		scSim10ConSrv.setKeepAliveIntervalSeconds(0);
		scSim10ConSrv.startListener();
		scSessionSim10ConSrv = scSim10ConSrv.newSessionServer("session-1");
		SessionServerCallback srvCallback = new SessionServerCallback(scSessionSim10ConSrv);
		scSessionSim10ConSrv.register(10, 10, srvCallback);
	}

	private static void startLargeSessionServer10Connections() throws Exception {
		scSim10ConSrv = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.PORT_LISTENER);
		// connect to SC as server
		scSim10ConSrv.setImmediateConnect(true);
		scSim10ConSrv.setKeepAliveIntervalSeconds(0);
		scSim10ConSrv.startListener();
		scSessionSim10ConSrv = scSim10ConSrv.newSessionServer("session-1");
		SessionServerCallback srvCallback = new SrvLargeCallback(scSessionSim10ConSrv);
		scSessionSim10ConSrv.register(10, 10, srvCallback);
	}

	public static void registerSessionServiceEnable() throws Exception {
		scSessionSimEnableSrv = scSimEnableSrv.newSessionServer("session-1");
		SessionServerCallback srvCallback = new SessionServerCallback(scSessionSimEnableSrv);
		scSessionSimEnableSrv.register(10, 10, srvCallback);
	}

	public static void deregisterSessionServiceEnable() throws Exception {
		scSessionSimEnableSrv.deregister();
	}

	private static class SessionServerCallback extends SCSessionServerCallback {

		public SessionServerCallback(SCSessionServer scSessionServer) {
			super(scSessionServer);
		}

		private static int count = 100;

		@Override
		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
			this.waitABit();
		}

		@Override
		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
			this.waitABit();
			Object data = message.getData();
			if (data instanceof String) {
				String body = (String) data;
				if (body.startsWith("wait:")) {
					String timeValue = body.substring(5);
					try {
						int time = Integer.parseInt(timeValue);
						Thread.sleep(time);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (body.startsWith("reject")) {
					SCMessageFault fault = new SCMessageFault();
					return fault;
				}
			}
			message.setData(count + "");
			count++;
			return message;
		}

		@Override
		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
			this.waitABit();
		}

		@Override
		public SCMessage execute(SCMessage message, int operationTimeoutInMillis) {
			Object data = message.getData();
			if (data instanceof String) {
				String body = (String) data;
				if (body.startsWith("Performance")) {
					return message;
				}
				this.waitABit();
				if (body.startsWith("large")) {
					StringBuilder sb = new StringBuilder();
					int i = 0;
					sb.append("large:");
					for (i = 0; i < 100000; i++) {
						if (sb.length() > Constants.MAX_MESSAGE_SIZE + 10000) {
							break;
						}
						sb.append(i);
					}
					message.setData(sb.toString());
					return message;
				} else if (body.startsWith("appError")) {
					SCMessageFault fault = new SCMessageFault();
					try {
						fault.setAppErrorCode(500);
						fault.setAppErrorText("appErrorText");
					} catch (SCMPValidatorException e) {
						e.printStackTrace();
					}
					return fault;
				} else if (body.startsWith("reflect")) {
					return message;
				} else if (body.startsWith("excOnServer")) {
					throw new NullPointerException("test purposes");
				} else if (body.startsWith("wait")) {
					String timeValue = body.substring(5);
					try {
						int time = Integer.parseInt(timeValue);
						Thread.sleep(time);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (data instanceof byte[]) {
				if (new String((byte[]) data).startsWith("reflect")) {
					message.setData(data);
					return message;
				}
			}
			message.setData("message data test case");
			return message;
		}

		private void waitABit() {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static class SrvLargeCallback extends SessionServerCallback {

		public SrvLargeCallback(SCSessionServer scSessionServer) {
			super(scSessionServer);
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
			// we return a large message
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 10000; i++) {
				sb.append("this is a large message\r\n");
			}
			Object data = sb.toString();
			request.setCompressed(false);
			request.setData(data);
			return request;
		}
	}

	public static void startPublishServer() throws Exception {
		String serviceName = "publish-1";
		SCServer scPubServer = new SCServer(TestConstants.HOST, TestConstants.PORT_TCP, 51000);
		scPubServer.setImmediateConnect(true);
		scPubServer.startListener();
		SCPublishServer publishSrv = scPubServer.newPublishServer(serviceName);
		// connect to SC as server
		PublishServerCallback publishCallback = new PublishServerCallback(publishSrv);
		publishSrv.register(1, 1, publishCallback);
		Runnable run = new PublishRun(publishSrv, serviceName);
		Thread thread = new Thread(run);
		thread.start();
	}

	private static class PublishServerCallback extends SCPublishServerCallback {

		public PublishServerCallback(SCPublishServer scPublishServer) {
			super(scPublishServer);
		}

		@Override
		public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutInMillis) {
			Object obj = message.getData();
			if (obj != null && obj instanceof String) {
				String data = (String) obj;
				if (data.startsWith("wait")) {
					String timeValue = data.substring(5);
					try {
						int time = Integer.parseInt(timeValue);
						Thread.sleep(time);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return message;
		}

		@Override
		public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
			Object obj = message.getData();
			if (obj != null && obj instanceof String) {
				String data = (String) obj;
				if (data.startsWith("large")) {
					SetupTestCases.large = true;
				} else if (data.startsWith("wait")) {
					String timeValue = data.substring(5);
					try {
						int time = Integer.parseInt(timeValue);
						Thread.sleep(time);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return message;
		}

		@Override
		public void unsubscribe(SCSubscribeMessage message, int operationTimeoutInMillis) {
		}
	}

	private static class PublishRun implements Runnable {
		SCPublishServer server;

		public PublishRun(SCPublishServer server, String serviceName) {
			this.server = server;
		}

		@Override
		public void run() {
			int index = 0;

			StringBuilder sb = new StringBuilder();
			sb.append("large:");
			for (int i = 0; i < 100000; i++) {
				if (sb.length() > Constants.MAX_MESSAGE_SIZE + 10000) {
					break;
				}
				sb.append(i);
			}

			while (!killPublishServer) {
				try {
					if (index % 3 == 0) {
						Thread.sleep(500);
					} else {
						Thread.sleep(100);
					}
					Object data = "publish message nr " + ++index;
					if (SetupTestCases.large) {
						data = sb.toString();
					}
					SCPublishMessage publishMessage = new SCPublishMessage();
					publishMessage.setData(data);
					publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					server.publish(publishMessage);
				} catch (Exception ex) {
					logger.error("run", ex);
				}
			}
		}
	}

	public static void killPublishServer() {
		SetupTestCases.killPublishServer = true;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		SetupTestCases.killPublishServer();
	}
}
