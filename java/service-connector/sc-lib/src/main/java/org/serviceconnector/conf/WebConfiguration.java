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

import org.apache.commons.configuration2.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class WebConfiguration.
 */
public class WebConfiguration {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfiguration.class);

	/**
	 * The translet enabled flag. If this flag is true then all xsl transformations will be cached internal otherwise not.
	 */
	private boolean xslTransformationCacheEnabled;

	/**
	 * Page header prefix is displayed in the page header and page title in front of the standard text. The property WEB_PAGE_HEADER_PREFIX allows to define a custom value.
	 */
	private String pageHeaderPrefix;

	/**
	 * Name of the service used by GUI to upload log files.
	 */
	private String scUploadService;

	/**
	 * Name of the service used by GUI to download configuration files.
	 */
	private String scDownloadService;

	/** The web session schedule timeout seconds. Defines the interval checking for any invalid web sessions. */
	private int webSessionScheduleTimeoutSeconds;

	/** The web session timeout minutes. Defines how long a session is valid after inactivity. */
	private int webSessionTimeoutMinutes;

	/** if true the GUI will display Sc terminate button in maintenance menu and allow Sc terminationn from the GUI. */
	private boolean scTerminateAllowed;

	/** name of the colorScheme. */
	private String colorScheme;

	/**
	 * Instantiates a new SCMP cache configuration.
	 */
	public WebConfiguration() {
		this.xslTransformationCacheEnabled = Constants.DEFAULT_WEB_XSL_TRANSFORMATION_CACHE_ENABLED;
		this.webSessionScheduleTimeoutSeconds = Constants.DEFAULT_WEB_SESSION_SCHEDULE_TIMEOUT_SECONDS;
		this.webSessionTimeoutMinutes = Constants.DEFAULT_WEB_SESSION_TIMEOUT_MINUTES;
		this.scDownloadService = null;
		this.scUploadService = null;
		this.pageHeaderPrefix = null;
		this.scTerminateAllowed = false;
		this.colorScheme = Constants.DEFAULT_WEB_COLOR_SCHEME;
	}

	/**
	 * Loads web parameters from properties file.<br />
	 * Service Connector web parameters: <br />
	 * web.xslTransformationCache.enabled=true<br >
	 *
	 * @param compositeConfiguration the composite configuration
	 * @throws Exception the exception
	 */
	public synchronized void load(CompositeConfiguration compositeConfiguration) throws Exception {
		Boolean xslTransformationCacheEnabledConf = compositeConfiguration.getBoolean(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED, null);
		if (xslTransformationCacheEnabledConf != null) {
			this.xslTransformationCacheEnabled = xslTransformationCacheEnabledConf;
		}
		LOGGER.info(Constants.WEB_XSL_TRANSFORMATION_CACHE_ENABLED + "=" + this.xslTransformationCacheEnabled);

		this.pageHeaderPrefix = compositeConfiguration.getString(Constants.WEB_PAGE_HEADER_PREFIX, "");
		LOGGER.info(Constants.WEB_PAGE_HEADER_PREFIX + "=" + this.pageHeaderPrefix);

		this.scDownloadService = compositeConfiguration.getString(Constants.WEB_SC_DOWNLOAD_SERVICE, null);
		LOGGER.info(Constants.WEB_SC_DOWNLOAD_SERVICE + "=" + this.scDownloadService);
		if (this.scDownloadService != null) {
			// service must exist if it was speciffied
			if (AppContext.getServiceRegistry().getService(this.scDownloadService) == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, Constants.WEB_SC_DOWNLOAD_SERVICE + "=" + this.scDownloadService + " service not found");
			}
		}

		this.scUploadService = compositeConfiguration.getString(Constants.WEB_SC_UPLOAD_SERVICE, null);
		LOGGER.info(Constants.WEB_SC_UPLOAD_SERVICE + "=" + this.scUploadService);
		if (this.scUploadService != null) {
			// service must exist if it was speciffied
			if (AppContext.getServiceRegistry().getService(this.scUploadService) == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, Constants.WEB_SC_UPLOAD_SERVICE + "=" + this.scUploadService + " service not found");
			}
		}
		Boolean scTerminateAllowedConf = compositeConfiguration.getBoolean(Constants.WEB_SC_TERMINATE_ALLOWED, null);
		if (scTerminateAllowedConf != null) {
			this.scTerminateAllowed = scTerminateAllowedConf;
		}
		this.colorScheme = compositeConfiguration.getString(Constants.WEB_COLOR_SCHEMA, null);
		if (this.colorScheme == null || this.colorScheme.isEmpty()) {
			this.colorScheme = Constants.DEFAULT_WEB_COLOR_SCHEME;
		}
		LOGGER.info(Constants.WEB_COLOR_SCHEMA + "=" + this.colorScheme);
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
	 * Gets the web session schedule timeout seconds.
	 *
	 * @return the web session schedule timeout seconds
	 */
	public int getWebSessionScheduleTimeoutSeconds() {
		return webSessionScheduleTimeoutSeconds;
	}

	/**
	 * Gets the web session timeout minutes.
	 *
	 * @return the web session timeout minutes
	 */
	public int getWebSessionTimeoutMinutes() {
		return webSessionTimeoutMinutes;
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

	/**
	 * Checks if is SC can be terminated from the GUI.
	 *
	 * @return true, if termination is allowed
	 */
	public boolean isScTerminateAllowed() {
		return scTerminateAllowed;
	}

	/**
	 * Gets the color scheme
	 *
	 * @return the color scheme name
	 */
	public String getColorScheme() {
		return colorScheme;
	}
}
