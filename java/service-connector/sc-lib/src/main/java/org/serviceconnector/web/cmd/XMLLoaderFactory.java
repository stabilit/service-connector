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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.web.xml.AjaxContentXMLLoader;
import org.serviceconnector.web.xml.AjaxMaintenanceXMLLoader;
import org.serviceconnector.web.xml.AjaxResourceXMLLoader;
import org.serviceconnector.web.xml.AjaxSystemXMLLoader;
import org.serviceconnector.web.xml.CacheXMLLoader;
import org.serviceconnector.web.xml.DefaultXMLLoader;
import org.serviceconnector.web.xml.DumpXMLLoader;
import org.serviceconnector.web.xml.IXMLLoader;
import org.serviceconnector.web.xml.LogsXMLLoader;
import org.serviceconnector.web.xml.MaintenanceXMLLoader;
import org.serviceconnector.web.xml.ResourceXMLLoader;
import org.serviceconnector.web.xml.RespondersXMLLoader;
import org.serviceconnector.web.xml.ServersXMLLoader;
import org.serviceconnector.web.xml.ServicesXMLLoader;
import org.serviceconnector.web.xml.SessionsXMLLoader;
import org.serviceconnector.web.xml.SubscriptionsXMLLoader;
import org.serviceconnector.web.xml.TimerXMLLoader;

/**
 * A factory for creating XMLLoader objects.
 */
public class XMLLoaderFactory {

	/** The Constant LOGGER. */
	public static final Logger LOGGER = LoggerFactory.getLogger(XMLLoaderFactory.class);

	/**
	 * Gets the XML loader.
	 *
	 * @param url the url
	 * @return the XML loader
	 */
	public static IXMLLoader getXMLLoader(String url) {
		if (url == null) {
			return new DefaultXMLLoader();
		}
		int questionMarkPos = url.indexOf("?");
		if (questionMarkPos > 0) {
			url = url.substring(0, questionMarkPos);
		}

		if (url.equals("/services")) {
			return new ServicesXMLLoader();
		} else if (url.equals("/sessions")) {
			return new SessionsXMLLoader();
		} else if (url.equals("/subscriptions")) {
			return new SubscriptionsXMLLoader();
		} else if (url.equals("/servers")) {
			return new ServersXMLLoader();
		} else if (url.equals("/listeners")) {
			return new RespondersXMLLoader();
		} else if (url.equals("/resource")) {
			return new ResourceXMLLoader();
		} else if (url.equals("/logs")) {
			return new LogsXMLLoader();
		} else if (url.equals("/cache")) {
			return new CacheXMLLoader();
		} else if (url.equals("/maintenance")) {
			return new MaintenanceXMLLoader();
		} else if (url.equals("/dump")) {
			return new DumpXMLLoader();
		} else if (url.equals("/ajax/resource")) {
			return new AjaxResourceXMLLoader();
		} else if (url.equals("/ajax/timer")) {
			return new TimerXMLLoader();
		} else if (url.equals("/ajax/system")) {
			return new AjaxSystemXMLLoader();
		} else if (url.equals("/ajax/content")) {
			return new AjaxContentXMLLoader();
		} else if (url.equals("/ajax/maintenance")) {
			return new AjaxMaintenanceXMLLoader();
		} else {
			return new DefaultXMLLoader();
		}
	}
}
