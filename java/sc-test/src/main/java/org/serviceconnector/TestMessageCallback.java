package org.serviceconnector;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;

public class TestMessageCallback extends SCMessageCallback {

	public TestMessageCallback(SCService service) {
		super(service);
	}

	@Override
	public void receive(SCMessage reply) {
	}

	@Override
	public void receive(Exception ex) {
	}
}
