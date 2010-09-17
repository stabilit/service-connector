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
package org.serviceconnector.cln.service;

import org.serviceconnector.service.ISCMessage;


/**
 * The Interface ISCSession. Reveals functionality of a sc session.
 * 
 * @author JTraber
 */
public interface ISCSession {

	/**
	 * Executes the session.
	 * 
	 * @param message
	 *            the data body to execute
	 * @return the message
	 * @throws Exception
	 *             the exception
	 */
	public ISCMessage execute(ISCMessage message) throws Exception;

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteSession() throws Exception;

	/**
	 * Close group.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void closeGroup() throws Exception;

	/**
	 * Open group.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void openGroup() throws Exception;

	/**
	 * Creates the session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void createSession() throws Exception;

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId();

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName();

	/**
	 * Sets the message info.
	 * 
	 * @param string
	 *            the new message info
	 */
	public void setMessageInfo(String string);

	/**
	 * Sets the session info.
	 * 
	 * @param string
	 *            the new session info
	 */
	public void setSessionInfo(String string);
}
