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
package org.serviceconnector.web.ctx;

import org.serviceconnector.conf.WebConfiguration;
import org.serviceconnector.web.WebCredentials;
import org.serviceconnector.web.WebSessionRegistry;
import org.serviceconnector.web.cmd.WebCommand;
import org.serviceconnector.web.cmd.XMLLoaderFactory;
import org.serviceconnector.web.xml.IXMLLoader;

/**
 * The Class WebContext.
 */
public final class WebContext {
	/** The web configuration. */
	private static WebConfiguration webConfiguration = new WebConfiguration();
	/** The web command. */
	private static WebCommand webCommand = new WebCommand();
	private static WebCredentials webContextCredentials;
	private static WebSessionRegistry webSessionRegistry;

	/**
	 * Instantiates a new web context. Singelton.
	 */
	private WebContext() {
	}

	// initialize configurations in every case
	static {
		WebContext.webConfiguration = new WebConfiguration();
		WebContext.webSessionRegistry = new WebSessionRegistry();
	}

	/**
	 * Gets the web configuration.
	 *
	 * @return the web configuration
	 */
	public static WebConfiguration getWebConfiguration() {
		return webConfiguration;
	}

	public static WebSessionRegistry getWebSessionRegistry() {
		return WebContext.webSessionRegistry;
	}

	/**
	 * Gets the web command.
	 *
	 * @param webRequest the web request
	 * @return the web command
	 */
	public static WebCommand getWebCommand() {
		return WebContext.webCommand;
	}

	/**
	 * Gets the XML loader.
	 *
	 * @param url the url
	 * @return the xML loader
	 */
	public static IXMLLoader getXMLLoader(String url) {
		return XMLLoaderFactory.getXMLLoader(url);
	}

	/**
	 * Gets the web context credentials.
	 *
	 * @return the web context credentials
	 */
	public static WebCredentials getWebSCContextCredentials() {
		return WebContext.webContextCredentials;
	}

	/**
	 * Sets the SC web credentials.
	 *
	 * @param webCredentials the new SC web credentials
	 */
	public static void setSCWebCredentials(WebCredentials webCredentials) {
		WebContext.webContextCredentials = webCredentials;
	}
}
