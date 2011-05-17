package org.serviceconnector.web.example;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.web.SCBaseSessionServlet;

public class DemoSCSessionServlet extends SCBaseSessionServlet {

	private static final long serialVersionUID = 1L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DemoSCSessionServlet.class);

	public DemoSCSessionServlet() {
		super("/demo-web/DemoSCSessionServlet");
	}

	@Override
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session created");
		return message;
	}

	@Override
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session deleted");
	}

	@Override
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("Session aborted");
	}

	@Override
	public SCMessage execute(SCMessage message, int operationTimeoutMillis) {
		LOGGER.info("execute");
		if (message.getCacheId() != null) {
			Calendar time = Calendar.getInstance();
			time.add(Calendar.HOUR_OF_DAY, 1);
			message.setCacheExpirationDateTime(time.getTime());
		}
		return message;
	}
}
