package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCPublishService;

public class DemoPublishClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoPublishClient.class);

	public static void main(String[] args) {
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		demoPublishClient.start();
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
		
			String serviceName = "publish-simulation";
			SCPublishService service = sc.newPublishService(serviceName);	// no other params possible
			service.setNoDataIntervalInSeconds(100);				// can be set before subscribe
			
			SCMessageCallback cbk = new DemoSessionClientCallback(service);	// callback on service!!
			String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";
			service.subscribe(cbk, mask);							//regular
			service.subscribe(cbk, mask, 10);						//alternative with operation timeout 
			SCMessage msg = new SCMessage();
			msg.setSessionInfo("subsriptionInfo");					// optional
			msg.setData("certificate or what so ever");				// optional
			service.subscribe(cbk, mask, 10, msg);					//alternative with operation timeout and message 

			String sid = service.getSessionID();
		
			while (true) {
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				service.unsubscribe();					// regular
				service.unsubscribe(10);				// alternative with operation timeout
				sc.detach();
			} catch (Exception e) {
				logger.info("cleanup " + e.toString());
			}
		}	
	}
	*/
	
	@Override
	public void run() {
		SCClient sc = new SCClient();
		SCPublishService publishService = null;
		try {
			((SCClient) sc).setConnectionType("netty.http");
			sc.attach("localhost", 7000);
			publishService = sc.newPublishService("publish-simulation");
			SCSubscribeMessage subscibeMessage = new SCSubscribeMessage();
			subscibeMessage.setMask("0000121ABCDEFGHIJKLMNO-----------X-----------");
			subscibeMessage.setSessionInfo("sessionInfo");
			publishService.subscribe(subscibeMessage, new DemoSessionClientCallback(publishService));

			while (true) {
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				publishService.unsubscribe();
				sc.detach();
			} catch (Exception e) {
				logger.info("cleanup " + e.toString());
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {

		public DemoSessionClientCallback(SCService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			System.out.println("Publish client received: " + reply.getData());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void receive(Exception e) {
		}
	}
}