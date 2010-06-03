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

package com.stabilit.scm.cln.call;

import com.stabilit.scm.cln.req.IClientSession;
import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Interface ISCMPCall. Basic functionality of a SCMPCall.
 * 
 * @author JTraber
 */
public interface ISCMPCall {

	/**
	 * New instance of a call.
	 * 
	 * @param client
	 *            the client to use for the call
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IRequester client);

	/**
	 * New instance.
	 * 
	 * @param client
	 *            the client to use for the call
	 * @param clientSession
	 *            the client session
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IRequester client, IClientSession clientSession);

	/**
	 * New instance.
	 * 
	 * @param client
	 *            the client
	 * @param scmpMessage
	 *            the scmp message
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IRequester client, SCMPMessage scmpMessage);

	/**
	 * Invoke.
	 * 
	 * @return the scmp message
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage invoke() throws Exception;

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
	 * @return the result
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage closeGroup() throws Exception;
}
