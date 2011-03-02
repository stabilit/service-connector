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
public class ListenerListConfiguration {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(ListenerListConfiguration.class);

	private Map<String, ListenerConfiguration> listenerConfigurations;

	public ListenerListConfiguration() {
	}

	public void load(CompositeConfiguration compositeConfig, RemoteNodeListConfiguration remoteNodeListConfiguration)
			throws SCMPValidatorException {
		@SuppressWarnings("unchecked")
		List<String> listeners = compositeConfig.getList(Constants.PROPERTY_LISTENERS, null);
		if (listeners == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
					+ Constants.PROPERTY_LISTENERS + " is missing");
		}

		// load all communicators in the list into the array
		this.listenerConfigurations = new HashMap<String, ListenerConfiguration>();
		for (String listenerName : listeners) {
			listenerName = listenerName.trim(); // remove blanks in name
			ListenerConfiguration listenerConfig = new ListenerConfiguration(listenerName);
			// load it with the configurated items
			listenerConfig.load(compositeConfig, remoteNodeListConfiguration);
			if (this.listenerConfigurations.containsKey(listenerName) == true) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
						"listener already in registry name must be unique listenerName=" + listenerName);
			}
			// adding listener to the list
			this.listenerConfigurations.put(listenerName, listenerConfig);
			// show it
			LOGGER.info("Listener=" + listenerConfig.toString());
		}
	}

	/**
	 * Gets the listener configurations.
	 * 
	 * @return the listener configurations
	 */
	public Map<String, ListenerConfiguration> getListenerConfigurations() {
		return this.listenerConfigurations;
	}
}