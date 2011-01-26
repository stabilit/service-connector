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
package org.serviceconnector.net.req;

import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Interface IRequester abstracts requester functionality.
 * 
 * @author JTraber
 */
public interface IRequester {

	/**
	 * Send and receive response asynchronous.
	 * 
	 * @param scmp
	 *            the scmp
	 * @param timeoutInMillis
	 *            the timeout in seconds
	 * @param callback
	 *            the callback
	 * @throws Exception
	 *             exception in sending/receiving process
	 */
	public void send(SCMPMessage scmp, int timeoutInMillis, ISCMPMessageCallback callback) throws Exception;

	/**
	 * Destroy.
	 */
	public void destroy();

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public RequesterContext getContext();
}
