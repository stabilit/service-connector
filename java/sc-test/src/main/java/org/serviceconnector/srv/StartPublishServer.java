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

import org.apache.log4j.Logger;
import org.serviceconnector.ctrl.util.TestEnvironmentController;
import org.serviceconnector.service.ISCMessage;
import org.serviceconnector.srv.ISCPublishServer;
import org.serviceconnector.srv.ISCPublishServerCallback;

public class StartPublishServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StartPublishServer.class);

	private ISCPublishServer publishSrv = null;
	private String startFile = null;
	private String[] serviceNames;
	private int port = 9000;
	private int listenerPort = 30000;
	private int maxCons = 10;
	private static boolean killPublishServer = false;

	public static void main(String[] args) throws Exception {
		StartPublishServer publishServer = new StartPublishServer();
		publishServer.runPublishServer(args);
	}

	public void runPublishServer(String[] args) {
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
			this.publishSrv.startListener("localhost", listenerPort, 0);

			SrvCallback srvCallback = new SrvCallback(new PublishServerContext());

			for (int i = 0; i < serviceNames.length; i++) {
				this.publishSrv.registerServer("localhost", port, serviceNames[i], 1000, maxCons,
						srvCallback);
			}

			// for testing whether the server already started
			new TestEnvironmentController().createFile(startFile);

			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean()
					.getName();
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
		ISCPublishServer server;
		String serviceName;

		public PublishRun(ISCPublishServer server, String serviceName) {
			this.server = server;
			this.serviceName = serviceName;
		}

		@Override
		public void run() {
			int index = 0;
			while (!StartPublishServer.killPublishServer) {
				try {
					if (index % 3 == 0) {
						Thread.sleep(1000);
					} else {
						Thread.sleep(2000);
					}
					Object data = "publish message nr " + ++index;
					String mask = "0000121%%%%%%%%%%%%%%%-----------X-----------";
					server.publish(serviceName, mask, data);
				} catch (Exception ex) {
					logger.error("run", ex);
					return;
				}
			}
		}
	}

	private void shutdown() {
		StartPublishServer.killPublishServer = true;
		try {
			for (int i = 0; i < serviceNames.length; i++) {
				this.publishSrv.deregisterServer(serviceNames[i]);
			}
		} catch (Exception ex) {
			logger.error("shutdown", ex);
			this.publishSrv = null;
		}
	}

	private class SrvCallback implements ISCPublishServerCallback {

		private PublishServerContext outerContext;

		public SrvCallback(PublishServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage changeSubscription(ISCMessage message) {
			logger.debug("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public ISCMessage subscribe(ISCMessage message) {
			logger.debug("PublishServer.SrvCallback.subscribe()");
			return message;
		}

		@Override
		public void unsubscribe(ISCMessage message) {
			logger.debug("PublishServer.SrvCallback.unsubscribe()");
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
		public ISCPublishServer getServer() {
			return publishSrv;
		}
	}
}