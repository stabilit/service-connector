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
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.cmd.WebCommandFactory;


/**
 * A factory for creating ServiceConnectorWebCommand objects. Provides access to concrete instances of Service Connector Web
 * commands.
 * 
 * @author JTraber
 */
public class ServiceConnectorWebCommandFactory extends WebCommandFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceConnectorWebCommandFactory.class);
	
	/**
	 * Instantiates a new service connector command factory.
	 */
	public ServiceConnectorWebCommandFactory() {
		init(this);
	}

	/**
	 * Instantiates a new service connector web command factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public ServiceConnectorWebCommandFactory(WebCommandFactory webCommandFactory) {
		init(webCommandFactory);
	}

	/**
	 * Initialize the web command factory.
	 * 
	 * @param commandFactory
	 *            the web command factory
	 */
	private void init(WebCommandFactory webCommandFactory) {
		IWebCommand defaultWebCommand = new DefaultWebCommand();
		webCommandFactory.addWebCommand(defaultWebCommand.getKey(), defaultWebCommand);
	}
}
