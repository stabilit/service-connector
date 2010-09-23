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

import org.apache.log4j.Logger;
import org.serviceconnector.factory.Factory;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.web.IWebRequest;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Command objects.
 */
public abstract class WebCommandFactory extends Factory {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(WebCommandFactory.class);
	
	/** The command factory. */
	protected static WebCommandFactory webCommandFactory = null;

	/**
	 * Instantiates a new command factory.
	 */
	public WebCommandFactory() {
	}

	/**
	 * Gets the current command factory.
	 * 
	 * @return the current command factory
	 */
	public static WebCommandFactory getCurrentWebCommandFactory() {
		return webCommandFactory;
	}

	/**
	 * Sets the current command factory.
	 *
	 * @param webCommandFactory the new current command factory
	 */
	public static void setCurrentWebCommandFactory(WebCommandFactory webCommandFactory) {
		WebCommandFactory.webCommandFactory = webCommandFactory;
	}

	
	/**
	 * Adds the web command.
	 *
	 * @param key the key
	 * @param webCommand the web command
	 */
	public void addWebCommand(String key, IWebCommand webCommand) {
		this.add(key, webCommand);		
	}

	
	/**
	 * Gets the web command.
	 *
	 * @param webRequest the web request
	 * @return the web command
	 */
	public IWebCommand getWebCommand(IWebRequest webRequest) {
		IFactoryable factoryInstance = this.newInstance("default");
		return (IWebCommand) factoryInstance;
	}

}
