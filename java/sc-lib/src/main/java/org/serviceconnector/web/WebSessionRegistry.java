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
package org.serviceconnector.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.serviceconnector.registry.Registry;

/**
 * The Class WebSessionRegistry.
 */
public class WebSessionRegistry extends Registry<String, IWebSession> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(WebSessionRegistry.class);

	/** The instance. */
	private static WebSessionRegistry instance = new WebSessionRegistry();

	/**
	 * Instantiates a new web session registry.
	 */
	private WebSessionRegistry() {
	}

	/**
	 * Gets the current instance of web session registry.
	 * 
	 * @return the current instance
	 */
	public static WebSessionRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * New session.
	 * 
	 * @return the i web session
	 */
	public IWebSession newSession() {
		IWebSession webSession = new WebSession();
		this.put(webSession.getSessionId(), webSession);
		return webSession;
	}

	/**
	 * Gets the session.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the session
	 */
	public IWebSession getSession(String sessionId) {
		IWebSession webSession = this.get(sessionId);
		return webSession;
	}

	/**
	 * The Class WebSession.
	 */
	private class WebSession implements IWebSession {

		/** The attr map. */
		private Map<String, Object> attrMap;

		/** The creation time. */
		private long creationTime;

		/** The access time. */
		private long accessTime;

		/** The session id. */
		private String sessionId;

		/**
		 * Instantiates a new web session.
		 */
		public WebSession() {
			attrMap = new HashMap<String, Object>();
			UUID uuid = UUID.randomUUID();
			this.sessionId = uuid.toString();
			this.creationTime = System.currentTimeMillis();
			this.accessTime = this.creationTime;
			logger.debug("New web session created, id = " + this.sessionId);
		}

		/** {@inheritDoc} */
		@Override
		public Object getAttribute(String key) {
			return this.attrMap.get(key);
		}

		/** {@inheritDoc} */
		@Override
		public void setAttribute(String key, Object value) {
			this.attrMap.put(key, value);

		}

		/** {@inheritDoc} */
		@Override
		public Object removeAttribute(String key) {
			return this.attrMap.remove(key);
		}

		/** {@inheritDoc} */
		@Override
		public String getSessionId() {
			return this.sessionId;
		}

	}
}
