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
package org.serviceconnector.api.srv;

import org.serviceconnector.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SrvServiceRegistry. Registry of services on backend server. Gives access to registered services and their callback.
 * 
 * @author JTraber
 */
public class SrvServiceRegistry extends Registry<String, SrvService> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SrvServiceRegistry.class);

	/**
	 * Adds the server service.
	 * 
	 * @param key
	 *            the key
	 * @param srvService
	 *            the server service
	 */
	public void addSrvService(String key, SrvService srvService) {
		super.put(key, srvService);
	}

	/**
	 * Gets the server service.
	 * 
	 * @param key
	 *            the server service name
	 * @return the server service
	 */
	public SrvService getSrvService(String key) {
		return this.get(key);
	}

	/**
	 * Removes the server service.
	 * 
	 * @param key
	 *            the key
	 * @return the srv service
	 */
	public SrvService removeSrvService(String key) {
		LOGGER.debug("remove SrvService " + key);
		return super.remove(key);
	}
}
