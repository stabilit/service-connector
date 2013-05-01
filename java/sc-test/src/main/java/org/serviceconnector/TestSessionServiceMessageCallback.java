package org.serviceconnector;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCSessionService;

public class TestSessionServiceMessageCallback extends SCMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TestSessionServiceMessageCallback.class);

	public TestSessionServiceMessageCallback(SCSessionService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
		LOGGER.info("Session client received=" + reply.getData());
	}

	@Override
	public void receive(Exception ex) {
		LOGGER.info("Session client received=" + ex);
	}
}
