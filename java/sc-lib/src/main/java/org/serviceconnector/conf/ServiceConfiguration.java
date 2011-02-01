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

import org.apache.commons.configuration.CompositeConfiguration;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;

public class ServiceConfiguration {

	/** The type. */
	private String type;
	/** The node name. */
	private String name;
	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The connectionType. */
	private String connectionType;
	/** The max pool size. */
	private int maxPoolSize;
	/** The keep alive interval. */
	private int keepAliveIntervalSeconds;
	/** The maxSessions (for file servers). */
	private int maxSessions;

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the node name
	 */
	public ServiceConfiguration(String name) {
	}
	
	/**
	 * Load the configurated items
	 * 
	 * @param compositeConfig
	 * @throws SCMPValidatorException
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {
	}
	
}
