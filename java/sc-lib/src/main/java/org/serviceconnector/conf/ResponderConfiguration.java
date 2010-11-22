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
							interfaces.add(inetAddress.getHostAddress());
							logger.info("Responder " + responderName + "listens on " + inetAddress.getHostAddress());
						}
					}
				} catch (Exception e) {
					logger.info("unable to reveal network interfaces");
				}
			}
			commConfig.setInterfaces(interfaces);

			// get port & connection type
			try {
				commConfig.setPort(apacheCompositeConfig.getInt(responderName + Constants.PROPERTY_QUALIFIER_PORT));
			} catch (Exception e) {
				logger.info(e.toString());
			}
			try {
				commConfig.setConnectionType((String) apacheCompositeConfig.getString(responderName
						+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE));
			} catch (Exception e) {
				logger.info(e.toString());
			}

			// get user & password
			try {
				commConfig.setUsername((String) apacheCompositeConfig.getString(responderName
						+ Constants.PROPERTY_QUALIFIER_USERNAME));
			} catch (Exception e) {
				logger.info(e.toString());
			}
			try {
				commConfig.setPassword((String) apacheCompositeConfig.getString(responderName
						+ Constants.PROPERTY_QUALIFIER_PASSWORD));
			} catch (Exception e) {
				logger.info(e.toString());
			}
			// get remote host for responder
			String remoteHost = null;
			try {
				remoteHost = (String) apacheCompositeConfig.getString(responderName + Constants.PROPERTY_QUALIFIER_REMOTE_HOST);
			} catch (Exception e) {
				logger.info(e.toString());
			}

			if (remoteHost != null) {
				// create configuration for remote host
				CommunicatorConfig remoteHostConfig = new CommunicatorConfig(remoteHost);
				// get host for remoteHost
				try {
					List<String> hosts = new ArrayList<String>();
					hosts.add(apacheCompositeConfig.getString(remoteHost + Constants.PROPERTY_QUALIFIER_HOST));
					remoteHostConfig.setInterfaces(hosts);
				} catch (Exception e) {
					logger.info(e.toString());
				}
				// get port & connection type
				try {
					remoteHostConfig.setPort(apacheCompositeConfig.getInt(remoteHost + Constants.PROPERTY_QUALIFIER_PORT));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				try {
					remoteHostConfig.setConnectionType((String) apacheCompositeConfig.getString(remoteHost
							+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				// get user & password
				try {
					remoteHostConfig.setUsername((String) apacheCompositeConfig.getString(remoteHost
							+ Constants.PROPERTY_QUALIFIER_USERNAME));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				try {
					remoteHostConfig.setPassword((String) apacheCompositeConfig.getString(remoteHost
							+ Constants.PROPERTY_QUALIFIER_PASSWORD));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				// get keep alive interval
				try {
					remoteHostConfig.setKeepAliveInterval(apacheCompositeConfig.getInt(remoteHost
							+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				// get max connection pool size
				try {
					remoteHostConfig.setMaxPoolSize(apacheCompositeConfig.getInt(remoteHost
							+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE));
				} catch (Exception e) {
					logger.info(e.toString());
				}
				// set remote host configuration responder configuration
				commConfig.setRemoteHostConfiguration(remoteHostConfig);
			}
			// adding responder to list
			this.responderConfigList.add(commConfig);
		}
	}
}