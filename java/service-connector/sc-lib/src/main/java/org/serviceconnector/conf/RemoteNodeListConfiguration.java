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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class RequesterConfiguration. It may hold more than one configuration for a requester, is represented by <code>RemoteNodeConfiguration</code>.
 *
 * @author JTraber
 */
public class RemoteNodeListConfiguration {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNodeListConfiguration.class);
	/** The remote node configurations. */
	private Map<String, RemoteNodeConfiguration> remoteNodeConfigurations;

	/**
	 * Instantiates a new remote node list configuration.
	 */
	public RemoteNodeListConfiguration() {
	}

	/**
	 * Initializes the requester configuration.
	 *
	 * @param compositeConfig the apache composite config
	 * @throws SCMPValidatorException the SCMP validator exception
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {
		List<String> requesterList = compositeConfig.getList(String.class, Constants.PROPERTY_REMOTE_NODES);
		if (requesterList == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.PROPERTY_REMOTE_NODES + " is missing");
		}

		// load all remote nodes into the list
		this.remoteNodeConfigurations = new HashMap<String, RemoteNodeConfiguration>();
		for (String requesterName : requesterList) {
			requesterName = requesterName.trim(); // remove blanks in name
			RemoteNodeConfiguration remoteNodeConfig = new RemoteNodeConfiguration(requesterName);
			// load it with the configurated items
			remoteNodeConfig.load(compositeConfig);
			if (this.remoteNodeConfigurations.containsKey(requesterName) == true) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "requester already in registry name must be unique requesterName=" + requesterName);
			}
			// adding requester to list
			this.remoteNodeConfigurations.put(requesterName, remoteNodeConfig);
			// show it
			LOGGER.info("RemoteNode=" + remoteNodeConfig.toString());
		}
	}

	/**
	 * Gets the requester configurations.
	 *
	 * @return the requester configurations
	 */
	public Map<String, RemoteNodeConfiguration> getRequesterConfigurations() {
		return this.remoteNodeConfigurations;
	}
}
