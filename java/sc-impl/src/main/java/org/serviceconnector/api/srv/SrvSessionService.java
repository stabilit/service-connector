package org.serviceconnector.api.srv;

import org.serviceconnector.net.req.IRequester;

public class SrvSessionService extends SrvService {

	/** The callback. */
	private ISCSessionServerCallback callback;
	
	public SrvSessionService(String serviceName, int maxSessions, int maxConnections, IRequester requester,
			ISCSessionServerCallback callback) {
		super(serviceName, maxSessions, maxConnections, requester);
		this.callback = callback;
	}

	/**
	 * Gets the callback.
	 * 
	 * @return the callback
	 */
	public ISCSessionServerCallback getCallback() {
		return this.callback;
	}
}
