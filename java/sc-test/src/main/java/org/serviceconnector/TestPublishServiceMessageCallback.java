package org.serviceconnector;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCPublishService;

public class TestPublishServiceMessageCallback extends SCMessageCallback {

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
