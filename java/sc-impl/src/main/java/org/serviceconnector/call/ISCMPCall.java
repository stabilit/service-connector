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

package org.serviceconnector.call;

import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Interface ISCMPCall. Basic functionality of a SCMPCall.
 * 
 * @author JTraber
 */
public interface ISCMPCall {

	/**
	 * New instance.
	 * 
	 * @param requester
	 *            the requester
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IRequester requester);

	/**
	 * New instance of a call.
	 * 
	 * @param requester
	 *            the requester to use for the call
	 * @param serviceName
	 *            the service name
	 * @return the scmp call
	 */
	public ISCMPCall newInstance(IRequester requester, String serviceName);

	/**
	 * New instance.
	 * 
	 * @param requester
	 *            the requester
	 * @param sessionId
	 *            the session id
	 * @param serviceName
	 *            the service name
	 * @return the scmp call
	 */
	public ISCMPCall newInstance(IRequester requester, String serviceName, String sessionId);

	/**
	 * New instance. Is used on SC because received message is used to create a forward call.
	 * 
	 * @param requester
	 *            the requester
	 * @param receivedMessage
	 *            the scmp message
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IRequester requester, SCMPMessage receivedMessage);

	/**
	 * Invoke asynchronous.
	 * 
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout in milliseconds
	 * @return the scmp message
	 * @throws Exception
	 *             the exception
	 */
	public void invoke(ISCMPCallback callback, double timeoutMillis) throws Exception;

	/**
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setRequestBody(Object body);

	/**
	 * Gets the call.
	 * 
	 * @return the call
	 */
	public SCMPMessage getRequest();

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public SCMPMessage getResponse();

	/**
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	public SCMPMsgType getMessageType();

	/**
	 * Open group.
	 * 
	 * @return the iSCMP call
	 * @throws Exception
	 *             the exception
	 */
	public ISCMPCall openGroup() throws Exception;

	/**
	 * Close group, sends the ending request.
	 * 
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout in milliseconds
	 * @return the result
	 * @throws Exception
	 *             the exception
	 */
	public void closeGroup(ISCMPCallback callback, double timeoutMillis) throws Exception;
}
