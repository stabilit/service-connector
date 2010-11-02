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
	@Override
	public void run() {
	
		SCClient sc = new SCClient("localhost", 7000);				// regular defaults must be documented in javadoc
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY-HTTP);	// alternative with connection type
		
		try {
			sc.setConnectionType(ConnectionType.NETTY-HTTP);		// can be set before attach
			sc.setHost("localhost");								// can be set before attach
			sc.setPort(7000);										// can be set before attach
			sc.setMaxConnections(20);								// can be set before attach
			sc.setKeepaliveIntervalInSeconds(10);					// can be set before attach
			sc.attach();											// regular
			sc.attach(10);											// alternative with operation timeout
		
			SCSessionService service = sc.newSessionService("simulation");		// no other params possible
			service.setEchoIntervalInSeconds(10);					// can be set before create session
			service.setEchoTimeoutInSeconds(2);						// can be set before create session
			
			SCSession session = service.createSession();			//regular
			SCSession session = service.createSession(10);			//alternative with operation timeout 
			SCMessage msg = new SCMessage();
			msg.setSessionInfo("sessionInfo");						// optional
			msg.setData("certificate or what so ever");				// optional
			SCSession session = service.createSession(10, msg);		//alternative with operation timeout and message 
			
			String sid = session.getSessionID();
			
			SCMessage requestMsg = new SCMessage();
			SCMessage responseMsg = new SCMessage();
			SCMessageCallback cbk = new DemoSessionClientCallback(service);	// callback on service!!
			
			int index = 0;
			while (true) {
				requestMsg.setData("body nr : " + index++);
				logger.info("Message sent: " + requestMsg.getData());

				responseMsg = session.execute(requestMsg);			// regular synchronous
				responseMsg = session.execute(requestMsg, 10);		// alternative synchronous with operation timeout

				DemoSessionClient.pendingRequest = true;	
				session.execute(requestMsg, cbk);					// regular asynchronous
				session.execute(requestMsg, cbk, 10);				// alternative asynchronous with operation timeout
				while (DemoSessionClient.pendingRequest) {
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				session.deleteSession();							// regular
				session.deleteSession(10);							// alternative with operation timeout
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