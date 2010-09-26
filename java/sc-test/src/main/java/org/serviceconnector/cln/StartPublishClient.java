package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.cln.IPublishService;
import org.serviceconnector.api.cln.ISCClient;
import org.serviceconnector.api.cln.IService;
import org.serviceconnector.api.cln.ISessionService;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.ctrl.util.TestConstants;

public class StartPublishClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StartPublishClient.class);

	private String methodName;

	public static void main(String[] args) {
		try {
			StartPublishClient client = new StartPublishClient(args[0]);
			client.start();
		} catch (Exception e) {
			logger.error("incorrect parameters", e);
		}
	}

	public StartPublishClient(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public void run() {
		ISCClient client = new SCClient();

		try {
			client.attach(TestConstants.HOST, TestConstants.PORT_HTTP);

			if (getMethodName() == "subscribe_serviceNameValidMaskSameAsInServer_isSubscribedSessionIdExists") {
				IPublishService service = client
						.newPublishService(TestConstants.serviceNamePublish);
				service.subscribe(TestConstants.mask, "sessionInfo", 300,
						new DemoPublishClientCallback(service));
				service.unsubscribe();

			} else if (getMethodName() == "subscribe_timeoutMaxAllowed_isSubscribedSessionIdExists") {
				IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
				service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
						service), 3600);
				service.unsubscribe();

			} else if (getMethodName() == "changeSubscription_toMaskWhiteSpace_passes") {
				IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
				service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
						service));
				service.changeSubscription(" ");
				service.unsubscribe();

			} else if (getMethodName() == "subscribeUnsubscribe_twice_isSubscribedThenNot") {
				IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
				service.subscribe(TestConstants.mask, "sessionInfo", 300,
						new DemoPublishClientCallback(service));
				service.unsubscribe();
				service.subscribe(TestConstants.mask, "sessionInfo", 300,
						new DemoPublishClientCallback(service));
				service.unsubscribe();

			} else if (getMethodName() == "changeSubscription_twice_passes") {
				IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
				service.subscribe(TestConstants.mask, "sessionInfo", 300, new DemoPublishClientCallback(
						service));
				
				service.changeSubscription(TestConstants.mask);
				service.changeSubscription(TestConstants.mask);
				
				service.unsubscribe();

			} else if (getMethodName() == "unsubscribe_serviceNameValid_notSubscribedEmptySessionId") {
				IPublishService service = client.newPublishService(TestConstants.serviceNamePublish);
				service.unsubscribe();

			} else if (getMethodName() == "createSession_rejectTheSessionThenCreateValidSessionThenExecuteAMessage_passes") {
				ISessionService sessionService = client
						.newSessionService(TestConstants.serviceName);

				try {
					sessionService.createSession("sessionInfo", 300, 10, "reject");
				} catch (Exception e) {
				}
				sessionService.createSession("sessionInfo", 300, 10);

				sessionService.execute(new SCMessage());
				sessionService.deleteSession();

			} else if (getMethodName() == "execute_messageData1MBArray_returnsTheSameMessageData") {
				ISessionService sessionService = client
						.newSessionService(TestConstants.serviceName);
				sessionService.createSession("sessionInfo", 300, 60);

				SCMessage message = new SCMessage(new byte[TestConstants.dataLength1MB]);
				message.setCompressed(false);

				sessionService.execute(message);
				sessionService.deleteSession();

			}

		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				client.detach();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}
	
	private class DemoPublishClientCallback extends SCMessageCallback {

		public DemoPublishClientCallback(IService service) {
			super(service);
		}

		@Override
		public void callback(SCMessage reply) {
			logger.info("Publish client received: " + reply.getData());
		}

		@Override
		public void callback(Exception e) {
		}
	}
}
