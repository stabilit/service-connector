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
package org.serviceconnector.ctx;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.cache.CacheConfiguration;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.RequesterConfiguration;
import org.serviceconnector.conf.ResponderConfiguration;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.ServerLoader;
import org.serviceconnector.service.ServiceLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurationContext.
 */
public class ConfigurationContext {
	
	/** The Constant instance. */
	private static final ConfigurationContext instance = new ConfigurationContext();

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ConfigurationContext.class);

	/** The properties. */
	private CompositeConfiguration apacheCompositeConfig;

	/** The basic configuration. */
	private BasicConfiguration basicConfiguration;
	
	/** The cache configuration. */
	private CacheConfiguration cacheConfiguration;
	
	/** The responder configuration. */
	private ResponderConfiguration responderConfiguration;
	
	/** The requester configuration. */
	private RequesterConfiguration requesterConfiguration;

	/**
	 * Inits the context.
	 *
	 * @param configFile the config file
	 * @throws Exception the exception
	 */
	public void initContext(String configFile) throws Exception {
		this.apacheCompositeConfig = new CompositeConfiguration();
		try {
			this.apacheCompositeConfig.addConfiguration(new PropertiesConfiguration(configFile));
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, e.toString());
		}
		this.basicConfiguration = new BasicConfiguration();
		this.basicConfiguration.init(this.apacheCompositeConfig);
		this.cacheConfiguration = new CacheConfiguration();
		this.cacheConfiguration.init(this.apacheCompositeConfig);
		this.responderConfiguration = new ResponderConfiguration();
		this.responderConfiguration.init(this.apacheCompositeConfig);
		this.requesterConfiguration = new RequesterConfiguration();
		this.requesterConfiguration.init(this.apacheCompositeConfig);
		// load servers
		ServerLoader.load(this.apacheCompositeConfig);
		// load services
		ServiceLoader.load(this.apacheCompositeConfig);

	}

	/**
	 * Gets the current context.
	 *
	 * @return the current context
	 */
	public static ConfigurationContext getCurrentContext() {
		return ConfigurationContext.instance;
	}

	/**
	 * Gets the basic configuration.
	 *
	 * @return the basic configuration
	 */
	public BasicConfiguration getBasicConfiguration() {
		return basicConfiguration;
	}
	
	/**
	 * Gets the cache configuration.
	 *
	 * @return the cache configuration
	 */
	public CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}
	
	/**
	 * Gets the requester configuration.
	 *
	 * @return the requester configuration
	 */
	public RequesterConfiguration getRequesterConfiguration() {
		return requesterConfiguration;
	}
	
	/**
	 * Gets the responder configuration.
	 *
	 * @return the responder configuration
	 */
	public ResponderConfiguration getResponderConfiguration() {
		return responderConfiguration;
	}
	/**
	 * Instantiates a new configuration context.
	 */
	private ConfigurationContext() {

	}

	/**
	 * The Class PropertiesLoader.
	 */
	private class PropertiesLoader extends PropertiesConfiguration {
		
		/**
		 * Instantiates a new properties loader.
		 *
		 * @param fileName the file name
		 * @throws ConfigurationException the configuration exception
		 */
		public PropertiesLoader(String fileName) throws ConfigurationException {
			super(fileName);
		}

		/* (non-Javadoc)
		 * @see org.apache.commons.configuration.BaseConfiguration#addPropertyDirect(java.lang.String, java.lang.Object)
		 */
		@Override
		protected void addPropertyDirect(String key, Object value) {
			Object previousValue = getProperty(key);
			if (previousValue == null) {
				super.addPropertyDirect(key, value);
			}
			return;
		}
	}

}
