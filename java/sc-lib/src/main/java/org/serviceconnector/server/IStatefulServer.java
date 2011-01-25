package org.serviceconnector.server;

import java.util.List;

import org.serviceconnector.service.AbstractSession;

public interface IStatefulServer extends IServer {

	/** {@inheritDoc} */
	@Override
	public abstract ServerType getType();

	/** {@inheritDoc} */
	@Override
	public abstract void abortSession(AbstractSession session, String string);

	/**
	 * Removes an allocated session from the server.
	 * 
	 * @param session
	 *            the session
	 */
	public abstract void removeSession(AbstractSession abstractSession);

	/**
	 * Adds an allocated session to the server.
	 * 
	 * @param session
	 *            the session
	 */
	public abstract void addSession(AbstractSession session);

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions
	 */
	public abstract List<AbstractSession> getSessions();

	/**
	 * Checks for free session.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean hasFreeSession();

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public abstract int getMaxSessions();
}
