package org.serviceconnector;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;

public class TestPublishServiceMessageCallback extends SCMessageCallback {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(TestPublishServiceMessageCallback.class);
	
	public TestPublishServiceMessageCallback(SCPublishService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
		logger.info("Publish client received: " + reply.getData());
	}

	@Override
	public void receive(Exception ex) {
		logger.info("Publish client received: " + ex);
	}
}
