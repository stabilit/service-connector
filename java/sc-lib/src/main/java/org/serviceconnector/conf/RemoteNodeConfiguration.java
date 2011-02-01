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
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

public class RemoteNodeConfiguration {

	/** The type. */
	private String type;
	/** The node name. */
	private String name;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The connectionType. */
	private String connectionType;
	/** The max pool size. */
	private int maxPoolSize;
	/** The keep alive interval. */
	private int keepAliveIntervalSeconds;
	/** The maxSessions (for file servers). */
	private int maxSessions;

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the communicator name
	 */
	public RemoteNodeConfiguration(String name) {
		this(name, null, 0, null, Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE, Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS, 0);
	}

	public RemoteNodeConfiguration(String name, String host, int port, String connectionType, int keepAliveIntervalInSeconds,
			int maxConnections, int maxSessions) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = keepAliveIntervalInSeconds;
		this.maxPoolSize = maxConnections;
		this.maxSessions = maxSessions;
	}

	public RemoteNodeConfiguration(String name, String host, int port, String connectionType, int keepAliveIntervalInSeconds,
			int maxConnections) {
		this(name, host, port, connectionType, keepAliveIntervalInSeconds, maxConnections, 0);
	}

	/**
	 * Load the configurated items
	 * 
	 * @param compositeConfig
	 * @throws SCMPValidatorException
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {
		// get host
		this.host = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_HOST);
		if (this.host == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_HOST + " is missing");
		}

		// get port
		Integer localPort = compositeConfig.getInteger(this.name + Constants.PROPERTY_QUALIFIER_PORT, null);
		if (localPort == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_PORT + " is missing");
		}
		this.port = localPort;

		// get connectionType
		this.connectionType = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);
		if (this.connectionType == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE + " is missing");
		}

		// get max connection pool size
		Integer localMaxPoolSize = compositeConfig.getInteger(this.name + Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE,
				null);
		if (localMaxPoolSize == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE + " is missing");
		}
		this.maxPoolSize = localMaxPoolSize;

		// get keep alive interval
		Integer localKeepAliveIntervalSeconds = compositeConfig.getInteger(this.name
				+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS, null);
		if (localKeepAliveIntervalSeconds == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS + " is missing");
		}
		this.keepAliveIntervalSeconds = localKeepAliveIntervalSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append(" on=[");
		builder.append(this.host);
		builder.append("]:");
		builder.append(port);
		builder.append(" /type=");
		builder.append(this.connectionType);
		builder.append("/poolSize=");
		builder.append(this.maxPoolSize);
		builder.append("/kpi=");
		builder.append(this.keepAliveIntervalSeconds);
		return builder.toString();
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return
	 */
	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * @return
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * @return
	 */
	public int getKeepAliveIntervalSeconds() {
		return keepAliveIntervalSeconds;
	}

	public int getMaxSessions() {
		return this.maxSessions;
	}
}
