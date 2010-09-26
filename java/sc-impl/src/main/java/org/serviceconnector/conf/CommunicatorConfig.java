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

import org.apache.log4j.Logger;

/**
 * The Class CommunicatorConfig.
 * 
 * @author JTraber
 */
public class CommunicatorConfig {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommunicatorConfig.class);

	/** The communicator name. */
	private String communicatorName;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The connectionKey. */
	private String connectionType;
	/** The max pool size. */
	private int maxPoolSize;
	/** The keep alive interval. */
	private int keepAliveInterval;
	private double operationTimeoutMultiplier;
	/** The userid. */
	private String userid;
	/** The password. */
	private String password;

	/**
	 * Instantiates a new communicator configuration.
	 */
	CommunicatorConfig() {
	}

	public CommunicatorConfig(String communicatorName, String host, int port, String connectionType, int maxPoolSize,
			int keepAliveInterval, int keepAliveTimeout) {
		super();
		this.communicatorName = communicatorName;
		this.port = port;
		this.host = host;
		this.connectionType = connectionType;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveInterval = keepAliveInterval;
		this.operationTimeoutMultiplier = 0;
	}

	/**
	 * The Constructor.
	 * 
	 * @param respName
	 *            the responder name
	 */
	public CommunicatorConfig(String respName) {
		this.communicatorName = respName;
	}

	/**
	 * Gets the communicator name.
	 * 
	 * @return the communicator name
	 */
	public String getCommunicatorName() {
		return communicatorName;
	}

	/**
	 * Sets the communicator name.
	 * 
	 * @param respName
	 *            the new communicator name
	 */
	public void setCommunicatorName(String respName) {
		this.communicatorName = respName;
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
	 * Sets the userid.
	 * 
	 * @param userid
	 *            the new userid
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * Gets the userid.
	 * 
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
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

	/**
	 * Sets the operation timeout multiplier.
	 * 
	 * @param operationTimeoutMultiplier
	 *            the new operation timeout multiplier
	 */
	public void setOperationTimeoutMultiplier(double operationTimeoutMultiplier) {
		this.operationTimeoutMultiplier = operationTimeoutMultiplier;
	}

	/**
	 * Gets the operation timeout multiplier.
	 * 
	 * @return the operation timeout multiplier
	 */
	public double getOperationTimeoutMultiplier() {
		return operationTimeoutMultiplier;
	}

}
