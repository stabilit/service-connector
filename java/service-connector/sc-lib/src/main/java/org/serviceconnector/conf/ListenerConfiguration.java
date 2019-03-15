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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.ServerType;
import org.serviceconnector.util.ValidatorUtility;
import org.serviceconnector.util.XMLDumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ListenerConfiguration.
 *
 * @author JTraber
 */
public class ListenerConfiguration {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConfiguration.class);

	/** The listener name. */
	private String name;
	/** The port. */
	private int port;
	/** The interfaces. */
	private List<String> networkInterfaces;
	/** The connectionType. */
	private String connectionType;
	/** The username. */
	private String username;
	/** The password. */
	private String password;
	/** The remote node configuration. */
	private RemoteNodeConfiguration remoteNodeConfiguration;

	/**
	 * The Constructor.
	 *
	 * @param name the listener name
	 */
	public ListenerConfiguration(String name) {
		this.name = name;
		this.port = 0;
		this.networkInterfaces = null;
		this.connectionType = null;
		this.username = null;
		this.password = null;
		this.remoteNodeConfiguration = null;
	}

	/**
	 * Load the configurated items.
	 *
	 * @param compositeConfig the composite config
	 * @param remoteNodeListConfiguration the remote node list configuration
	 * @throws SCMPValidatorException the sCMP validator exception
	 */

	@SuppressWarnings("unchecked")
	public void load(CompositeConfiguration compositeConfig, RemoteNodeListConfiguration remoteNodeListConfiguration) throws SCMPValidatorException {

		// get interfaces for listener
		networkInterfaces = compositeConfig.getList(String.class, this.name + Constants.PROPERTY_QUALIFIER_INTERFACES, null);
		if (networkInterfaces == null) {
			// interfaces not set in configuration file - get all NIC's
			networkInterfaces = new ArrayList<String>();
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAdresses)) {
						if (inetAddress instanceof Inet6Address) {
							// ignore IPV6 addresses, bind not possible on this NIC
							continue;
						}
						networkInterfaces.add(inetAddress.getHostAddress());
					}
				}
			} catch (Exception e) {
				LOGGER.error("unable to detect network interface", e);
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "wrong interface");
			}
		}

		// get port
		Integer localPort = compositeConfig.getInteger(this.name + Constants.PROPERTY_QUALIFIER_PORT, null);
		if (localPort == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_PORT + " is missing");
		}
		this.port = localPort;
		ValidatorUtility.validateInt(1, this.port, SCMPError.HV_WRONG_PORTNR);

		// get connectionType
		this.connectionType = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);
		if (this.connectionType == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE + " is missing");
		}
		ConnectionType connectionTypeConf = ConnectionType.getType(this.connectionType);
		if (connectionTypeConf == ConnectionType.UNDEFINED) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown connectionType=" + this.name + this.connectionType);
		}

		// get username & password for netty.web
		if (connectionTypeConf == ConnectionType.NETTY_WEB) {
			this.username = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_USERNAME, null);
			if (this.username == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_USERNAME + " is missing");
			}
			this.password = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_PASSWORD, null);
			if (this.password == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_PASSWORD + " is missing");
			}
		}

		// get remote host config for http-proxy
		if (connectionTypeConf == ConnectionType.NETTY_PROXY_HTTP) {
			String remoteNode = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE);
			if (remoteNode == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE + " is missing");
			}
			RemoteNodeConfiguration remoteNodeConfig = remoteNodeListConfiguration.getRequesterConfigurations().get(remoteNode);

			// remote node must be a web server or a cascaded SC
			if ((remoteNodeConfig.getServerType() == ServerType.WEB_SERVER || remoteNodeConfig.getServerType() == ServerType.CASCADED_SC) == false) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "remote node=" + remoteNode + " is not a web server");
			}

			// set remote host configuration into the listener configuration
			this.remoteNodeConfiguration = remoteNodeConfig;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append(" on=");
		builder.append(this.networkInterfaces.toString());
		builder.append(":");
		builder.append(this.port);
		builder.append(" /type=");
		builder.append(this.connectionType);
		if (this.remoteNodeConfiguration != null) {
			builder.append("/remote=");
			builder.append(this.remoteNodeConfiguration.getHost());
		}
		return builder.toString();
	}

	/**
	 * Dump the listener config into the xml writer.
	 *
	 * @param writer the writer
	 * @throws Exception the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("type", this.connectionType);
		writer.writeElement("interfaces", this.networkInterfaces.toString());
		if (username != null) {
			writer.writeElement("username", this.username);
		}
		if (password != null) {
			writer.writeElement("password", this.password);
		}
		if (remoteNodeConfiguration != null) {
			this.remoteNodeConfiguration.dump(writer);
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
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the interfaces.
	 *
	 * @return the interfaces
	 */
	public List<String> getInterfaces() {
		return networkInterfaces;
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
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the remote node config.
	 *
	 * @return the remote node config
	 */
	public RemoteNodeConfiguration getRemoteNodeConfiguration() {
		return remoteNodeConfiguration;
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
	 * Gets the network interfaces.
	 *
	 * @return the network interfaces
	 */
	public List<String> getNetworkInterfaces() {
		return networkInterfaces;
	}

	/**
	 * Sets the network interfaces.
	 *
	 * @param networkInterfaces the new network interfaces
	 */
	public void setNetworkInterfaces(List<String> networkInterfaces) {
		this.networkInterfaces = networkInterfaces;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets the connection type.
	 *
	 * @param connectionType the new connection type
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
}
