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

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class ServerRegistry. Stores an entry for every registered server in system. Server registry is also responsible for
 * observation of server timeout and to clean up in case of broken servers. Server timeout gets initialized by adding server.
 * Resetting the timer needs to be done outside the registry by calling reset method.
 * 
 * @author JTraber
 */
public class ServerRegistry extends Registry<String, Server> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ServerRegistry.class);

	/** The timer. Timer instance is responsible to observe server timeouts. */
	private ScheduledThreadPoolExecutor serverScheduler;

	/**
	 * Instantiates a ServerRegistry.
	 */
	public ServerRegistry() {
		this.serverScheduler = new ScheduledThreadPoolExecutor(1);
	}

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
		LOGGER.debug("Adding Server to registry, server=" + server.toString());
		this.scheduleServerTimeout(server, server.getServerTimeoutMillis());
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
				// String key = entry.getKey();
				Server server = entry.getValue();
				servers[index++] = server;
			}
			return servers;
		} catch (Exception e) {
			LOGGER.error("getServers", e);
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
		Server server = super.remove(key);
		this.cancelServerTimeout(server);
		if (server != null) {
			LOGGER.debug("Removing Server from registry, server=" + key);
		}
	}

	/**
	 * Schedule server timeout.
	 * 
	 * @param server
	 *            the server
	 */
	@SuppressWarnings("unchecked")
	private void scheduleServerTimeout(Server server, double newTimeoutMillis) {
		if (server == null || newTimeoutMillis == 0) {
			// no scheduling of server timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelServerTimeout(server);
		// sets up server timeout
		TimeoutWrapper serverTimeouter = new TimeoutWrapper(new ServerTimeout(server, server.getServerTimeoutMillis()));
		// schedule serverTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.serverScheduler.schedule(serverTimeouter,
				(long) newTimeoutMillis, TimeUnit.MILLISECONDS);
		LOGGER.trace("schedule server timeout server=" + server.getServerKey() + " timeout=" + newTimeoutMillis);
		server.setTimeout(timeout);
		server.setTimeouterTask(serverTimeouter);
	}

	/**
	 * Cancel server timeout.
	 * 
	 * @param server
	 *            the server
	 */
	private void cancelServerTimeout(Server server) {
		if (server == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> serverTimeout = server.getTimeout();
		if (serverTimeout == null) {
			// no session timeout has been set up for this server
			return;
		}
		LOGGER.trace("cancel server timeout server=" + server.getServerKey());
		boolean cancelSuccess = serverTimeout.cancel(false);
		if (cancelSuccess == false) {
			LOGGER.error("cancel of server timeout failed server=" + server.getServerKey() + " delay="
					+ serverTimeout.getDelay(TimeUnit.MILLISECONDS) + " ms");
			boolean remove = this.serverScheduler.remove(server.getTimeouterTask());
			if (remove == false) {
				LOGGER.error("remove of server timeout failed server=" + server.getServerKey() + " delay="
						+ serverTimeout.getDelay(TimeUnit.MILLISECONDS) + " ms");
			}
		}
		this.serverScheduler.purge();
		// important to set timeouter null - rescheduling of same instance not possible
		server.setTimeout(null);
	}

	/**
	 * Reset server timeout. Careful in use - take care of synchronization when parallel request possible.
	 * 
	 * @param server
	 *            the server
	 * @param newTimeoutMillis
	 *            the new timeout in milliseconds
	 */
	public synchronized void resetServerTimeout(Server server, double newTimeoutMillis) {
		this.cancelServerTimeout(server);
		this.scheduleServerTimeout(server, newTimeoutMillis);
	}

	/**
	 * Dump the servers into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("servers");
		Set<Entry<String, Server>> entries = this.registryMap.entrySet();
		for (Entry<String, Server> entry : entries) {
			Server server = entry.getValue();
			server.dump(writer);
		}
		writer.writeEndElement(); // end of servers
	}

	/**
	 * The Class ServerTimeout. Gets control when a server times out. Responsible for cleaning up when server gets broken.
	 */
	private class ServerTimeout implements ITimeout {
		/** The server. */
		private Server server;
		/** The timeout. */
		private double timeoutMillis;

		/**
		 * Instantiates a new server timer run.
		 * 
		 * @param server
		 *            the server
		 */
		public ServerTimeout(Server server, double timeoutMillis) {
			this.server = server;
			this.timeoutMillis = server.getServerTimeoutMillis();
		}

		/**
		 * Timeout. Server timeout run out.
		 */
		@Override
		public void timeout() {
			/**
			 * broken stateful server procedure<br />
			 * 1. abort sessions and destroy server, everything done inside method<br />
			 */
			if (this.server instanceof StatefulServer) {
				((StatefulServer) this.server)
						.abortSessionsAndDestroy("Server timeout - refreshing server failed. Clean up dead server.");
				LOGGER.warn("Server timeout - refreshing server failed. Clean up dead server=" + this.server.getServerKey()
						+ " timeout(ms)=" + this.timeoutMillis);
			}
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return (int) this.timeoutMillis;
		}
	}
}
