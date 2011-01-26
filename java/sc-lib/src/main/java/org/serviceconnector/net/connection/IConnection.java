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
package org.serviceconnector.net.connection;

import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Interface IConnection abstracts any connection to a responder.
 * 
 * @author JTraber
 */
public interface IConnection {

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public abstract ConnectionContext getContext();

	/**
	 * Sets the context.
	 * 
	 * @param connectionContext
	 *            the new context
	 */
	public abstract void setContext(ConnectionContext connectionContext);

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the host
	 */

	public void setHost(String host);

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the port
	 */
	public void setPort(int port);

	/**
	 * Connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void connect() throws Exception;

	/**
	 * Send and receive asynchronous operation.
	 * 
	 * @param scmp
	 *            the scmp
	 * @param callback
	 *            the callback
	 * @throws Exception
	 *             the exception
	 */
	public abstract void send(SCMPMessage scmp, ISCMPMessageCallback callback) throws Exception;

	/**
	 * Disconnect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void disconnect() throws Exception;

	/**
	 * Destroys connection.
	 */
	public abstract void destroy();

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	public abstract boolean isConnected();

	/**
	 * Sets the idle timeout.
	 * 
	 * @param idleTimeoutSeconds
	 *            the new idle timeout
	 */
	public abstract void setIdleTimeoutSeconds(int idleTimeoutSeconds);

	/**
	 * Increment nr of idles.
	 */
	public abstract void incrementNrOfIdles();

	/**
	 * Reset nr of idles.
	 */
	public abstract void resetNrOfIdles();

	/**
	 * Gets the nr of idles in sequence.
	 * 
	 * @return the nr of idles in sequence
	 */
	public abstract int getNrOfIdlesInSequence();
}
