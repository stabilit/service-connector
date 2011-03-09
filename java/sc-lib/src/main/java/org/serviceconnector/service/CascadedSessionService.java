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
package org.serviceconnector.service;

import org.serviceconnector.server.CascadedSC;

/**
 * The Class CascadedSessionService.
 */
public class CascadedSessionService extends Service {

	/** The cascaded sc. */
	protected CascadedSC cascadedSC;

	/**
	 * Instantiates a new cascaded session service.
	 * 
	 * @param name
	 *            the name
	 * @param cascadedSC
	 *            the cascaded sc
	 */
	public CascadedSessionService(String name, CascadedSC cascadedSC) {
		super(name, ServiceType.CASCADED_SESSION_SERVICE);
		this.cascadedSC = cascadedSC;
	}

	/**
	 * Sets the cascaded sc.
	 * 
	 * @param cascadedSC
	 *            the new cascaded sc
	 */
	public void setCascadedSC(CascadedSC cascadedSC) {
		this.cascadedSC = cascadedSC;
	}

	/**
	 * Gets the cascaded sc.
	 * 
	 * @return the cascaded sc
	 */
	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}
}
