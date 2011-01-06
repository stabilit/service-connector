package org.serviceconnector.cln;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileUtility;

public class TestAbstractClient extends Thread {

	/** The Constant logger, must be initialized in subclass. */
	protected static Logger logger = null;
	/** The Constant sessionLogger. */
	protected static final String fs = System.getProperty("file.separator");
	protected ThreadSafeCounter ctr;

	protected List<String> methodsToInvoke;
	protected ConnectionType connectionType;
	protected int port;
	protected String host;
	protected int keepAliveIntervalSeconds;
	protected int maxConnections;
	protected String serviceName;
	protected String clientName;
	protected SCClient client;
	protected SCService service;

	@Override
	public void run() {
		try {
			FileUtility.createPIDfile(FileUtility.getPath() + fs + this.clientName + ".pid");
			// add exit handler
			this.addExitHandler(FileUtility.getPath() + fs + this.clientName + ".pid");
		} catch (SCMPValidatorException e1) {
			logger.fatal("unable to get path to pid file", e1);
		} catch (Exception e) {
			logger.fatal("unable to create pid file", e);
		}
		ctr = new ThreadSafeCounter();

		for (String methodString : this.methodsToInvoke) {
			try {
				Method method = this.getClass().getMethod(methodString);
				method.invoke(this);
			} catch (Exception e) {
				logger.error("runSessionClient " + methodString, e);
			}
		}
	}
	
	public void p_initAttach() throws Exception {
		client = new SCClient(this.host, this.port, this.connectionType);
		client.setKeepAliveIntervalSeconds(this.keepAliveIntervalSeconds);
		client.setMaxConnections(this.maxConnections);
		client.attach();
	}
	
	public void p_detach() throws Exception {
		client.detach();
	}
	
	public void p_exit() {
		System.exit(0);
	}

	public void setMethodsToInvoke(List<String> methodsToInvoke) {
		this.methodsToInvoke = methodsToInvoke;
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

	public void setPort(int port) {
		this.port = port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	/**
	 * Adds the shutdown hook.
	 */
	private void addExitHandler(String pidFileNameFull) {
		TestClientExitHandler exitHandler = new TestClientExitHandler(pidFileNameFull);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestClientExitHandler.
	 */
	private static class TestClientExitHandler extends Thread {
		private String pidFileNameFull = null;

		public TestClientExitHandler(String pidFileNameFull) {
			this.pidFileNameFull = pidFileNameFull;
		}

		@Override
		public void run() {
			File pidFile = new File(this.pidFileNameFull);
			if (pidFile.exists()) {
				pidFile.delete();
				logger.log(Level.OFF, "Delete PID-file: " + this.pidFileNameFull);
			}
			logger.log(Level.OFF, "TestClient exiting");
			logger.log(Level.OFF, "<<<");
		}
	}
}
