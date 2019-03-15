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
 * The Class ServiceListConfiguration.
 */
public class ServiceListConfiguration {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceListConfiguration.class);

	/** The service configurations. */
	private Map<String, ServiceConfiguration> serviceConfigurations;

	/**
	 * Load.
	 *
	 * @param config the config
	 * @throws SCMPValidatorException the sCMP validator exception
	 */
	public void load(CompositeConfiguration config) throws SCMPValidatorException {
		@SuppressWarnings("unchecked")
		List<String> serviceNames = config.getList(String.class, Constants.PROPERTY_SERVICE_NAMES);
		if (serviceNames == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + Constants.PROPERTY_SERVICE_NAMES + " is missing");
		}

		// load all remote nodes into the list
		this.serviceConfigurations = new HashMap<String, ServiceConfiguration>();
		for (String serviceName : serviceNames) {
			serviceName = serviceName.trim(); // remove blanks in name
			ServiceConfiguration serviceConfig = new ServiceConfiguration(serviceName);
			// load it with the configured items
			serviceConfig.load(config);
			if (this.serviceConfigurations.containsKey(serviceName) == true) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "service already in registry name must be unique serviceName=" + serviceName);
			}
			// adding service to list
			this.serviceConfigurations.put(serviceName, serviceConfig);
			// show it
			LOGGER.info("Service=" + serviceConfig.toString());
		}
	}

	/**
	 * Gets the service configurations.
	 *
	 * @return the service configurations
	 */
	public Map<String, ServiceConfiguration> getServiceConfigurations() {
		return this.serviceConfigurations;
	}
}
