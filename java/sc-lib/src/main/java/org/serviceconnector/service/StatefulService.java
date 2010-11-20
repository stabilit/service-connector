/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;

public abstract class StatefulService extends Service {

	/** The server index. */
	protected int serverIndex;
	/** The list of servers. */
	protected List<StatefulServer> listOfServers;

	public StatefulService(String name, ServiceType type) {
		super(name, type);
		this.serverIndex = 0;
		// synchronize the sever list
		this.listOfServers = Collections.synchronizedList(new ArrayList<StatefulServer>());
	}

	/**
	 * Adds the server.
	 * 
	 * @param server
	 *            the server
	 */
	public void addServer(StatefulServer server) {
		this.listOfServers.add(server);
	}

	/**
	 * Removes the server.
	 * 
	 * @param server
	 *            the server
	 */
	public void removeServer(StatefulServer server) {
		this.listOfServers.remove(server);
	}

	/**
	 * Gets the server list.
	 * 
	 * @return the server list
	 */
	public List<StatefulServer> getServerList() {
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

		for (StatefulServer server : listOfServers) {
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

		for (StatefulServer server : listOfServers) {
			availableSessions += server.getMaxSessions();
		}
		return availableSessions;
	}
}
