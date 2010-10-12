package org.serviceconnector.api.srv;

import org.serviceconnector.net.req.IRequester;

public class SrvPublishService extends SrvService {

	/** The callback. */
	private ISCPublishServerCallback callback;

	public SrvPublishService(String serviceName, int maxSessions, int maxConnections, IRequester requester,
			ISCPublishServerCallback callback) {
		super(serviceName, maxSessions, maxConnections, requester);
		this.callback = callback;
	}

	/**
	 * Gets the callback.
	 * 
	 * @return the callback
	 */
	public ISCPublishServerCallback getCallback() {
		return this.callback;
	}
}
