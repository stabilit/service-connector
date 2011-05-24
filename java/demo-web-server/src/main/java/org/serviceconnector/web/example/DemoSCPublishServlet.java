package org.serviceconnector.web.example;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.web.SCBasePublishServlet;

public class DemoSCPublishServlet extends SCBasePublishServlet {

	private static final long serialVersionUID = 1L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DemoSCPublishServlet.class);

	public DemoSCPublishServlet() {
		super("/demo-web/DemoSCPublishServlet");
	}

	@Override
	public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription created");
		PublishThread publish = new PublishThread();
		publish.start();
		return message;
	}

	@Override
	public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription changed");
		return message;
	}

	@Override
	public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		LOGGER.info("Subscription deleted");
	}

	@Override
	public void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis) {
		LOGGER.info("Subscription aborted");
	}

	private class PublishThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				SCPublishMessage pubMessage = new SCPublishMessage();
				for (int i = 0; i < 5; i++) {
					pubMessage.setData("publish message nr : " + i);
					pubMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
					DemoSCPublishServlet.this.publish(pubMessage);
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				LOGGER.warn("publish failed");
			}
		}
	}
}
