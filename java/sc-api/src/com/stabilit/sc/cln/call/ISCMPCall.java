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
package com.stabilit.sc.cln.call;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Interface ISCMPCall abstracts SCMPCalls.
 * 
 * @author JTraber
 */
public interface ISCMPCall {

	/**
	 * New instance of a call.
	 * 
	 * @param client
	 *            the client to use for the call *
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IClient client);

	/**
	 * New instance of a call.
	 * 
	 * @param client
	 *            the client to use for the call
	 * @param scmpSession
	 *            the scmp session to use for the call
	 * @return the iSCMP call
	 */
	public ISCMPCall newInstance(IClient client, SCMP scmpSession);

	/**
	 * Invoke.
	 * 
	 * @return the scmps
	 * @throws Exception
	 *             the exception
	 */
	public SCMP invoke() throws Exception;

	/**
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setBody(Object body);

	/**
	 * Gets the call.
	 * 
	 * @return the call
	 */
	public SCMP getCall();

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public SCMP getResult();

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
	 * @return the scmp result
	 * @throws Exception
	 *             the exception
	 */
	public SCMP closeGroup() throws Exception;
}
