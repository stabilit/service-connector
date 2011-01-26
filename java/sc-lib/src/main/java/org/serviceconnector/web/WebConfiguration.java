/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.web;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class WebConfiguration.
 */
public class WebConfiguration {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(WebConfiguration.class);

	/**
	 * The translet enabled flag.
	 * 
	 * If this flag is true then all xsl transformations will be cached internal otherwise not.
	 * 
	 */
	private boolean xslTransformationCacheEnabled;

	/**
	 * Instantiates a new SCMP cache configuration.
	 */
	public WebConfiguration() {
		this.xslTransformationCacheEnabled = Constants.DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED;
	}

	/**
	 * Loads web parameters from properties file.</br> Service Connector web parameters: </br>
	 * web.xslTransformationCache.enabled=true</br>
	 * 
	 * @param compositeConfiguration
	 *            the composite configuration
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void init(CompositeConfiguration compositeConfiguration) throws Exception {
		Boolean xslTransformationCacheEnabled = compositeConfiguration.getBoolean(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED,
				null);
		if (xslTransformationCacheEnabled != null) {
			this.xslTransformationCacheEnabled = xslTransformationCacheEnabled;
		}
		logger.info(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED + "=" + this.xslTransformationCacheEnabled);
	}

	/**
	 * Checks if is translet enabled.
	 * 
	 * @return true, if is translet enabled
	 */
	public boolean isTransletEnabled() {
		return xslTransformationCacheEnabled;
	}

}
