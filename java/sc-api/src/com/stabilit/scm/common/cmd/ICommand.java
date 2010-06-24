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
package com.stabilit.scm.common.cmd;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMsgType;

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
	 * Gets the command validator.
	 * 
	 * @return the command validator
	 */
	public ICommandValidator getCommandValidator();

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
	 * Checks if is asynchronous.
	 *
	 * @return true, if is asynchronous
	 */
	public boolean isAsynchronous();
}
