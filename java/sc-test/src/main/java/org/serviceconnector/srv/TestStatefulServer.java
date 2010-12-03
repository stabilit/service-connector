package org.serviceconnector.srv;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileUtility;

public abstract class TestStatefulServer extends Thread {
	/** The Constant logger, must be initialized in subclass. */
	protected static Logger logger = null;
	/** The Constant sessionLogger. */
	protected final static SessionLogger sessionLogger = SessionLogger.getInstance();
	protected static final String fs = System.getProperty("file.separator");
	protected ThreadSafeCounter ctr;

	protected int listenerPort;
	protected int port;
	protected int maxSessions;
	protected int maxConnections;
	protected String serviceNames;
	protected String serverName;
	protected ConnectionType connectionType;

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
			try {
				this.server.deregister();
			} catch (Exception e) {
				logger.error("run", e);
			} finally {
				try {
					this.server.getSCServer().stopListener();
					// sleep for 1/2 seconds before killing the server
					Thread.sleep(500);
				} catch (InterruptedException e) {
				} finally {
					System.exit(0);
				}
			}
		}
	}

	/**
	 * Adds the shutdown hook.
	 */
	protected void addExitHandler(String pidFileNameFull) {
		TestServerExitHandler exitHandler = new TestServerExitHandler(pidFileNameFull);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestServerExitHandler.
	 */
	private static class TestServerExitHandler extends Thread {
		private String pidFileNameFull = null;

		public TestServerExitHandler(String pidFileNameFull) {
			this.pidFileNameFull = pidFileNameFull;
		}

		@Override
		public void run() {
			FileUtility.deletePIDfile(this.pidFileNameFull);
			logger.log(Level.OFF, "Delete PID-file: " + this.pidFileNameFull);
			logger.log(Level.OFF, "TestServer exiting");
			logger.log(Level.OFF, "<<<");
		}
	}
}
