/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.service;

import org.apache.log4j.Logger;

/**
 * The Class Service.
 * 
 * @author JTraber
 */
public abstract class Service {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(Service.class);

	/** The type. */
	private ServiceType type;
	/** The state. */
	private ServiceState state;
	/** The name. */
	private String name;
	/** The location. */
	private String location;

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
		this.location = null;
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
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 * 
	 * @param location
	 *            the new location
	 */
	public void setLocation(String location) {
		this.location = location;
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
	 * Gets the current service state
	 * 
	 * @return
	 */
	public ServiceState getState() {
		return state;
	}

	/**
	 * @param state
	 */
	public void setState(ServiceState state) {
		this.state = state;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.name + ":" + this.state + ":" + this.type.getValue();
	}
}
