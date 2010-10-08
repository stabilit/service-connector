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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.serviceconnector.web.IWebRequest;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Command objects.
 */
public abstract class WebCommandFactory {

	/** The Constant logger. */
	protected static final Logger logger = Logger
			.getLogger(WebCommandFactory.class);

	/** The command factory. */
	protected static WebCommandFactory webCommandFactory = null;

	/** The web command map. */
	private Map<String, IWebCommand> webCommandMap = new ConcurrentHashMap<String, IWebCommand>();

	/**
	 * Instantiates a new command factory.
	 */
	public WebCommandFactory() {
	}

	/**
	 * Adds the web command.
	 * 
	 * @param key
	 *            the key
	 * @param webCommand
	 *            the web command
	 */
	public void addWebCommand(String key, IWebCommand webCommand) {
		this.webCommandMap.put(key, webCommand);
	}

	/**
	 * Gets the web command.
	 * 
	 * @param webRequest
	 *            the web request
	 * @return the web command
	 */
	public IWebCommand getWebCommand(IWebRequest webRequest) {
		return this.webCommandMap.get("default");
	}

}
