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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class RequesterConfiguration. It may hold more than one configuration for a requester, is represented by
 * <code>RequesterConfig</code>.
 * 
 * @author JTraber
 */
public class RequesterConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RequesterConfiguration.class);

	private List<CommunicatorConfig> requesterConfigList;

	public RequesterConfiguration() {
	}

	/**
	 * Gets the responder configuration list.
	 * 
	 * @return the responder configuration list
	 */
	public List<CommunicatorConfig> getRequesterConfigList() {
		return this.requesterConfigList;
	}

	/**
	 * Inits the.
	 * 
	 * @param apacheCompositeConfig
	 *            the apache composite config
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void init(CompositeConfiguration apacheCompositeConfig) throws SCMPValidatorException {
		@SuppressWarnings("unchecked")
		List<String> requesterList = apacheCompositeConfig.getList(Constants.PROPERTY_REMOTE_NODES);
		if (requesterList == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property:"
					+ Constants.PROPERTY_REMOTE_NODES + " not found");
		}
		// load all communicators in the list into the array
		this.requesterConfigList = new ArrayList<CommunicatorConfig>();
		for (String requesterName : requesterList) {
			requesterName = requesterName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(requesterName);
			
			// get port & connection type
			commConfig.setPort(apacheCompositeConfig.getInt(requesterName + Constants.PROPERTY_QUALIFIER_PORT));
			commConfig.setConnectionType((String) apacheCompositeConfig.getString(requesterName
					+ Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE));
			// get host for requester
			List<String> hosts = new ArrayList<String>();
			hosts.add(apacheCompositeConfig.getString(requesterName + Constants.PROPERTY_QUALIFIER_HOST));
			commConfig.setInterfaces(hosts);

			// get max connection pool size
			String maxPoolSizeValue = (String) apacheCompositeConfig.getString(requesterName
					+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE);
			if (maxPoolSizeValue != null) {
				int maxPoolSize = Integer.parseInt(maxPoolSizeValue);
				commConfig.setMaxPoolSize(maxPoolSize);
			}
			// get keep alive interval
			String keepAliveIntervalValue = (String) apacheCompositeConfig.getString(requesterName
					+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL);
			int keepAliveInterval = 0;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			commConfig.setKeepAliveInterval(keepAliveInterval);
			// adding requester to list
			this.requesterConfigList.add(commConfig);
		}
	}
}