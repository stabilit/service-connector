/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.registry;

import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;

/**
 * The Class ServerRegistry. Stores an entry for every registered server in system.
 * 
 * @author JTraber
 */
public class ServerRegistry extends Registry<String, Server> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServerRegistry.class);

	/**
	 * Adds an entry of a server.
	 * 
	 * @param key
	 *            the key
	 * @param server
	 *            the server
	 */
	public void addServer(String key, Server server) {
		this.put(key, server);
	}

	/**
	 * Gets the server.
	 * 
	 * @param key
	 *            the key
	 * @return the server
	 */
	public Server getServer(String key) {
		return super.get(key);
	}

	
	/**
	 * Gets all servers.
	 *
	 * @return the servers
	 */
	public Server[] getServers() {
		try {
			Set<Entry<String, Server>> entries = this.registryMap.entrySet();
			Server[] servers = new Server[entries.size()];
			int index = 0;
			for (Entry<String, Server> entry : entries) {
				String key = entry.getKey();
				Server server = entry.getValue();
				servers[index++] = server;
			}
			return servers;
		} catch (Exception e) {
			logger.error("getServers", e);
		}
		return null;
	}

	/**
	 * Removes the server.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeServer(String key) {
		super.remove(key);
	}
}
