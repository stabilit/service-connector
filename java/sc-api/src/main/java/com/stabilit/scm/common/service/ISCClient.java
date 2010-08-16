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

import com.stabilit.scm.cln.service.ISessionService;

/**
 * The Interface IServiceConnector.
 * 
 * @author JTraber
 */
public interface ISCClient extends ISC {

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext();

	/**
	 * Connects to SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void attach() throws Exception;

	/**
	 * Disconnects from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void detach() throws Exception;

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost();

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort();

	/**
	 * Creates a new file service.
	 * 
	 * @param serviceName
	 *            the service name of the file service to use
	 * @return the file service
	 */
	public IFileService newFileService(String serviceName);

	/**
	 * Creates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name of the publish service to use
	 * @return the publish service
	 */
	public IPublishService newPublishService(String serviceName);

	/**
	 * Creates a new session service.
	 * 
	 * @param serviceName
	 *            the service name of the session service to use
	 * @return the session service
	 */
	public ISessionService newSessionService(String serviceName);

	/**
	 * Sets the max connections.
	 * 
	 * @param maxConnections
	 *            the new max connections
	 */
	public void setMaxConnections(int maxConnections);

	/** {@inheritDoc} */
	@Override
	public String getConnectionType();
}
