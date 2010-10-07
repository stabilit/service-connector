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

import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Interface ICommand.
 */
public interface ICommand extends IFactoryable {

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
	 * @throws Exception
	 *             the exception
	 */
	public void run(IRequest request, IResponse response) throws Exception;

	
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
	 * Checks if command is asynchronous.
	 * 
	 * @return true, if command is asynchronous
	 */
	public boolean isAsynchronous();
	
	/**
	 * Checks if command passes through message parts
	 * 
	 * @return
	 */
	public boolean isPassThroughPartMsg();
}
