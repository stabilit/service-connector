package org.serviceconnector.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StatefulService extends Service {

	/** The server index. */
	protected int serverIndex;
	/** The list of servers. */
	protected List<SessionServer> listOfServers;

	public StatefulService(String name, ServiceType type) {
		super(name, type);
		this.serverIndex = 0;
		// synchronize the sever list
		this.listOfServers = Collections.synchronizedList(new ArrayList<SessionServer>());
	}

	/**
	 * Adds the server.
	 * 
	 * @param server
	 *            the server
	 */
	public void addServer(SessionServer server) {
		this.listOfServers.add(server);
	}

	/**
	 * Removes the server.
	 * 
	 * @param server
	 *            the server
	 */
	public void removeServer(SessionServer server) {
		this.listOfServers.remove(server);
	}

	/**
	 * Gets the server list.
	 * 
	 * @return the server list
	 */
	public List<SessionServer> getServerList() {
		return Collections.unmodifiableList(this.listOfServers);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.serverIndex);
		for (Server server : this.listOfServers) {
			sb.append(" - ");
			sb.append(server);
		}
		return sb.toString();
	}

	public int getCountServers() {
		return listOfServers.size();
	}

	/**
	 * Gets the count allocated sessions.
	 * 
	 * @return the count allocated sessions
	 */
	public int getCountAllocatedSessions() {
		int allocatedSessions = 0;

		for (SessionServer server : listOfServers) {
			allocatedSessions += server.getSessions().size();
		}
		return allocatedSessions;
	}

	/**
	 * Gets the count available sessions.
	 * 
	 * @return the count available sessions
	 */
	public int getCountAvailableSessions() {
		int availableSessions = 0;

		for (SessionServer server : listOfServers) {
			availableSessions += server.getMaxSessions();
		}
		return availableSessions;
	}
}
