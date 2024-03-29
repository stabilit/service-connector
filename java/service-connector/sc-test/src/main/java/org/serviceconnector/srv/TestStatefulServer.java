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
package org.serviceconnector.srv;

import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileCtx;
import org.serviceconnector.util.FileUtility;
import org.slf4j.Logger;

public abstract class TestStatefulServer extends Thread {

	/** The Constant LOGGER, must be initialized in subclass. */
	protected static Logger LOGGER = null;
	/** The Constant sessionLogger. */
	protected static final String fs = System.getProperty("file.separator");
	protected ThreadSafeCounter ctr;

	protected int listenerPort;
	protected int port;
	protected int maxSessions;
	protected int maxConnections;
	protected String serviceNames;
	protected String serverName;
	protected ConnectionType connectionType;
	protected String nicsStrings;

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setNics(String nics) {
		this.nicsStrings = nics;
	}

	public void setServiceNames(String serviceNames) {
		this.serviceNames = serviceNames;
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		if (connectionType.equals(ConnectionType.NETTY_HTTP.getValue())) {
			this.connectionType = ConnectionType.NETTY_HTTP;
		} else if (connectionType.equals(ConnectionType.NETTY_TCP.getValue())) {
			this.connectionType = ConnectionType.NETTY_TCP;
		}
	}

	protected class KillThread<T extends SCSessionServer> extends Thread {
		private T server;

		public KillThread(T server) {
			this.server = server;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			// sleep for 1/2 seconds before killing the server
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
			try {
				this.server.deregister();
			} catch (Exception e) {
				LOGGER.error("deregister", e);
			}
			try {
				this.server.getSCServer().stopListener();
			} catch (Exception e) {
				LOGGER.error("stopListener", e);
			}
			System.exit(0);
		}
	}

	/**
	 * Adds the shutdown hook.
	 */
	protected void addExitHandler(String pidFileNameFull, FileCtx fileCtx) {
		TestServerExitHandler exitHandler = new TestServerExitHandler(pidFileNameFull, fileCtx);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestServerExitHandler.
	 */
	private static class TestServerExitHandler extends Thread {
		private String pidFileNameFull = null;
		private FileCtx fileCtx = null;

		public TestServerExitHandler(String pidFileNameFull, FileCtx fileCtx) {
			this.pidFileNameFull = pidFileNameFull;
			this.fileCtx = fileCtx;
		}

		@Override
		public void run() {
			fileCtx.releaseFileLockAndCloseChannel();
			FileUtility.deleteFile(this.pidFileNameFull);
			LOGGER.info("Delete PID-file=" + this.pidFileNameFull);
			LOGGER.debug("TestServer exit");
		}
	}
}
