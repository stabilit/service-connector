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
package org.serviceconnector.web.cmd.sc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.log4j.Logger;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.AjaxContentXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.AjaxMaintenanceXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.AjaxResourceXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.AjaxSystemXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.CacheXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.DefaultXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.DumpXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.LogsXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.MaintenanceXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.ResourceXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.RespondersXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.ServersXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.ServicesXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.SessionsXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.SubscriptionsXMLLoader;
import org.serviceconnector.web.cmd.sc.impl.TimerXMLLoader;

/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class DefaultXMLLoaderFactory {

	/** The Constant LOGGER. */
	public final static Logger LOGGER = Logger.getLogger(DefaultXMLLoaderFactory.class);

	/** The loader factory. */
	protected static DefaultXMLLoaderFactory loaderFactory = new DefaultXMLLoaderFactory();

	private Map<String, IXMLLoader> loaderMap = new ConcurrentHashMap<String, IXMLLoader>();

	public static DefaultXMLLoaderFactory getLoaderFactory() {
		return loaderFactory;
	}
	
	/**
	 * Instantiates a new default xml loader factory.
	 */
	public DefaultXMLLoaderFactory() {
		IXMLLoader loader = new DefaultXMLLoader();
		this.addXMLLoader("default", loader);
		loader = new ServicesXMLLoader();
		this.addXMLLoader("/services", loader);
		loader = new SessionsXMLLoader();
		this.addXMLLoader("/sessions", loader);
		loader = new SubscriptionsXMLLoader();
		this.addXMLLoader("/subscriptions", loader);
		loader = new ServersXMLLoader();
		this.addXMLLoader("/servers", loader);
		loader = new RespondersXMLLoader();
		this.addXMLLoader("/listeners", loader);
		loader = new ResourceXMLLoader();
		this.addXMLLoader("/resource", loader);
		loader = new LogsXMLLoader();
		this.addXMLLoader("/logs", loader);
		loader = new CacheXMLLoader();
		this.addXMLLoader("/cache", loader);
		loader = new MaintenanceXMLLoader();
		this.addXMLLoader("/maintenance", loader);
		loader = new DumpXMLLoader();
		this.addXMLLoader("/dump", loader);
		loader = new AjaxResourceXMLLoader();
		this.addXMLLoader("/ajax/resource", loader);
		loader = new TimerXMLLoader();
		this.addXMLLoader("/ajax/timer", loader);
		loader = new AjaxSystemXMLLoader();
		this.addXMLLoader("/ajax/system", loader);
		loader = new AjaxContentXMLLoader();
		this.addXMLLoader("/ajax/content", loader);
		loader = new AjaxMaintenanceXMLLoader();
		this.addXMLLoader("/ajax/maintenance", loader);
	}

	/**
	 * Adds the xml loader.
	 * 
	 * @param key
	 *            the key
	 * @param loader
	 *            the loader
	 */
	public void addXMLLoader(String key, IXMLLoader loader) {
		this.loaderMap.put(key, loader);
	}

	/**
	 * Gets the xML loader.
	 * 
	 * @param url
	 *            the url
	 * @return the xML loader
	 */
	public IXMLLoader getXMLLoader(String url) {
		if (url == null) {
			return this.loaderMap.get("default");
		}
		int questionMarkPos = url.indexOf("?");
		if (questionMarkPos > 0) {
			url = url.substring(0, questionMarkPos);
		}
		IXMLLoader xmlLoader = this.loaderMap.get(url);
		if (xmlLoader == null) {
			xmlLoader = this.loaderMap.get("default");
		}
		if (xmlLoader == null) {
			return null;
		}
		return (IXMLLoader) xmlLoader.newInstance();
	}

}
