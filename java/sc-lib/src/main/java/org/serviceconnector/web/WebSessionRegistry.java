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
package org.serviceconnector.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.web.ctx.WebContext;

/**
 * The Class WebSessionRegistry.
 */
public final class WebSessionRegistry extends Registry<String, IWebSession> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebSessionRegistry.class);
	/** The instance. */
	private static WebSessionRegistry instance = new WebSessionRegistry();
	/** The web session scheduler. */
	private ScheduledThreadPoolExecutor webSessionScheduler;
	/** The expiration timeout run. */
	private WebSessionExpirationTimeoutRun webSessionExpirationTimeoutRun;

	/**
	 * Instantiates a new web session registry.
	 */
	private WebSessionRegistry() {
		this.webSessionScheduler = new ScheduledThreadPoolExecutor(1);
		int webSessionScheduleTimeoutSeconds = WebContext.getWebConfiguration().getWebSessionScheduleTimeoutSeconds();
		int webSessionTimeoutMinutes = WebContext.getWebConfiguration().getWebSessionTimeoutMinutes();
		this.webSessionExpirationTimeoutRun = new WebSessionExpirationTimeoutRun(webSessionTimeoutMinutes);
		this.webSessionScheduler.scheduleAtFixedRate(this.webSessionExpirationTimeoutRun, webSessionScheduleTimeoutSeconds,
				webSessionScheduleTimeoutSeconds, TimeUnit.SECONDS);
		LOGGER.debug("start web session expiration thread using timeout (s) = " + webSessionScheduleTimeoutSeconds);
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
		if (webSession != null) {
			webSession.access();
		}
		return webSession;
	}

	/**
	 * Removes the session.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(IWebSession session) {
		if (session == null) {
			return;
		}
		this.remove(session.getSessionId());
	}

	/**
	 * Removes the expired sessions.
	 * 
	 * @param timeoutMinutes
	 *            the timeout minutes
	 */
	public synchronized void removeExpiredSessions(int timeoutMinutes) {
		Object[] sessionKeys = this.getAllSessionKeys();
		if (sessionKeys == null) {
			return;
		}
		for (Object sessionKey : sessionKeys) {
			IWebSession session = (IWebSession) this.get((String) sessionKey);
			if (session.isExpired(timeoutMinutes)) {
				this.remove((String) sessionKey);
			}
		}
	}

	/**
	 * Gets the all session keys.
	 * 
	 * @return the all session keys
	 */
	public Object[] getAllSessionKeys() {
		Object[] keys = this.keySetArray();
		return keys;
	}

	/**
	 * The Class WebSession.
	 */
	private class WebSession implements IWebSession {

		/** The attr map. */
		private Map<String, Object> attrMap;

		/** The user agent. */
		private String userAgent;
		
		/** The local server host. */
		private String host;

		/** The local server port. */
		private int port;

		/** The remote host. */
		private String remoteHost;

		/** The remote port. */
		private int remotePort;

		/** The session id. */
		private String sessionId;

		/** The creation time stamp. */
		private long creationTimeStamp;

		/** The last access time stamp. */
		private long accessTimeStamp;

		/**
		 * Instantiates a new web session.
		 */
		public WebSession() {
			attrMap = new HashMap<String, Object>();
			UUID uuid = UUID.randomUUID();
			this.userAgent = null;
			this.host = null;
			this.remoteHost = null;
			this.port = 0;
			this.remotePort = 0;
			this.sessionId = uuid.toString();
			this.creationTimeStamp = System.currentTimeMillis();
			this.accessTimeStamp = this.creationTimeStamp;
			LOGGER.debug("New web session created, id = " + this.sessionId);
		}

		/**
		 * Gets the attribute.
		 * 
		 * @param key
		 *            the key
		 * @return the attribute {@inheritDoc}
		 */
		@Override
		public Object getAttribute(String key) {
			return this.attrMap.get(key);
		}

		/**
		 * Sets the attribute.
		 * 
		 * @param key
		 *            the key
		 * @param value
		 *            the value {@inheritDoc}
		 */
		@Override
		public void setAttribute(String key, Object value) {
			this.attrMap.put(key, value);

		}

		/**
		 * Removes the attribute.
		 * 
		 * @param key
		 *            the key
		 * @return the object {@inheritDoc}
		 */
		@Override
		public Object removeAttribute(String key) {
			return this.attrMap.remove(key);
		}

		/**
		 * Gets the user agent.
		 * 
		 * @return the user agent {@inheritDoc}
		 */
		@Override
		public String getUserAgent() {
			return this.userAgent;
		}

		/**
		 * Sets the user agent.
		 * 
		 * @param userAgent
		 *            the new user agent {@inheritDoc}
		 */
		@Override
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}
		
		/**
		 * Gets the host.
		 * 
		 * @return the host {@inheritDoc}
		 */
		@Override
		public String getHost() {
			return this.host;
		}

		/**
		 * Sets the host.
		 * 
		 * @param host
		 *            the new host {@inheritDoc}
		 */
		@Override
		public void setHost(String host) {
			this.host = host;
		}

		/**
		 * Gets the port.
		 * 
		 * @return the port {@inheritDoc}
		 */
		@Override
		public int getPort() {
			return this.port;
		}

		/**
		 * Sets the port.
		 * 
		 * @param port
		 *            the new port {@inheritDoc}
		 */
		@Override
		public void setPort(int port) {
			this.port = port;
		}

		/**
		 * Gets the remote host.
		 * 
		 * @return the remote host {@inheritDoc}
		 */
		@Override
		public String getRemoteHost() {
			return this.remoteHost;
		}

		/**
		 * Sets the remote host.
		 * 
		 * @param remoteHost
		 *            the new remote host {@inheritDoc}
		 */
		@Override
		public void setRemoteHost(String remoteHost) {
			this.remoteHost = remoteHost;
		}

		/**
		 * Gets the remote port.
		 * 
		 * @return the remote port {@inheritDoc}
		 */
		@Override
		public int getRemotePort() {
			return this.remotePort;
		}

		/**
		 * Sets the remote port.
		 * 
		 * @param remotePort
		 *            the new remote port {@inheritDoc}
		 */
		@Override
		public void setRemotePort(int remotePort) {
			this.remotePort = remotePort;
		}

		/**
		 * Gets the session id.
		 * 
		 * @return the session id {@inheritDoc}
		 */
		@Override
		public String getSessionId() {
			return this.sessionId;
		}

		/**
		 * Access. {@inheritDoc}
		 */
		@Override
		public void access() {
			this.accessTimeStamp = System.currentTimeMillis();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isExpired(long timeoutMinutes) {
			long timeoutMillis = timeoutMinutes * 1000 * 60;
			long currentMillis = System.currentTimeMillis();
			if (this.accessTimeStamp + timeoutMillis < currentMillis) {
				// current time is higher than max planned session inactivity time
				return true;
			}
			return false;
		}

	}

	/**
	 * The Class WebSessionExpirationTimeoutThread.
	 * 
	 * This class controls within a thread any web session instance for expiration
	 */
	private class WebSessionExpirationTimeoutRun implements Runnable {

		/** The max session inactivity timeout minutes. */
		private int timeoutMinutes;

		/**
		 * Instantiates a new expiration timeout thread.
		 * 
		 * @param timeoutMinutes
		 *            the timeout minutes
		 */
		public WebSessionExpirationTimeoutRun(int timeoutMinutes) {
			this.timeoutMinutes = timeoutMinutes;
		}

		/**
		 * web session expiration thread run method, checks withing given interval if web session elements were expired and removes
		 * them
		 * from web session registry.
		 * 
		 */
		@Override
		public void run() {
			WebSessionRegistry.this.removeExpiredSessions(this.timeoutMinutes);
			return;
		}
	}
}
