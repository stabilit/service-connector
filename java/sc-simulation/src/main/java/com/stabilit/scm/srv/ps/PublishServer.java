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
package com.stabilit.scm.srv.ps;

import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IConnectionListener;
import com.stabilit.scm.common.listener.IExceptionListener;
import com.stabilit.scm.common.listener.ILoggerListener;
import com.stabilit.scm.common.listener.IPerformanceListener;
import com.stabilit.scm.common.listener.ISessionListener;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.log.Level;
import com.stabilit.scm.common.log.impl.ConnectionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.log.impl.LoggerFactory;
import com.stabilit.scm.common.log.impl.PerformanceLogger;
import com.stabilit.scm.common.log.impl.SessionLogger;
import com.stabilit.scm.common.log.impl.TopLogger;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.srv.ISCPublishServer;
import com.stabilit.scm.srv.ISCPublishServerCallback;

public class PublishServer {
	private ISCPublishServer publishSrv = null;
	private String serviceName = "publish-simulation";

	public static void main(String[] args) throws Exception {
		PublishServer publishServer = new PublishServer();
		publishServer.initLogStuff("log4j");
		publishServer.runExample();
	}

	public void runExample() {
		try {
			this.publishSrv = new SCPublishServer("localhost", 9000);

			// connect to SC as server
			this.publishSrv.setMaxSessions(10);
			this.publishSrv.setKeepAliveInterval(0);
			this.publishSrv.setRunningPortNr(7000);
			this.publishSrv.setImmediateConnect(true);
			this.publishSrv.startServer("localhost");
			SrvCallback srvCallback = new SrvCallback(new PublishServerContext());
			this.publishSrv.registerService(serviceName, srvCallback);
		} catch (Exception e) {
			e.printStackTrace();
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			this.publishSrv.deregisterService(serviceName);
		} catch (Exception e) {
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
			System.out.println("PublishServer.SrvCallback.changeSubscription()");
			return message;
		}

		@Override
		public ISCMessage subscribe(ISCMessage message) {
			System.out.println("PublishServer.SrvCallback.subscribe()");
			return message;
		}

		@Override
		public void unsubscribe(ISCMessage message) {
			System.out.println("PublishServer.SrvCallback.unsubscribe()");
			Object data = message.getData();
			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						this.outerContext.getServer().deregisterService(serviceName);
					} catch (Exception e) {
						e.printStackTrace();
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

	private void initLogStuff(String loggerKey) {
		LoggerFactory loggerFactory = LoggerFactory.getCurrentLoggerFactory(loggerKey);
		ConnectionPoint.getInstance().addListener(
				(IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class));
		ExceptionPoint.getInstance().addListener((IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class));
		LoggerPoint.getInstance().addListener((ILoggerListener) loggerFactory.newInstance(TopLogger.class));
		LoggerPoint.getInstance().setLevel(Level.DEBUG);
		PerformancePoint.getInstance().addListener(
				(IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class));
		PerformancePoint.getInstance().setOn(true);
		SessionPoint.getInstance().addListener((ISessionListener) loggerFactory.newInstance(SessionLogger.class));
	}
}