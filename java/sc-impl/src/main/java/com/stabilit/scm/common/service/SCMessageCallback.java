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
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IService;

/**
 * The Class SCMessageCallback.
 * 
 * @author JTraber
 */
public abstract class SCMessageCallback implements ISCMessageCallback {

	/** The service which is using the message callback. */
	private IService service;

	/**
	 * Instantiates a new SCMessageCallback.
	 * 
	 * @param service
	 *            the service
	 */
	public SCMessageCallback(IService service) {
		this.service = service;
	}
	
	/** {@inheritDoc} */
	@Override
	public abstract void callback(ISCMessage reply);

	/** {@inheritDoc} */
	@Override
	public abstract void callback(Exception ex);

	/** {@inheritDoc} */
	@Override
	public IService getService() {
		return service;
	}
}