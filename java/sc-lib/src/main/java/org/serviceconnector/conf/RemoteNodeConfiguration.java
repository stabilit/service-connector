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
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.ServerType;
import org.serviceconnector.util.ValidatorUtility;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class RemoteNodeConfiguration.
 */
public class RemoteNodeConfiguration {

	/** The serverType. */
	private ServerType serverType;
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
	/** The check registration interval seconds. */
	private int checkRegistrationIntervalSeconds;
	/** The maxSessions (for file servers). */
	private int maxSessions;
	/** the HTTP URL file qualifier which is added to the URL when communicating to a HTTP server. */
	private String httpUrlFileQualifier = Constants.SLASH;

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the remote node name
	 */
	public RemoteNodeConfiguration(String name) {
		this(ServerType.UNDEFINED, name, null, 0, null, Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE,
				Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS, Constants.DEFAULT_CHECK_REGISTRATION_INTERVAL_SECONDS, 0,
				Constants.SLASH);
	}

	/**
	 * Instantiates a new remote node configuration.
	 * 
	 * @param serverType
	 *            the server type
	 * @param name
	 *            the name
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 * @param keepAliveIntervalSeconds
	 *            the keep alive interval seconds
	 * @param maxConnections
	 *            the max connections
	 * @param maxSessions
	 *            the max sessions
	 */
	public RemoteNodeConfiguration(ServerType serverType, String name, String host, int port, String connectionType,
			int keepAliveIntervalSeconds, int checkRegistrationIntervalSeconds, int maxConnections, int maxSessions,
			String httpUrlFileQualifier) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
		this.maxPoolSize = maxConnections;
		this.maxSessions = maxSessions;
		this.serverType = serverType;
		this.httpUrlFileQualifier = httpUrlFileQualifier;
		this.checkRegistrationIntervalSeconds = checkRegistrationIntervalSeconds;
	}

	/**
	 * Instantiates a new remote node configuration.
	 * 
	 * @param name
	 *            the name
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 * @param keepAliveIntervalSeconds
	 *            the keep alive interval seconds
	 * @param maxConnections
	 *            the max connections
	 */
	public RemoteNodeConfiguration(String name, String host, int port, String connectionType, int keepAliveIntervalSeconds,
			int checkRegistrationIntervalSeconds, int maxConnections) {
		this(ServerType.UNDEFINED, name, host, port, connectionType, keepAliveIntervalSeconds, checkRegistrationIntervalSeconds,
				maxConnections, 0, Constants.SLASH);
	}

	/**
	 * Load the configurated items.
	 * 
	 * @param compositeConfig
	 *            the composite config
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
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
		ValidatorUtility.validateInt(1, this.port, SCMPError.HV_WRONG_PORTNR);

		// get connectionType
		this.connectionType = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);
		if (this.connectionType == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE + " is missing");
		}
		ConnectionType connectionTypeConf = ConnectionType.getType(this.connectionType);
		if (connectionTypeConf == ConnectionType.UNDEFINED) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown connectionType=" + this.name
					+ this.connectionType);
		}

		// get serverType
		String serverTypeValue = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_TYPE);
		if (serverTypeValue == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_TYPE + " is missing");
		}
		this.serverType = ServerType.getType(serverTypeValue);
		if (this.serverType == ServerType.UNDEFINED) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown type=" + this.name + this.serverType);
		}
		if (serverType == ServerType.STATEFUL_SERVER) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "stateful server=" + this.name + this.serverType
					+ "must not be configured");
		}

		if ((serverType == ServerType.FILE_SERVER) || (serverType == ServerType.WEB_SERVER)
				|| (serverType == ServerType.CASCADED_SC)) {
			// get max connection pool size
			Integer localMaxPoolSize = compositeConfig.getInteger(this.name + Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE,
					null);
			if (localMaxPoolSize == null) {
				localMaxPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
			}
			this.maxPoolSize = localMaxPoolSize;
			ValidatorUtility.validateInt(1, this.maxPoolSize, SCMPError.HV_WRONG_MAX_CONNECTIONS);

			// get keep alive interval
			Integer localKeepAliveIntervalSeconds = compositeConfig.getInteger(this.name
					+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS, null);
			if (localKeepAliveIntervalSeconds == null) {
				localKeepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
			}
			this.keepAliveIntervalSeconds = localKeepAliveIntervalSeconds;
			ValidatorUtility.validateInt(0, this.keepAliveIntervalSeconds, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		}

		if (serverType == ServerType.FILE_SERVER) {
			// get maxSessions
			Integer localMaxSessions = compositeConfig.getInteger(this.name + Constants.PROPERTY_QALIFIER_MAX_SESSIONS, null);
			if (localMaxSessions == null) {
				localMaxSessions = Constants.DEFAULT_MAX_FILE_SESSIONS;
			}
			this.maxSessions = localMaxSessions;
			ValidatorUtility.validateInt(1, this.maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
		}
	}

	/**
	 * Dump the remote node into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("remote-node");
		writer.writeAttribute("host", this.host);
		writer.writeAttribute("port", this.port);
		writer.writeElement("maxPoolSize", this.maxPoolSize);
		writer.writeElement("maxSessions", this.maxSessions);
		writer.writeElement("keepAliveIntervalSeconds", this.keepAliveIntervalSeconds);
		writer.writeElement("serverType", this.serverType.getValue());
		writer.writeEndElement(); // end of remote-node
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the remote host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return connectionType
	 */
	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * @return maxPoolSize for file or web servers or cascaded SCs
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * @return keepAliveIntervalSeconds for file or web servers or cascaded SCs
	 */
	public int getKeepAliveIntervalSeconds() {
		return keepAliveIntervalSeconds;
	}

	/**
	 * Gets the check registration interval seconds.
	 * 
	 * @return the check registration interval seconds
	 */
	public int getCheckRegistrationIntervalSeconds() {
		return checkRegistrationIntervalSeconds;
	}

	/**
	 * @return serverType (called type in the configuration)
	 */
	public ServerType getServerType() {
		return this.serverType;
	}

	/**
	 * @return the HTTP URL qualifier.
	 */
	public String getHttpUrlFileQualifier() {
		return httpUrlFileQualifier;
	}

	/**
	 * @return maxSessions for file servers
	 */
	public int getMaxSessions() {
		return maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append(" type=");
		builder.append(this.serverType);
		builder.append(" on=[");
		builder.append(this.host);
		builder.append("]:");
		builder.append(port);
		builder.append(" /type=");
		builder.append(this.connectionType);
		builder.append("/kpi=");
		builder.append(this.keepAliveIntervalSeconds);
		builder.append("/mxc=");
		builder.append(this.maxPoolSize);
		builder.append("/mxs=");
		builder.append(this.maxSessions);
		return builder.toString();
	}
}
