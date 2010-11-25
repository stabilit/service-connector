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

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.ctrl.util.ProcessesController;

public class TestPublishServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestPublishServer.class);

	private SCPublishServer publishSrv = null;
	private String startFile = null;
	private String[] serviceNames;
	private int port = TestConstants.PORT_TCP;
	private int listenerPort = TestConstants.PORT_LISTENER;
	private int maxCons = 10;
	private static boolean killPublishServer = false;

	/** start server process (wrapper for the case this will be started directly from CLI)
	 * @param args see runSessionServer
	 */
	public static void main(String[] args) throws Exception {
		TestPublishServer publishServer = new TestPublishServer();
		publishServer.runPublishServer(args);
	}

	/** start server process
	 * @param args
	 *  [0] listenerPort<br>	
	 *  [1] SC port<br>			
	 *  [2] maxSessions<br>			
	 *  [3] maxConnections<br>
	 *  [4...] serviceNames<br>		
	 */
	public void runPublishServer(String[] args) {
		
		logger.log(Level.OFF, "TestPublishServer is running ...");
		
		this.listenerPort = Integer.parseInt(args[0]);
		this.port = Integer.parseInt(args[1]);
		this.maxCons = Integer.parseInt(args[2]);
		this.startFile = args[3];
		this.serviceNames = new String[args.length - 4];

			
		try {
			this.publishSrv = new SCPublishServer();

			try {
				this.listenerPort = Integer.parseInt(args[0]);
				this.port = Integer.parseInt(args[1]);
				this.maxCons = Integer.parseInt(args[2]);
				this.startFile = args[3];
				this.serviceNames = new String[args.length - 4];
				System.arraycopy(args, 4, serviceNames, 0, args.length - 4);
			} catch (Exception e) {
				logger.error("incorrect parameters", e);
				shutdown();
			}

			// connect to SC as server
			this.publishSrv.setImmediateConnect(true);
			this.publishSrv.startListener(TestConstants.HOST, listenerPort, 0);

			SrvCallback srvCallback = new SrvCallback(new PublishServerContext());

			for (int i = 0; i < serviceNames.length; i++) {
				this.publishSrv.registerServer(TestConstants.HOST, port, serviceNames[i], 1000, maxCons, srvCallback);
			}

			// for testing whether the server already started
			new ProcessesController().createFile(startFile);

			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			long pid = Long.parseLong(processName.split("@")[0]);
			FileWriter fw = null;
			try {
				File pidFile = new File(startFile);
				fw = new FileWriter(pidFile);
				fw.write("pid: " + pid);
				fw.flush();
				fw.close();
			} finally {
				if (fw != null) {
					fw.close();
				}
			}

			// start publishing
			for (int i = 0; i < serviceNames.length; i++) {
				Runnable run = new PublishRun(publishSrv, serviceNames[i]);
				Thread thread = new Thread(run);
				thread.start();
			}
		} catch (Exception ex) {
			logger.error("runPublishServer", ex);
			this.shutdown();
		}
	}

	private static class PublishRun implements Runnable {
		SCPublishServer server;
		String serviceName;

		public PublishRun(SCPublishServer server, String serviceName) {
			this.server = server;
			this.serviceName = serviceName;
		}

		@Override
		public void run() {
			int index = 0;
			while (!TestPublishServer.killPublishServer) {
				try {
					if (index % 3 == 0) {
						Thread.sleep(1000);
					} else {
						Thread.sleep(2000);
					}
					Object data = "publish message nr " + ++index;
					SCPublishMessage publishMessage = new SCPublishMessage();
					publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					publishMessage.setData(data);
					server.publish(serviceName, publishMessage);
					logger.info("message nr " + index + " sent.");
				} catch (Exception ex) {
					logger.error("run", ex);
					return;
				}
			}
		}
	}

	private void shutdown() {
		TestPublishServer.killPublishServer = true;
		try {
			for (int i = 0; i < serviceNames.length; i++) {
				this.publishSrv.deregisterServer(serviceNames[i]);
			}
		} catch (Exception ex) {
			logger.error("shutdown", ex);
			this.publishSrv = null;
		}
	}

	private class SrvCallback extends SCPublishServerCallback {

		private PublishServerContext outerContext;

		public SrvCallback(PublishServerContext context) {
			this.outerContext = context;
		}

		@Override
		public SCMessage changeSubscription(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public SCMessage subscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("PublishServer.SrvCallback.subscribe()");
			return message;
		}

		@Override
		public void unsubscribe(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("PublishServer.SrvCallback.unsubscribe()");
			Object data = message.getData();
			// watch out for kill server message
			if (data != null && data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						for (int i = 0; i < serviceNames.length; i++) {
							this.outerContext.getServer().deregisterServer(serviceNames[i]);
						}
					} catch (Exception ex) {
						logger.error("unsubscribe", ex);
					}
				}
			}
		}
	}

	private class PublishServerContext {
		public SCPublishServer getServer() {
			return publishSrv;
		}
	}
}