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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class ResponderConfiguration. It may hold more than one configuration for a responder, is represented by
 * <code>ResponderConfig</code>.
 * 
 * @author JTraber
 */
public class ResponderConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ResponderConfiguration.class);

	private List<CommunicatorConfig> responderConfigList;

	public ResponderConfiguration() {
	}

	/**
	 * Gets the responder configuration list.
	 * 
	 * @return the responder configuration list
	 */
	public List<CommunicatorConfig> getResponderConfigList() {
		return this.responderConfigList;
	}

	public void init(CompositeConfiguration apacheCompositeConfig) throws SCMPValidatorException {
		@SuppressWarnings("unchecked")
		List<String> respondersList = apacheCompositeConfig.getList(Constants.PROPERTY_LISTENERS);
		if (respondersList == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property:"
					+ Constants.PROPERTY_LISTENERS + " not found");
		}
		// load all communicators in the list into the array
		this.responderConfigList = new ArrayList<CommunicatorConfig>();
		for (String responderName : respondersList) {
			responderName = responderName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(responderName);

			// get interfaces for responder
			List<String> interfaces = apacheCompositeConfig.getList(responderName + Constants.PROPERTY_QUALIFIER_INTERFACES, null);
			if (interfaces == null) {
				// interfaces not set in configuration file - listen to all NIC's
				interfaces = new ArrayList<String>();
				try {
					Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
					for (NetworkInterface netint : Collections.list(nets)) {
						Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
						for (InetAddress inetAddress : Collections.list(inetAdresses)) {
							if (inetAddress.getHostAddress().equals(Constants.IPV6_LOOPBACK_NIC)) {
								// ignore IPV6_LOOPBACK_NIC, bind not possible on this NIC
								continue;
							}
							interfaces.add(inetAddress.getHostAddress());
							logger.info("Responder " + responderName + " listens on " + inetAddress.getHostAddress());
						}
					}
				} catch (Exception e) {
					logger.fatal("unable to detect network interface", e);
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "wrong interface");
				}
			}
			commConfig.setInterfaces(interfaces);

			// get port, connectionType, username & password
			this.readPortConnectionTypeAndCredentials(apacheCompositeConfig, responderName, commConfig);

			// get remote host for responder
			String remoteHost = apacheCompositeConfig.getString(responderName + Constants.PROPERTY_QUALIFIER_REMOTE_HOST);
			// remote host is optional
			if (remoteHost != null) {
				// create configuration for remote host
				CommunicatorConfig remoteHostConfig = new CommunicatorConfig(remoteHost);
				// get host for remoteHost

				String host = apacheCompositeConfig.getString(remoteHost + Constants.PROPERTY_QUALIFIER_HOST);
				if (host == null) {
					logger.error(responderName + " host not set");
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, responderName + " host not set");
				}
				List<String> hosts = new ArrayList<String>();
				hosts.add(host);
				remoteHostConfig.setInterfaces(hosts);

				// get port, connectionType, username & password
				this.readPortConnectionTypeAndCredentials(apacheCompositeConfig, responderName, remoteHostConfig);
				try {
					// get keep alive interval
					remoteHostConfig.setKeepAliveIntervalSeconds(apacheCompositeConfig.getInt(remoteHost
							+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL));
					// get max connection pool size
					remoteHostConfig.setMaxPoolSize(apacheCompositeConfig.getInt(remoteHost
							+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE));
				} catch (Exception e) {
					logger.error(e.toString());
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.getMessage());
				}
				// set remote host configuration responder configuration
				commConfig.setRemoteHostConfiguration(remoteHostConfig);
			}
			// adding responder to list
			this.responderConfigList.add(commConfig);
		}
	}

	private void readPortConnectionTypeAndCredentials(CompositeConfiguration apacheCompositeConfig, String responderName,
			CommunicatorConfig commConfig) throws SCMPValidatorException {
		try {
			// get port
			commConfig.setPort(apacheCompositeConfig.getInt(responderName + Constants.PROPERTY_QUALIFIER_PORT));
		} catch (Exception e) {
			logger.error(e.toString());
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.getMessage());
		}
		// get connectionType
		String connectionType = apacheCompositeConfig.getString(responderName + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);
		if (connectionType == null) {
			logger.error(responderName + " connectionType not set");
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, responderName + " connectionType not set");
		}
		commConfig.setConnectionType(connectionType);

		// get user & password
		commConfig.setUsername(apacheCompositeConfig.getString(responderName + Constants.PROPERTY_QUALIFIER_USERNAME));
		commConfig.setPassword(apacheCompositeConfig.getString(responderName + Constants.PROPERTY_QUALIFIER_PASSWORD));
	}
}