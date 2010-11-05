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
	
		SCClient sc = new SCClient("localhost", 7000);				// regular, defaults documented in javadoc
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY-HTTP);	// alternative with connection type
		
		try {
			sc.setConnectionType(ConnectionType.NETTY-HTTP);		// can be set before attach
			sc.setHost("localhost");								// can be set before attach
			sc.setPort(7000);										// can be set before attach
			sc.setMaxConnections(20);								// can be set before attach
			sc.setKeepaliveIntervalInSeconds(10);					// can be set before attach
			sc.attach();											// regular
			sc.attach(10);											// alternative with operation timeout
		
			String serviceName = "simulation";
			SCSessionService service = sc.newSessionService(serviceName);	// regular, no other params possible
			service.setEchoIntervalInSeconds(10);					// can be set before create session
			service.setEchoTimeoutInSeconds(2);						// can be set before create session
			
			service.createSession();								// regular
			service.createSession(10);								// alternative with operation timeout 
			SCMessage msg = new SCMessage();
			msg.setSessionInfo("sessionInfo");						// optional
			msg.setData("certificate or what so ever");				// optional
			service.createSession(10, msg);							// alternative with operation timeout and message 

			String sid = service.getSessionID();
			
			SCMessage requestMsg = new SCMessage();
			SCMessage responseMsg = new SCMessage();		
			SCMessageCallback cbk = new DemoSessionClientCallback(service);	// callback on service!!
			for (int i = 0; i < 100; i++) {
				requestMsg.setData("body nr : " + i);
				logger.info("Message sent: " + requestMsg.getData());

				service.send(cbk, requestMsg);						// regular asynchronous call
				service.send(cbk, requestMsg, 10);					// alternative with operation timeout
				service.receive();									// wait for response message synchronous
				responseMsg = cbk.getMessage();						// response message
				
				responseMsg = service.execute(requestMsg);			// regular synchronous call
				responseMsg = service.execute(requestMsg, 10);		// alternative with operation timeout
				
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				service.deleteSession();							// regular
				service.deleteSession(10);							// alternative with operation timeout
				sc.detach();
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {
		public DemoSessionClientCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			logger.info("Message received: " + reply.getData());
			DemoSessionClient.pendingRequest = false;
		}

		@Override
		public void receive(Exception e) {
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
		public DemoSessionClientCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			logger.info("Message received: " + reply.getData());
			DemoSessionClient.pendingRequest = false;
		}

		@Override
		public void receive(Exception e) {
		}
	}
}