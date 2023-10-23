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
package org.serviceconnector.net.res;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class SCMPSessionCompositeRegistry. Stores composite components (large response/requests) of a communication to resume at the time it gets active again.
 *
 * @author JTraber
 */
public final class SCMPSessionCompositeRegistry extends Registry<String, SCMPSessionCompositeItem> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPSessionCompositeRegistry.class);
	private ScheduledThreadPoolExecutor largeMessageScheduler;

	public SCMPSessionCompositeRegistry() {
		this.largeMessageScheduler = new ScheduledThreadPoolExecutor(1);
	}

	/**
	 * Adds the session.
	 *
	 * @param key the key
	 */
	public void addSession(String key) {
		this.put(key, new SCMPSessionCompositeItem(key));
	}

	/**
	 * Removes the session.
	 *
	 * @param key the key
	 */
	public void removeSession(String key) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		this.cancelLargeMessageTimeout(item);
		super.remove(key);
	}

	/**
	 * Adds the scmp large request.
	 *
	 * @param key the key
	 * @param largeRequest the large request
	 */
	public void addSCMPLargeRequest(String key, SCMPCompositeReceiver largeRequest, int largeMessageTimeoutMillis) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeRequest(largeRequest);
		item.setLargeMessageTimeoutMillis(largeMessageTimeoutMillis);
		this.resetLargeMessageTimeout(item);
	}

	/**
	 * Gets the SCMP large request.
	 *
	 * @param key the key
	 * @return the SCMP large request
	 */
	public SCMPCompositeReceiver getSCMPLargeRequest(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getSCMPLargeRequest();
	}

	/**
	 * Removes the scmp large request.
	 *
	 * @param key the key
	 */
	public void removeSCMPLargeRequest(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		this.cancelLargeMessageTimeout(item);
		item.setSCMPLargeRequest(null);
	}

	/**
	 * Adds the SCMP large response.
	 *
	 * @param key the key
	 * @param largeResponse the large response
	 */
	public void addSCMPLargeResponse(String key, SCMPCompositeSender largeResponse, int largeMessageTimeoutMillis) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeResponse(largeResponse);
		item.setLargeMessageTimeoutMillis(largeMessageTimeoutMillis);
		this.resetLargeMessageTimeout(item);
	}

	/**
	 * Gets the SCMP large response.
	 *
	 * @param key the key
	 * @return the SCMP large response
	 */
	public SCMPCompositeSender getSCMPLargeResponse(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getSCMPLargeResponse();
	}

	/**
	 * Removes the scmp large response.
	 *
	 * @param key the key
	 */
	public void removeSCMPLargeResponse(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		this.cancelLargeMessageTimeout(item);
		item.setSCMPLargeResponse(null);
	}

	@SuppressWarnings("unchecked")
	private void scheduleLargeMessageTimeout(SCMPSessionCompositeItem sessionComposite) {
		if (sessionComposite == null || sessionComposite.getLargeMessageTimeoutMillis() == 0) {
			// no scheduling of session timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelLargeMessageTimeout(sessionComposite);
		LOGGER.trace("schedule large message sid=" + sessionComposite.getSessionId() + " timeout in millis " + (long) sessionComposite.getLargeMessageTimeoutMillis());
		// sets up session timeout
		TimeoutWrapper sessionTimeouter = new TimeoutWrapper(new LargeMessageTimeout(sessionComposite));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.largeMessageScheduler.schedule(sessionTimeouter,
				sessionComposite.getLargeMessageTimeoutMillis(), TimeUnit.MILLISECONDS);
		sessionComposite.setTimeout(timeout);
	}

	private void cancelLargeMessageTimeout(SCMPSessionCompositeItem sessionComposite) {
		if (sessionComposite == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> sessionTimeout = sessionComposite.getTimeout();
		if (sessionTimeout == null) {
			// no session timeout has been set up for this session
			return;
		}
		LOGGER.trace("cancel large message timeout sid=" + sessionComposite.getSessionId());
		sessionTimeout.cancel(false);
		this.largeMessageScheduler.purge();
		// important to set timeouter null - rescheduling of same instance not possible
		sessionComposite.setTimeout(null);
	}

	private synchronized void resetLargeMessageTimeout(SCMPSessionCompositeItem sessionComposite) {
		this.cancelLargeMessageTimeout(sessionComposite);
		this.scheduleLargeMessageTimeout(sessionComposite);
	}

	/**
	 * Gets the SCMP message id.
	 *
	 * @param key the key
	 * @return the SCMP message id
	 */
	public SCMPMessageSequenceNr getSCMPMsgSequenceNr(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getMsgSequenceNr();
	}

	private class LargeMessageTimeout implements ITimeout {

		private SCMPSessionCompositeItem sessionComposite;

		public LargeMessageTimeout(SCMPSessionCompositeItem sessionComposite) {
			this.sessionComposite = sessionComposite;
		}

		@Override
		public void timeout() {
			String sessionId = sessionComposite.getSessionId();
			LOGGER.error("Large message process timed out sid=" + sessionId);
			SCMPSessionCompositeRegistry.this.removeSCMPLargeRequest(sessionId);
			SCMPSessionCompositeRegistry.this.removeSCMPLargeResponse(sessionId);
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return sessionComposite.getLargeMessageTimeoutMillis();
		}
	}
}
