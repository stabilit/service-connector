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
package com.stabilit.scm.common.res;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.registry.jmx.RegistryEntryWrapperJMX;

/**
 * The Class EndpointAdapter. Provides basic functionality for endpoints.
 * 
 * @author JTraber
 */
public abstract class EndpointAdapter implements IEndpoint {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(EndpointAdapter.class);
	
	/** The responder. */
	protected IResponder resp;

	/**
	 * Instantiates a new EndpointAdapter.
	 */
	public EndpointAdapter() {
		this.resp = null;
	}

	/** {@inheritDoc} */
	@Override
	public IResponder getResponder() {
		return resp;
	}

	/** {@inheritDoc} */
	public void setResponder(IResponder resp) {
		this.resp = resp;
	}	
}
