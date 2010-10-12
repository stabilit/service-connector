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
package org.serviceconnector.web.cmd;

import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;


/**
 * The Interface ICommand.
 */
public interface IWebCommand extends IFactoryable {

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public String getKey();

	/**
	 * Gets the command validator.
	 * 
	 * @return the command validator
	 */
	public IWebCommandValidator getCommandValidator();
	
	/**
	 * Sets the command validator.
	 *
	 * @param commandValidator the new command validator
	 */
	public void setCommandValidator(IWebCommandValidator commandValidator);

	/**
	 * Gets the command accessible.
	 *
	 * @return the command accessible
	 */
	public IWebCommandAccessible getCommandAccessible();
	
	/**
	 * Sets the command accessible.
	 *
	 * @param commandAccessible the new command accessible
	 */
	public void setCommandAccessible(IWebCommandAccessible commandAccessible);
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
	public void run(IWebRequest request, IWebResponse response) throws Exception;

	/**
	 * Checks if command is asynchronous.
	 * 
	 * @return true, if command is asynchronous
	 */
	public boolean isAsynchronous();
}
