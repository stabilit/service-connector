/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.cln.req;

import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.scmp.SCMPMessage;

/**
 * The Interface IRequester abstracts requester functionality.
 * 
 * @author JTraber
 */
public interface IRequester extends IFactoryable {

	/**
	 * Disconnect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void disconnect() throws Exception;

	/**
	 * Destroy.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void destroy() throws Exception;

	/**
	 * Connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void connect() throws Exception;

	/**
	 * Send and receive, synchronous operation.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the scmp
	 * @throws Exception
	 *             exception in sending/receiving process
	 */
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception;

	/**
	 * Sets the requester config.
	 * 
	 * @param requesterConfig
	 *            the new requester config
	 */
	public void setRequesterConfig(IRequesterConfigItem requesterConfig);

	/**
	 * Returns a hash code which identifies client connection.
	 * 
	 * @return the string
	 */
	public String toHashCodeString();

}
