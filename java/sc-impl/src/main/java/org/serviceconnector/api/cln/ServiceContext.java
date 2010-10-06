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
package org.serviceconnector.api.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCService;
import org.serviceconnector.service.ISCCommunicator;

/**
 * The Class ServiceContext. Context of a service. Holds information about the service himself and the
 * serviceConnectorContext which uses service.
 */
public class ServiceContext {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceContext.class);

	/** The service connector context. */
	private ServiceConnectorContext serviceConnectorContext;
	/** The service. */
	private SCService service;

	/**
	 * Instantiates a new service context.
	 * 
	 * @param serviceConnectorContext
	 *            the service connector context
	 * @param service
	 *            the service
	 */
	public ServiceContext(ServiceConnectorContext serviceConnectorContext, SCService service) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.service = service;
	}

	public ISCCommunicator getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

	public SCService getService() {
		return this.service;
	}
}
