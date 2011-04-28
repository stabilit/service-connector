package org.serviceconnector.web.example;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.web.SCBaseSessionServlet;

public class SCSessionServletExample extends SCBaseSessionServlet {

	private static final long serialVersionUID = 1L;

	public SCSessionServletExample() {
		super("/sc-web/SCSessionServletExample");
	}

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
