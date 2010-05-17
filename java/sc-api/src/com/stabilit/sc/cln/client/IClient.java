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
package com.stabilit.sc.cln.client;

import com.stabilit.sc.cln.config.IClientConfigItem;
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Interface IClient abstracts client functionality.
 * 
 * @author JTraber
 */
public interface IClient extends IFactoryable {

	/**
	 * Disconnect.
	 * 
	 * @throws Exception the exception
	 */
	public void disconnect() throws Exception;

	/**
	 * Destroy.
	 * 
	 * @throws Exception the exception
	 */
	public void destroy() throws Exception;

	/**
	 * Connect.
	 * 
	 * @throws Exception the exception
	 */
	public void connect() throws Exception;

	/**
	 * Send and receive, synchronous operation.
	 * 
	 * @param scmp the scmp
	 * 
	 * @return the scmp
	 * 
	 * @throws Exception exception in sending/receiving process
	 */
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception;


	/**
	 * Sets the client config.
	 * 
	 * @param clientConfig the new client config
	 */
	public void setClientConfig(IClientConfigItem clientConfig);

	/**
	 * Gets the client session.
	 * 
	 * @return the client session
	 */
	public IClientSession getClientSession();
	
	/**
	 * Sets the client session.
	 * 
	 * @param clientSession the new client session
	 */
	public void setClientSession(IClientSession clientSession);
	
	/**
	 * Returns a hash code which identifies client connection.
	 * 
	 * @return the string
	 */
	public String toHashCodeString();

}
