package org.serviceconnector.web.example;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.web.SCBasePublishServlet;

public class SCPublishServletExample extends SCBasePublishServlet {

	private static final long serialVersionUID = 1L;

	public SCPublishServletExample() {
		super("/sc-web/SCPublishServletExample");
	}

	@Override
	public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	@Override
	public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	@Override
	public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
	}

	@Override
	public void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis) {
	}
}
