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
package org.serviceconnector.cln;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileCtx;
import org.serviceconnector.util.FileUtility;
import org.slf4j.Logger;

public class TestAbstractClient extends Thread {

	/** The Constant LOGGER, must be initialized in subclass. */
	protected static Logger LOGGER = null;
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
			try {
				FileCtx fileChannel = FileUtility.createPIDfileAndLock(FileUtility.getLogPath() + fs + this.clientName + ".pid");
				// add exit handler
				this.addExitHandler(FileUtility.getLogPath() + fs + this.clientName + ".pid", fileChannel);
			} catch (SCMPValidatorException e1) {
				LOGGER.error("unable to get path to pid file", e1);
			} catch (Exception e) {
				LOGGER.error("unable to create pid file", e);
			}
			ctr = new ThreadSafeCounter();

			for (String methodString : this.methodsToInvoke) {
				try {
					Method method = this.getClass().getMethod(methodString);
					method.invoke(this);
				} catch (Exception e) {
					LOGGER.error("runClient " + methodString, e);
				}
			}
		} finally {
			try {
				this.p_detach();
			} catch (Exception e) {
				this.p_exit();
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
	private void addExitHandler(String pidFileNameFull, FileCtx fileChannel) {
		TestClientExitHandler exitHandler = new TestClientExitHandler(pidFileNameFull, fileChannel);
		Runtime.getRuntime().addShutdownHook(exitHandler);
	}

	/**
	 * The Class TestClientExitHandler.
	 */
	private static class TestClientExitHandler extends Thread {
		private String pidFileNameFull = null;
		private FileCtx fileCtx = null;

		public TestClientExitHandler(String pidFileNameFull, FileCtx fileCtx) {
			this.pidFileNameFull = pidFileNameFull;
			this.fileCtx = fileCtx;
		}

		@Override
		public void run() {
			try {
				this.fileCtx.releaseFileLockAndCloseChannel();
			} catch (Exception e) {
				LOGGER.debug("Releasing file lock failed");
			}
			File pidFile = this.fileCtx.getFile();
			if (pidFile.exists()) {
				pidFile.delete();
				LOGGER.debug("Delete PID-file: " + this.pidFileNameFull);
			}
			LOGGER.debug("TestClient exiting");
		}
	}
}
