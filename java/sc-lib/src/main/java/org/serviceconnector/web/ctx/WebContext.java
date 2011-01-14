/*
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
 */
package org.serviceconnector.web.ctx;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.WebConfiguration;
import org.serviceconnector.web.cmd.FlyweightWebCommandFactory;
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.cmd.sc.DefaultXMLLoaderFactory;


/**
 * The Class WebContext.
 */
public class WebContext {

	// initialize configurations in every case
	static {
		WebContext.webConfiguration = new WebConfiguration();
	}

	/** The instance. */
	protected static WebContext instance = new WebContext();

	// Factories
	/** The loader factory. */
	private static DefaultXMLLoaderFactory loaderFactory = new DefaultXMLLoaderFactory();

	/** The web command factory. */
	private static FlyweightWebCommandFactory webCommandFactory;

	/** The composite configuration. */
	private static CompositeConfiguration apacheCompositeConfig;
	
	/** The web configuration. */
	private static WebConfiguration webConfiguration = new WebConfiguration();

	/**
	 * Instantiates a new web context.
	 */
	public WebContext() {
	}

	public static void initConfiguration(String configFile) throws Exception {
		WebContext.apacheCompositeConfig = new CompositeConfiguration();
		// system properties override every setting
		try {
			// add environment variables to configuration
			WebContext.apacheCompositeConfig.addConfiguration(new EnvironmentConfiguration());
			WebContext.apacheCompositeConfig.addConfiguration(new PropertiesConfiguration(configFile));
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.toString());
		}

		WebContext.webConfiguration.init(WebContext.apacheCompositeConfig);
	}

	/**
	 * Inits the context.
	 * 
	 * @param webCommandFactory
	 *            the web command factory
	 */
	public static void initContext(FlyweightWebCommandFactory webCommandFactory) {
		if (WebContext.webCommandFactory != null) {
			// set only one time
			return;
		}
		WebContext.webCommandFactory = webCommandFactory;
	}

	/**
	 * Gets the web configuration.
	 *
	 * @return the web configuration
	 */
	public static WebConfiguration getWebConfiguration() {
		return webConfiguration;
	}

	/**
	 * Gets the web command.
	 * 
	 * @param webRequest
	 *            the web request
	 * @return the web command
	 */
	public static IWebCommand getWebCommand(IWebRequest webRequest) {
		return WebContext.webCommandFactory.getWebCommand(webRequest);
	}

	/**
	 * Gets the xML loader.
	 * 
	 * @param key
	 *            the key
	 * @return the xML loader
	 */
	public static IXMLLoader getXMLLoader(String url) {
		return WebContext.loaderFactory.getXMLLoader(url);
	}
}
