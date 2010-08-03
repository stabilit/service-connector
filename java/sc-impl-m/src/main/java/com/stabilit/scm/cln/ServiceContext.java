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
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCContext;

/**
 * The Class ServiceContext. Context of a service. Holds information about the service himself and the
 * serviceConnectorContext which uses service.
 */
public class ServiceContext implements IServiceContext {

	/** The service connector context. */
	private ISCContext serviceConnectorContext;
	/** The service. */
	private Service service;

	/**
	 * Instantiates a new service context.
	 * 
	 * @param serviceConnectorContext
	 *            the service connector context
	 * @param service
	 *            the service
	 */
	public ServiceContext(ISCContext serviceConnectorContext, Service service) {
		this.serviceConnectorContext = serviceConnectorContext;
		this.service = service;
	}

	/** {@inheritDoc} */
	@Override
	public ISC getServiceConnector() {
		return this.serviceConnectorContext.getServiceConnector();
	}

	/** {@inheritDoc} */
	@Override
	public Service getService() {
		return this.service;
	}
}
