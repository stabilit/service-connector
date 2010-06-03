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
package com.stabilit.scm.cln.client;

/**
 * The Interface IClientSession. Represents a virtual link between client and server. API programmer needs to
 * manage several client sessions on his own. Necessary to make session calls like SCMPClnDataCall.
 * 
 * @author JTraber
 */
public interface IClientSession {

	/**
	 * Gets the session id.
	 * 
	 * @return the sessionId
	 */
	public abstract String getSessionId();

	/**
	 * Gets the service name.
	 * 
	 * @return the serviceName
	 */
	public abstract String getServiceName();

	/**
	 * Gets the session info.
	 * 
	 * @return the sessionInfo
	 */
	public abstract String getSessionInfo();

}