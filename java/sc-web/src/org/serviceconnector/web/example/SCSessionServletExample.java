package org.serviceconnector.web.example;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.web.SCServlet;

public class SCSessionServletExample extends SCServlet {

	public SCSessionServletExample() {
		super("/sc-web/SCSessionServletExample");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		return message;
	}

	@Override
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
	}

	@Override
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
	}

	@Override
	public SCMessage execute(SCMessage message, int operationTimeoutMillis) {
		return message;
	}
}
