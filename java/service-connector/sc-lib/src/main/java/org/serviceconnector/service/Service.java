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

import org.apache.log4j.Logger;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class Service.
 * 
 * @author JTraber
 */
public abstract class Service {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Service.class);

	/** The type. */
	ServiceType type;
	/** The state. */
	boolean enabled;
	/** The name. */
	String name;

	/**
	 * Instantiates a new service.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public Service(String name, ServiceType type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public ServiceType getType() {
		return type;
	}

	/**
	 * Gets the current service state.
	 * 
	 * @return the state
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 * 
	 * @param enabled
	 *            the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return this.name + ":" + this.enabled + ":" + this.type.getValue();
	}

	/**
	 * Dump the service into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public abstract void dump(XMLDumpWriter writer) throws Exception;
}
