package org.serviceconnector.server;

import org.serviceconnector.service.AbstractSession;

public interface IServer {

	/**
	 * Gets the server type.
	 * 
	 * @return the type
	 */
	public abstract ServerType getType();

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public abstract String getServiceName();

	/**
	 * Abort session on server.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason
	 */
	public abstract void abortSession(AbstractSession session, String reason);
}
