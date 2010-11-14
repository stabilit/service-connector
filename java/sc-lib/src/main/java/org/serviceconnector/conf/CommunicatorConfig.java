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

import org.apache.commons.configuration.CompositeConfiguration;
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
	/** The host. */
	private String host;
	/** The connectionType. */
	private String connectionType;	
	/** The max pool size. */
	private int maxPoolSize;
	/** The keep alive interval. */
	private int keepAliveInterval;
	/** The username. */
	private String username;
	/** The password. */
	private String password;
	/** The remote host configuration. */
	private CommunicatorConfig remoteHostConfiguration;

	public CommunicatorConfig(String name, String host, int port, String connectionType, int maxPoolSize,
			int keepAliveInterval, int keepAliveTimeout) {
		this.name = name;
		this.port = port;
		this.host = host;
		this.connectionType = connectionType;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveInterval = keepAliveInterval;
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
	}

	public void initialize(CompositeConfiguration configurations) {
		int port = Integer.parseInt((String) configurations.getString(this.name + Constants.PROPERTY_QUALIFIER_PORT));
		String maxPoolSizeValue = (String) configurations.getString(this.name	+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE);
		if (maxPoolSizeValue != null) {
			int maxPoolSize = Integer.parseInt(maxPoolSizeValue);
			this.setMaxPoolSize(maxPoolSize);
		} // TODO TRN where is the else setting the default?
		String keepAliveIntervalValue = (String) configurations.getString(this.name + Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL);
		int keepAliveInterval = 0;
		if (keepAliveIntervalValue != null) {
			keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
		} // TODO TRN where is the else setting the default?
		this.setKeepAliveInterval(keepAliveInterval);
		this.setPort(port);
		this.setHost((String) configurations.getString(this.name  + Constants.PROPERTY_QUALIFIER_HOST));
		this.setConnectionType((String) configurations.getString(this.name	+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE));
		this.setUsername((String) configurations.getString(this.name  + Constants.PROPERTY_QUALIFIER_USERNAME));
		this.setPassword((String) configurations.getString(this.name + Constants.PROPERTY_QUALIFIER_PASSWORD));
		// get remote host
		String remoteHost = (String) configurations.getString(this.name + Constants.PROPERTY_QUALIFIER_REMOTE_HOST);
		if (remoteHost != null) {
			CommunicatorConfig remoteHostConfig = new CommunicatorConfig(remoteHost);
			remoteHostConfig.initialize(configurations);
			this.setRemoteHostConfiguration(remoteHostConfig);
		}
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
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the new host
	 */
	public void setHost(String host) {
		this.host = host;
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
	 * @param remoteHostConfig the new remote host
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
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * Sets the keep alive interval.
	 * 
	 * @param keepAliveInterval
	 *            the new keep alive interval
	 */
	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}
}
