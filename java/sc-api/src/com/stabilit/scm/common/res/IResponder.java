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

import com.stabilit.scm.common.conf.ICommunicatorConfig;

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
	 * Run asynchronously. Starts responder in another thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void runAsync() throws Exception;

	/**
	 * Run sync. Starts responder in incoming thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void runSync() throws Exception;

	/**
	 * Destroys responder.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void destroy() throws Exception;

	/**
	 * Sets the responder configuration.
	 * 
	 * @param respConfig
	 *            the new responder configuration
	 */
	public void setResponderConfig(ICommunicatorConfig respConfig);

	/**
	 * Gets the responder configuration.
	 * 
	 * @return the responder configuration
	 */
	public ICommunicatorConfig getResponderConfig();
}
