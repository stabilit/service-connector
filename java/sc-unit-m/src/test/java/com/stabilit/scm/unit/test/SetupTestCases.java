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
package com.stabilit.scm.unit.test;

import java.io.File;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.listener.DefaultStatisticsListener;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IStatisticsListener;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.sc.SC;
import com.stabilit.scm.srv.ISCPublishServer;
import com.stabilit.scm.srv.ISCPublishServerCallback;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SCServer;
import com.stabilit.scm.srv.ps.SCPublishServer;
import com.stabilit.scm.unit.UnitCommandFactory;

/**
 * @author JTraber
 */
public class SetupTestCases {

	private static SetupTestCases setupTestCases = null;
	public static IStatisticsListener statisticsListener = new DefaultStatisticsListener();
	private static boolean killPublishServer = false;

	private SetupTestCases() {
	}

	public static void init() throws Exception {
		ResponderConfigPool config = new ResponderConfigPool();
		config.load("sc.properties");

		deleteLog();
		// setup loggers
		try {
//			LoggerFactory loggerFactory = LoggerFactory.getCurrentLoggerFactory(config.getLoggerKey());
//			ConnectionPoint.getInstance().addListener(
//					(IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class));
//			ExceptionPoint.getInstance().addListener(
//					(IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class));
//			LoggerPoint.getInstance().addListener((ILoggerListener) loggerFactory.newInstance(TopLogger.class));
//			LoggerPoint.getInstance().setLevel(Level.DEBUG);
//			PerformancePoint.getInstance().addListener(
//					(IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class));
//			PerformancePoint.getInstance().setOn(true);
//			SessionPoint.getInstance().addListener((ISessionListener) loggerFactory.newInstance(SessionLogger.class));
//			StatisticsPoint.getInstance().addListener(statisticsListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteLog() {
		File logDir = new File("log");

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
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				SC.main(null);
				SetupTestCases.startSessionServer10Connections();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setupSCSessionServer1Connections() {
		if (setupTestCases == null) {
			try {
				init();
				setupTestCases = new SetupTestCases();
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				SC.main(null);
				SetupTestCases.startSessionServer1Connections();
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
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				SC.main(null);
				SetupTestCases.startSessionServer10Connections();
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
				CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
				SC.main(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void startSessionServer1Connections() throws Exception {
		ISCServer scSrv = new SCServer("localhost", 9000);
		// connect to SC as server
		scSrv.setMaxSessions(10);
		scSrv.setKeepAliveInterval(0);
		scSrv.setRunningPortNr(7000);
		scSrv.setImmediateConnect(true);
		scSrv.startServer("localhost");
		SessionServerCallback srvCallback = new SessionServerCallback();
		scSrv.registerService("simulation", srvCallback);
	}

	private static void startSessionServer10Connections() throws Exception {
		ISCServer scSrv = new SCServer("localhost", 9000);
		// connect to SC as server
		scSrv.setMaxSessions(10);
		scSrv.setKeepAliveInterval(0);
		scSrv.setRunningPortNr(7000);
		scSrv.setImmediateConnect(true);
		scSrv.startServer("localhost");
		SessionServerCallback srvCallback = new SessionServerCallback();
		scSrv.registerService("simulation", srvCallback);
	}	

	private static class SessionServerCallback implements ISCSessionServerCallback {

		private static int count = 100;
		
		@Override
		public void abortSession(ISCMessage message) {
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
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
				}
			}
			message.setData(count+"");
			count++;
			return message;
		}

		@Override
		public void deleteSession(ISCMessage message) {
		}

		@Override
		public ISCMessage execute(ISCMessage message) {

			if (message.getData().toString().startsWith("large")) {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				sb.append("large:");
				for (i = 0; i < 100000; i++) {
					if (sb.length() > SCMPMessage.LARGE_MESSAGE_LIMIT + 10000) {
						break;
					}
					sb.append(i);
				}
				message.setData(sb.toString());
				return message;
			}
			message.setData("message data test case");
			return message;
		}
	}

	private static void startPublishServer() throws Exception {
		String serviceName = "publish-simulation";
		ISCPublishServer publishSrv = new SCPublishServer("localhost", 9000);
		// connect to SC as server
		publishSrv.setMaxSessions(10);
		publishSrv.setKeepAliveInterval(0);
		publishSrv.setRunningPortNr(7001);
		publishSrv.setImmediateConnect(true);
		publishSrv.startServer("localhost");
		PublishServerCallback publishCallback = new PublishServerCallback();
		publishSrv.registerService(serviceName, publishCallback);
		Runnable run = new PublishRun(publishSrv, serviceName);
		Thread thread = new Thread(run);
		thread.start();
	}

	private static class PublishServerCallback implements ISCPublishServerCallback {

		@Override
		public ISCMessage changeSubscription(ISCMessage message) {
			return message;
		}

		@Override
		public ISCMessage subscribe(ISCMessage message) {
			return message;
		}

		@Override
		public void unsubscribe(ISCMessage message) {
		}
	}

	private static class PublishRun implements Runnable {
		ISCPublishServer server;
		String serviceName;

		public PublishRun(ISCPublishServer server, String serviceName) {
			this.server = server;
			this.serviceName = serviceName;
		}

		@Override
		public void run() {
			int index = 0;
			while (!killPublishServer) {
				try {
					Thread.sleep(5000);
					Object data = "publish message nr " + ++index;
					String mask = "AVSD-----";
					server.publish(serviceName, mask, data);
				} catch (Exception e) {
					ExceptionPoint.getInstance().fireException(this, e);
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
