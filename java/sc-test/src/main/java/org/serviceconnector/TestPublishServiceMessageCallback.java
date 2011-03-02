package org.serviceconnector;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;

public class TestPublishServiceMessageCallback extends SCMessageCallback {

	public static int receivedMsg;
	public static int lastNumber = -1;

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(TestPublishServiceMessageCallback.class);

	public TestPublishServiceMessageCallback(SCPublishService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
		synchronized (this) {
			receivedMsg++;
			String responseString = ((String) reply.getData());
			LOGGER.info("Publish client sid=" + this.service.getSessionId() + " received: " + reply + " body=" + reply.getData());
			String number = responseString.substring(responseString.indexOf(":") + 1);
			int currentNumber = Integer.valueOf(number);
			if (currentNumber != lastNumber + 1) {
				LOGGER.info("Publish client sid=" + this.service.getSessionId() + " received messages not in sequence wrong NR: "
						+ currentNumber);
			}
			lastNumber = currentNumber;
		}
	}

	@Override
	public void receive(Exception ex) {
		synchronized (this) {
			receivedMsg++;
			LOGGER.info("Publish client sid=" + this.service.getSessionId() + " received: " + ex);
		}
	}
}
