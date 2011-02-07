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
package org.serviceconnector.conf;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class WebConfiguration.
 */
public class WebConfiguration {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(WebConfiguration.class);

	/**
	 * The translet enabled flag.
	 * 
	 * If this flag is true then all xsl transformations will be cached internal otherwise not.
	 * 
	 */
	private boolean xslTransformationCacheEnabled;

	/**
	 * Page header prefix is displayed in the page header and page title in front of the
	 * standard text. The property WEB_PAGE_HEADER_PREFIX allows to define a custom value.
	 */
	private String pageHeaderPrefix;

	/**
	 * Name of the service used by GUI to upload log files
	 */
	private String scUploadService;
	
	/**
	 * Name of the service used by GUI to download configuration files
	 */
	private String scDownloadService;
	
	/**
	 * Instantiates a new SCMP cache configuration.
	 */
	public WebConfiguration() {
		this.xslTransformationCacheEnabled = Constants.DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED;
		this.scDownloadService = null;
		this.scUploadService = null;
		this.pageHeaderPrefix = null;
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
	public synchronized void load(CompositeConfiguration compositeConfiguration) throws Exception {
		Boolean xslTransformationCacheEnabled = compositeConfiguration.getBoolean(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED,
				null);
		if (xslTransformationCacheEnabled != null) {
			this.xslTransformationCacheEnabled = xslTransformationCacheEnabled;
		}
		logger.info(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED + "=" + this.xslTransformationCacheEnabled);

		this.pageHeaderPrefix = compositeConfiguration.getString(Constants.WEB_PAGE_HEADER_PREFIX, "");
		logger.info(Constants.WEB_PAGE_HEADER_PREFIX + "=" + this.pageHeaderPrefix);
		
		this.scDownloadService = compositeConfiguration.getString(Constants.WEB_SC_DOWNLOAD_SERVICE, null);
		logger.info(Constants.WEB_SC_DOWNLOAD_SERVICE + "=" + this.scDownloadService);
		if (AppContext.getServiceRegistry().getService(this.scDownloadService) == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, 
				Constants.WEB_SC_DOWNLOAD_SERVICE + "="+ this.scDownloadService + " service not found");
		}
		
		this.scUploadService = compositeConfiguration.getString(Constants.WEB_SC_UPLOAD_SERVICE, null);
		logger.info(Constants.WEB_SC_UPLOAD_SERVICE + "=" + this.scUploadService);
		if (AppContext.getServiceRegistry().getService(this.scDownloadService) == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, 
				Constants.WEB_SC_UPLOAD_SERVICE + "="+ this.scUploadService + " service not found");
		}
	}

	/**
	 * Checks if is translet enabled.
	 * 
	 * @return true, if is translet enabled
	 */
	public boolean isTransletEnabled() {
		return xslTransformationCacheEnabled;
	}

	/**
	 * Gets the page header prefix.
	 *
	 * @return the page header prefix
	 */
	public String getPageHeaderPrefix() {
		return pageHeaderPrefix;
	}
	
	/**
	 * Gets the sc download service.
	 *
	 * @return the sc download service
	 */
	public String getScDownloadService() {
		return scDownloadService;
	}
	
	/**
	 * Gets the sc upload service.
	 *
	 * @return the sc upload service
	 */
	public String getScUploadService() {
		return scUploadService;
	}
}
