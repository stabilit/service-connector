package org.serviceconnector.service;

import java.util.UUID;

import org.serviceconnector.util.TimerTaskWrapper;

public abstract class AbstractSession {

	/** The id. */
	private String id;
	/** The server. */
	private StatefulServer server;
	/** The ip address list. */
	private String ipAddressList;
	/** The session info. */
	private String sessionInfo;

	/** The session timeouter - observes session timeout. */
	private TimerTaskWrapper sessionTimeouter;

	/**
	 * Instantiates a new session.
	 */
	public AbstractSession(String sessionInfo, String ipAddressList) {
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.server = null;
		this.sessionTimeouter = null;
		this.ipAddressList = ipAddressList;
		this.sessionInfo = sessionInfo;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the server.
	 * 
	 * @param server
	 *            the new server
	 */
	public void setServer(StatefulServer server) {
		this.server = server;
	}

	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public StatefulServer getServer() {
		return this.server;
	}

	/**
	 * Gets the session timeouter.
	 * 
	 * @return the session timeouter
	 */
	public TimerTaskWrapper getSessionTimeouter() {
		return sessionTimeouter;
	}

	/**
	 * Sets the session timeouter.
	 * 
	 * @param sessionTimeouter
	 *            the new session timeouter
	 */
	public void setSessionTimeouter(TimerTaskWrapper sessionTimeouter) {
		this.sessionTimeouter = sessionTimeouter;
	}

	public String getIpAddressList() {
		return ipAddressList;
	}

	public String getSessionInfo() {
		return sessionInfo;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return id + ":" + server;
	}
}
