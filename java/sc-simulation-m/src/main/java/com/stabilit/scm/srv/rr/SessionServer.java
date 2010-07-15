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
package com.stabilit.scm.srv.rr;

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
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class SessionServer {
	private ISCServer scSrv = null;
	private String serviceName = "simulation";

	public static void main(String[] args) throws Exception {
		SessionServer sessionServer = new SessionServer();
		sessionServer.initLogStuff("log4j");
		sessionServer.runExample();
	}

	public void runExample() {
		try {
			scSrv = new SCServer("localhost", 9000);

			// connect to SC as server
			scSrv.setMaxSessions(10);
			scSrv.setKeepAliveInterval(0);
			scSrv.setRunningPortNr(7000);
			scSrv.setImmediateConnect(true);
			scSrv.startServer("localhost");
			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());
			scSrv.registerService(serviceName, srvCallback);
		} catch (Exception e) {
			e.printStackTrace();
			this.shutdown();
		}
	}
	
	private void shutdown() {
		try {
			scSrv.deregisterService(serviceName);
		} catch (Exception e) {
			scSrv = null;
		}
	}

	class SrvCallback implements ISCServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public ISCMessage deleteSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.deleteSession()");
			return message;

		}

		@Override
		public ISCMessage abortSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.abortSession()");
			return message;
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
			Object data = request.getData();
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
			return request;
		}
	}

	private class SessionServerContext {
		public ISCServer getServer() {
			return scSrv;
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