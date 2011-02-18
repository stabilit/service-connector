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
package org.serviceconnector.web.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.cmd.IWebCommandAccessible;
import org.serviceconnector.web.cmd.IWebCommandValidator;
import org.serviceconnector.web.cmd.NullWebCommandAccessible;
import org.serviceconnector.web.cmd.NullWebCommandValidator;

/**
 * The Class CommandAdapter. Adapter for every kind of command. Provides basic
 * functions that is used by executions of commands.
 * 
 * @author JTraber
 */
public abstract class WebCommandAdapter implements IWebCommand {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(WebCommandAdapter.class);

	/** The web command accessible. */
	protected IWebCommandAccessible webCommandAccessible;
	/** The web command validator. */
	protected IWebCommandValidator webCommandValidator;

	/**
	 * Instantiates a new web command adapter.
	 */
	public WebCommandAdapter() {
		this.webCommandAccessible = NullWebCommandAccessible.newInstance(); // www.refactoring.com
		this.webCommandValidator = NullWebCommandValidator.newInstance(); // www.refactoring.com
		// Introduce
		// NULL
		// Object
	}

	/**
	 * Run.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public void run(IWebRequest request, IWebResponse response) throws Exception {
		throw new UnsupportedOperationException("not allowed");
	}

	/**
	 * Gets the command accessible.
	 * 
	 * @return the command accessible {@inheritDoc}
	 */
	@Override
	public IWebCommandAccessible getCommandAccessible() {
		return webCommandAccessible;
	}

	/**
	 * Sets the command accessible.
	 * 
	 * @param webCommandAccessible
	 *            the new command accessible {@inheritDoc}
	 */
	@Override
	public void setCommandAccessible(IWebCommandAccessible webCommandAccessible) {
		this.webCommandAccessible = webCommandAccessible;
	}

	/**
	 * Gets the command validator.
	 * 
	 * @return the command validator {@inheritDoc}
	 */
	@Override
	public IWebCommandValidator getCommandValidator() {
		return webCommandValidator;
	}

	/**
	 * Sets the command validator.
	 * 
	 * @param webCommandValidator
	 *            the new command validator {@inheritDoc}
	 */
	@Override
	public void setCommandValidator(IWebCommandValidator webCommandValidator) {
		this.webCommandValidator = webCommandValidator;
	}

	/**
	 * New instance.
	 * 
	 * @return the i factoryable {@inheritDoc}
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/**
	 * Checks if is asynchronous.
	 * 
	 * @return true, if is asynchronous {@inheritDoc}
	 */
	@Override
	public boolean isAsynchronous() {
		return false;
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key {@inheritDoc}
	 */
	@Override
	public abstract String getKey();
}
