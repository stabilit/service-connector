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
import org.serviceconnector.common.service.ISCMessage;
import org.serviceconnector.srv.ISCPublishServer;
import org.serviceconnector.srv.ISCPublishServerCallback;
import org.serviceconnector.srv.ps.SCPublishServer;


public class DemoPublishServer {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishServer.class);
	
	private ISCPublishServer publishSrv = null;
	private String serviceName = "publish-simulation";
	private static boolean killPublishServer = false;

	public static void main(String[] args) throws Exception {
		DemoPublishServer publishServer = new DemoPublishServer();
		publishServer.runPublishServer();
	}

	public void runPublishServer() {
		try {
			this.publishSrv = new SCPublishServer();
			// connect to SC as server
			this.publishSrv.setImmediateConnect(true);
			this.publishSrv.startListener("localhost", 7200, 0);
			SrvCallback srvCallback = new SrvCallback(new PublishServerContext());
			this.publishSrv.registerService("localhost", 9000, serviceName, 10, 10, srvCallback);
			Runnable run = new PublishRun(publishSrv, serviceName);
			Thread thread = new Thread(run);
			thread.start();
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
			while (!DemoPublishServer.killPublishServer) {
				try {
					if (index % 3 == 0) {
						Thread.sleep(1000);
					} else {
						Thread.sleep(5000);
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
		DemoPublishServer.killPublishServer = true;
		try {
			this.publishSrv.deregisterService(serviceName);
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
			logger.info("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public ISCMessage subscribe(ISCMessage message) {
			logger.info("PublishServer.SrvCallback.subscribe()");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				logger.error("subscribe", ex);
			}
			return message;
		}

		@Override
		public void unsubscribe(ISCMessage message) {
			logger.info("PublishServer.SrvCallback.unsubscribe()");
			Object data = message.getData();
			// watch out for kill server message
			if (data != null && data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						this.outerContext.getServer().deregisterService(serviceName);
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