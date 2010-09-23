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
import org.serviceconnector.web.cmd.IWebCommandValidator;
import org.serviceconnector.web.cmd.NullWebCommandValidator;


/**
 * The Class CommandAdapter. Adapter for every kind of command. Provides basic functions that is used by executions of
 * commands.
 * 
 * @author JTraber
 */
public abstract class WebCommandAdapter implements IWebCommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(WebCommandAdapter.class);
	
	/** The web command validator. */
	protected IWebCommandValidator webCommandValidator;

	/**
	 * Instantiates a new web command adapter.
	 */
	public WebCommandAdapter() {
		this.webCommandValidator = NullWebCommandValidator.newInstance(); // www.refactoring.com Introduce NULL Object
	}

	/** {@inheritDoc} */
	@Override
	public void run(IWebRequest request, IWebResponse response) throws Exception {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public IWebCommandValidator getCommandValidator() {
		return webCommandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public abstract String getKey();
}
