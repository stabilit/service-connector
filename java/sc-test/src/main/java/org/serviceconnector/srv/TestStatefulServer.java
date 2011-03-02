package org.serviceconnector.srv;

import java.io.IOException;
import java.nio.channels.FileLock;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileUtility;

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
	protected void addExitHandler(String pidFileNameFull, FileLock pidLock) {
		TestServerExitHandler exitHandler = new TestServerExitHandler(pidFileNameFull, pidLock);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestServerExitHandler.
	 */
	private static class TestServerExitHandler extends Thread {
		private String pidFileNameFull = null;
		private FileLock pidLock = null;

		public TestServerExitHandler(String pidFileNameFull, FileLock pidLock) {
			this.pidFileNameFull = pidFileNameFull;
			this.pidLock = pidLock;
		}

		@Override
		public void run() {
			try {
				pidLock.release();
			} catch (IOException e) {
			}
			FileUtility.deleteFile(this.pidFileNameFull);
			LOGGER.info("Delete PID-file=" + this.pidFileNameFull);
			LOGGER.log(Level.OFF, "TestServer exit");
			LOGGER.log(Level.OFF, "<<<");
		}
	}
}
