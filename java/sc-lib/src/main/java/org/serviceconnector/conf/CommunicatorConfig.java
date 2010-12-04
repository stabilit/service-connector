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
package org.serviceconnector.conf;

import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class CommunicatorConfig.
 * 
 * @author JTraber
 */
public class CommunicatorConfig {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommunicatorConfig.class);

	/** The communicator name. */
	private String name;
	/** The port. */
	private int port;
	/** The interfaces. */
	private List<String> interfaces;
	/** The connectionType. */
	private String connectionType;
	/** The max pool size. */
	private int maxPoolSize;
	/** The keep alive interval. */
	private int keepAliveIntervalSeconds;
	/** The username. */
	private String username;
	/** The password. */
	private String password;
	/** The remote host configuration. */
	private CommunicatorConfig remoteHostConfiguration;

	public CommunicatorConfig(String name, List<String> interfaces, int port, String connectionType, int maxPoolSize,
			int keepAliveInterval, int keepAliveTimeout) {
		this.name = name;
		this.port = port;
		this.interfaces = interfaces;
		this.connectionType = connectionType;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveIntervalSeconds = keepAliveInterval;
		this.remoteHostConfiguration = null;
	}

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the communicator name
	 */
	public CommunicatorConfig(String name) {
		this.name = name;
		this.port = 0;
		this.interfaces = null;
		this.connectionType = null;
		this.maxPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
		this.remoteHostConfiguration = null;
	}

	/**
	 * Gets the communicator name.
	 * 
	 * @return the communicator name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the communicator name.
	 * 
	 * @param respName
	 *            the new communicator name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the interfaces.
	 * 
	 * @return the interfaces
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}

	/**
	 * Sets the interfaces.
	 * 
	 * @param interfaces
	 *            the new hosts
	 */
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * Gets the connection type.
	 * 
	 * @return the connection type
	 */
	public String getConnectionType() {
		return this.connectionType;
	}

	/**
	 * Sets the connection type.
	 * 
	 * @param connectionType
	 *            the new connection type
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the remote host.
	 * 
	 * @param remoteHostConfig
	 *            the new remote host
	 */
	public void setRemoteHostConfiguration(CommunicatorConfig remoteHostConfig) {
		this.remoteHostConfiguration = remoteHostConfig;
	}

	/**
	 * Gets the remote host config.
	 * 
	 * @return the remote host config
	 */
	public CommunicatorConfig getRemoteHostConfiguration() {
		return remoteHostConfiguration;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the max pool size.
	 * 
	 * @return the max pool size
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * Sets the max pool size.
	 * 
	 * @param maxPoolSize
	 *            the new max pool size
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Gets the keep alive interval.
	 * 
	 * @return the keep alive interval
	 */
	public int getKeepAliveIntervalSeconds() {
		return keepAliveIntervalSeconds;
	}

	/**
	 * Sets the keep alive interval.
	 * 
	 * @param keepAliveIntervalSeconds
	 *            the new keep alive interval
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) {
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}
}
