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
	 * Inits the.
	 * 
	 * @param compositeConfig
	 *            the apache composite config
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {
		@SuppressWarnings("unchecked")
		List<String> requesterList = compositeConfig.getList(Constants.PROPERTY_REMOTE_NODES);
		if (requesterList == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
					+ Constants.PROPERTY_REMOTE_NODES + " is missing");
		}
		// load all communicators in the list into the array
		this.requesterConfigList = new ArrayList<CommunicatorConfig>();
		for (String requesterName : requesterList) {
			requesterName = requesterName.trim(); // remove blanks in name
			CommunicatorConfig commConfig = new CommunicatorConfig(requesterName);
	
			try {
				// get port
				commConfig.setPort(compositeConfig.getInt(requesterName + Constants.PROPERTY_QUALIFIER_PORT));
			} catch (Exception e) {
				logger.error(e.toString());
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.getMessage());
			}
			// get connectionType
			String connectionType = compositeConfig.getString(requesterName + Constants.PROPERTY_QUALIFIER_CONNECTION_TYPE);
			if (connectionType == null) {
				logger.error(requesterName + " connectionType not set");
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, requesterName + " connectionType not set");
			}
			commConfig.setConnectionType(connectionType);
	
			// get host for requester
			String host = compositeConfig.getString(requesterName + Constants.PROPERTY_QUALIFIER_HOST);
			if (host == null) {
				logger.error(requesterName + " host not set");
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, requesterName + " host not set");
			}
			List<String> hosts = new ArrayList<String>();
			hosts.add(host);
			commConfig.setInterfaces(hosts);
	
			// get max connection pool size
			Integer localMaxPoolSize = compositeConfig.getInteger(requesterName
					+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE, null);
			if (localMaxPoolSize == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + requesterName
						+ Constants.PROPERTY_QALIFIER_MAX_CONNECTION_POOL_SIZE + " is missing");
			}
			commConfig.setMaxPoolSize(localMaxPoolSize);
	
			// get keep alive interval
			Integer localKeepAliveIntervalSeconds = compositeConfig.getInteger(requesterName
					+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS, null);
			if (localKeepAliveIntervalSeconds == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + requesterName
						+ Constants.PROPERTY_QUALIFIER_KEEP_ALIVE_INTERVAL_SECONDS + " is missing");
			}
			commConfig.setKeepAliveIntervalSeconds(localMaxPoolSize);
	
			// adding requester to list
			this.requesterConfigList.add(commConfig);
		}
	}

	/**
	 * Gets the responder configuration list.
	 * 
	 * @return the responder configuration list
	 */
	public List<CommunicatorConfig> getRequesterConfigList() {
		return this.requesterConfigList;
	}
}