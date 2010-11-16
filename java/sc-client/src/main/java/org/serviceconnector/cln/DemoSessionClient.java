package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.net.ConnectionType;

public class DemoSessionClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoSessionClient.class);

	private static boolean pendingRequest = false;

	public static void main(String[] args) {
		DemoSessionClient demoSessionClient = new DemoSessionClient();
		demoSessionClient.start();
	}

	@Override
	public void run() {

		SCClient sc = new SCClient("localhost", 7000); // regular, defaults documented in javadoc
		sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP); // alternative with connection type
		SCSessionService service = null;
		try {
			sc.setMaxConnections(20); // can be set before attach
			sc.setKeepAliveIntervalInSeconds(0); // can be set before attach
			sc.attach(); // regular
//			sc.attach(10); // alternative with operation timeout

			String serviceName = "simulation";
			service = sc.newSessionService(serviceName); // regular, no other params possible
			service.setEchoIntervalInSeconds(10); // can be set before create session
			service.setEchoTimeoutInSeconds(2); // can be set before create session

			service.createSession(); // regular
//			service.createSession(10); // alternative with operation timeout
//			SCMessage msg = new SCMessage();
//			msg.setSessionInfo("session-info"); // optional
//			msg.setData("certificate or what so ever"); // optional
//			service.createSession(10, msg); // alternative with operation timeout and message
//			service.createSession(msg); // alternative with message

			String sid = service.getSessionId();

			SCMessage requestMsg = new SCMessage();
			SCMessage responseMsg = new SCMessage();
			SCMessageCallback cbk = new DemoSessionClientCallback(service); // callback on service!!
			for (int i = 0; i < 10; i++) {
				requestMsg.setData("body nr : " + i);
				logger.info("Message sent: " + requestMsg.getData());

				// service.send(cbk, requestMsg); // regular asynchronous call
				// service.send(cbk, requestMsg, 10); // alternative with operation timeout
				// service.receive(cbk); // wait for response
				// cbk.receive(); // wait for response ?
				// responseMsg = cbk.getMessage(); // response message

				// synchronous call encapsulates asynchronous call!
				responseMsg = service.execute(requestMsg); // regular synchronous call
//				responseMsg = service.execute(requestMsg, 10); // alternative with operation timeout

				logger.info("Message received: " + responseMsg.getData());
				Thread.sleep(1000);
			}
			requestMsg.setData("kill server");
			// service.send(cbk, requestMsg);

		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				service.deleteSession(); // regular
//				service.deleteSession(10); // alternative with operation timeout
//				SCMessage msg = new SCMessage();
//				msg.setSessionInfo("session-info"); // optional
//				service.deleteSession(10, msg); // alternative with operation timeout and message
//				service.deleteSession(msg); // alternative with message
				sc.detach();
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}

	private class DemoSessionClientCallback extends SCMessageCallback {
		private SCMessage replyMessage;

		public DemoSessionClientCallback(SCSessionService service) {
			super(service);
		}

		@Override
		public void receive(SCMessage reply) {
			this.replyMessage = reply;
		}

		public SCMessage getMessage() {
			return this.replyMessage;
		}

		@Override
		public void receive(Exception e) {
		}
	}

	// @Override
	// public void run() {
	// SCClient sc = new SCClient("localhost", 7000);
	// SCSessionService sessionService = null;
	//
	// try {
	// sc.attach();
	// sessionService = sc.newSessionService("simulation");
	// SCMessage scMessage = new SCMessage();
	// scMessage.setSessionInfo("sessionInfo");
	// sessionService.createSession(300, 60, scMessage);
	//
	// int index = 0;
	// while (true) {
	// SCMessage requestMsg = new SCMessage();
	// requestMsg.setData("body nr : " + index++);
	// logger.info("Message sent: " + requestMsg.getData());
	// SCMessageCallback callback = new DemoSessionClientCallback(sessionService);
	// DemoSessionClient.pendingRequest = true;
	// sessionService.execute(requestMsg, callback);
	// while (DemoSessionClient.pendingRequest) {
	// Thread.sleep(500);
	// }
	// }
	// } catch (Exception e) {
	// logger.error("run", e);
	// } finally {
	// try {
	// sessionService.deleteSession();
	// sc.detach();
	// } catch (Exception e) {
	// logger.error("cleanup", e);
	// }
	// }
	// }

	// private class DemoSessionClientCallback extends SCMessageCallback {
	// public DemoSessionClientCallback(SCSessionService service) {
	// super(service);
	// }
	//
	// @Override
	// public void receive(SCMessage reply) {
	// logger.info("Message received: " + reply.getData());
	// DemoSessionClient.pendingRequest = false;
	// }
	//
	// @Override
	// public void receive(Exception e) {
	// }
	// }
}