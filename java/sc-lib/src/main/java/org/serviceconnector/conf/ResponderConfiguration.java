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
			commConfig.initialize(apacheCompositeConfig);
			this.responderConfigList.add(commConfig);
		}

	}
}
