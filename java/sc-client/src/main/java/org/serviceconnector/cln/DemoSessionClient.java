package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;


public class DemoSessionClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionClient.class);
	
	private static boolean pendingRequest = false;

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();
	}

	/*
	public void runOther() {
		try {
			SCClient sc = new SCClient();						// defaults must be documented in javadoc
			sc.setConnectionType(ConnectionType.NETTY-HTTP);	// must be set
			sc.setHost("localhost");							// must be set
			sc.setPort(7000);									// must be set
			sc.setMaxConnections(20);							// optional
			sc.setKeepaliveIntervalInSeconds(10);				// optional
			sc.attach();
		
			SCSessionService sessionService = sc.newSessionService("simulation");		
			SCSession session = sessionService.createSession();

			int index = 0;
			while (true) {
				SCMessage requestMsg = new SCMessage();
				requestMsg.setData("body nr : " + index++);
				logger.info("Message sent: " + requestMsg.getData());
				SCMessageCallback callback = new DemoSessionClientCallback(sessionService);
				DemoSessionClient.pendingRequest = true;
				session.execute(requestMsg, callback);
				while (DemoSessionClient.pendingRequest) {
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				session.deleteSession();
				sc.detach();
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}
	*/
	
	@Override
	public void run() {
		SCClient sc = new SCClient();
		SCSessionService sessionService = null;

		try {
			sc.attach("localhost", 7000);
			sessionService = sc.newSessionService("simulation");
			SCMessage scMessage = new SCMessage();
			scMessage.setSessionInfo("sessionInfo");
			sessionService.createSession(300, 60, scMessage);

			int index = 0;
			while (true) {
				SCMessage requestMsg = new SCMessage();
				requestMsg.setData("body nr : " + index++);
				logger.info("Message sent: " + requestMsg.getData());
				SCMessageCallback callback = new DemoSessionClientCallback(sessionService);
				DemoSessionClient.pendingRequest = true;
				sessionService.execute(requestMsg, callback);
				while (DemoSessionClient.pendingRequest) {
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				sessionService.deleteSession();
				sc.detach();
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {

		private SCService service;

		public DemoSessionClientCallback(SCService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			logger.info("Message received: " + reply.getData());
			DemoSessionClient.pendingRequest = false;
		}

		@Override
		public SCService getService() {
			return this.service;
		}

		@Override
		public void callback(Exception e) {
		}
	}
}