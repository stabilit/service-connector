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
package org.serviceconnector.cmd;

import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Interface ICommand.
 */
public interface ICommand {

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public SCMPMsgType getKey();

	/**
	 * Run command.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            callback to the responder
	 * @throws Exception
	 *             the exception
	 */
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception;

	/**
	 * Validate request.
	 * 
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	public void validate(IRequest request) throws Exception;

	/**
	 * Checks if command passes through message parts.
	 *
	 * @return true, if is pass through part msg
	 */
	public boolean isPassThroughPartMsg();
}
