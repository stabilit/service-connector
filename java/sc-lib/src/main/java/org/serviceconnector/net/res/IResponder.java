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
package org.serviceconnector.net.res;

import org.serviceconnector.conf.CommunicatorConfig;

/**
 * The Interface IRequester.
 * 
 * @author JTraber
 */
public interface IResponder {

	/**
	 * Creates the responder.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void create() throws Exception;

	/**
	 * Start listen asynchronous. Starts responder in a separate thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void startListenAsync() throws Exception;

	/**
	 * Start listener synchronous. Starts responder in current thread. Does not give back control.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void startListenSync() throws Exception;
	
	/**
	 * Stop listening.
	 */
	public void stopListening();

	/**
	 * Destroys responder.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void destroy();

	/**
	 * Sets the responder configuration.
	 * 
	 * @param respConfig
	 *            the new responder configuration
	 */
	public void setResponderConfig(CommunicatorConfig respConfig);

	/**
	 * Gets the responder configuration.
	 * 
	 * @return the responder configuration
	 */
	public CommunicatorConfig getResponderConfig();
	
	/**
	 * Gets the endpoint.
	 *
	 * @return the endpoint
	 */
	public IEndpoint getEndpoint();

}
