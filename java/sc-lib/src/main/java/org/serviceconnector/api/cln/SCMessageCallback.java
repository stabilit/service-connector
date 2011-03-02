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
import org.serviceconnector.api.SCMessage;

/**
 * The Class SCMessageCallback. Abstract class provides basic functions for a message callback.
 * 
 * @author JTraber
 */
public abstract class SCMessageCallback {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(SCMessageCallback.class);

	/** The service which is using the message callback. */
	protected SCService service;

	/**
	 * Instantiates a new SCMessageCallback.
	 * 
	 * @param service
	 *            the service
	 */
	public SCMessageCallback(SCService service) {
		this.service = service;
	}

	/**
	 * Callback. Method gets called when reply arrives.
	 * 
	 * @param reply
	 *            the reply
	 */
	public abstract void receive(SCMessage reply);

	/**
	 * Callback. Method gets called when an error shows up in communication process.
	 * 
	 * @param ex
	 *            the exception
	 */
	public abstract void receive(Exception ex);

	/**
	 * Gets the service which is using the message callback.
	 * 
	 * @return the service
	 */
	public SCService getService() {
		return service;
	}
}