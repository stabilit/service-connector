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
package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.net.ICommunicationPoint;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Interface IConnection abstracts any connection to a responder.
 * 
 * @author JTraber
 */
public interface IConnection extends ICommunicationPoint {

	/**
	 * Connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	void connect() throws Exception;

	/**
	 * Send and receive synchronous operation.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the scmp
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception;

	/**
	 * Send and receive asynchronous operation.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the scmp
	 * @throws Exception
	 *             the exception
	 */
	public void send(SCMPMessage scmp, ISCMPCallback callback) throws Exception;

	/**
	 * Disconnect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void disconnect() throws Exception;

	/**
	 * Destroys connection.
	 */
	public void destroy();

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	boolean isConnected();

	/**
	 * Sets the idle timeout.
	 * 
	 * @param idleTimeout
	 *            the new idle timeout
	 */
	void setIdleTimeout(int idleTimeout);
	
	public void incrementNrOfIdles();
	
	public void resetNrOfIdles();
	
	public int getNrOfIdlesInSequence();
}
