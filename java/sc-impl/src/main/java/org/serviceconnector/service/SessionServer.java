package org.serviceconnector.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionServer extends Server {

	/** The sessions, list of sessions allocated to the server. */
	private List<AbstractSession> sessions;
	/** The max sessions. */
	private int maxSessions;
	/** The service. */
	private StatefulService service;

	public SessionServer(InetSocketAddress socketAddress, String serviceName, int portNr, int maxSessions,
			int maxConnections, int keepAliveInterval) {
		super(socketAddress, serviceName, portNr, maxConnections, keepAliveInterval);
		this.sessions = Collections.synchronizedList(new ArrayList<AbstractSession>());
		this.maxSessions = maxSessions;
		this.service = null;
	}

	/**
	 * Adds an allocated session to the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void addSession(AbstractSession session) {
		this.sessions.add(session);
	}

	/**
	 * Removes an allocated session from the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(AbstractSession session) {
		this.sessions.remove(session);
	}

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions
	 */
	public List<AbstractSession> getSessions() {
		return this.sessions;
	}

	/**
	 * Checks for free session.
	 * 
	 * @return true, if successful
	 */
	public boolean hasFreeSession() {
		return this.sessions.size() < this.maxSessions;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return this.maxSessions;
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	public StatefulService getService() {
		return this.service;
	}

	/**
	 * Sets the service.
	 * 
	 * @param service
	 *            the new service
	 */
	public void setService(StatefulService service) {
		this.service = service;
	}

	/** @{inheritDoc */
	@Override
	public void destroy() {
		super.destroy();
		this.sessions = null;
		this.service = null;
	}

	/** @{inheritDoc */
	@Override
	public String toString() {
		return serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort() + ":" + portNr + " : "
				+ maxSessions;
	}
}
