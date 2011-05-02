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

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class SCMPSessionCompositeItem. Item represents a value in SCMPSessionCompositeRegistry. Gives access to composite
 * receiver/sender and in the context used SCMPMessageId.
 * 
 * @author JTraber
 */
public class SCMPSessionCompositeItem {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPSessionCompositeItem.class);

	/** The large response. */
	private SCMPCompositeReceiver largeRequest;
	/** The sender. */
	private SCMPCompositeSender largeResponse;
	/** The msgSequenceNr. */
	private SCMPMessageSequenceNr msgSequenceNr;
	/** The large message timeout. Time observed to abort a large request communication. */
	private int largeMessageTimeout;
	/** The session id. */
	private String sessionId;
	/** The large message timeout. */
	private ScheduledFuture<TimeoutWrapper> timeout;
	
	/**
	 * Instantiates a new SCMP session composite item.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param largeMessageTimeout
	 *            the large message timeout
	 */
	public SCMPSessionCompositeItem(String sessionId, int largeMessageTimeout) {
		this.largeRequest = null;
		this.largeResponse = null;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
		this.sessionId = sessionId;
	}

	/**
	 * Gets the SCMP large response.
	 * 
	 * @return the SCMP large response
	 */
	public SCMPCompositeReceiver getSCMPLargeRequest() {
		return largeRequest;
	}

	/**
	 * Gets the SCMP large request.
	 * 
	 * @return the SCMP large request
	 */
	public SCMPCompositeSender getSCMPLargeResponse() {
		return largeResponse;
	}

	/**
	 * Gets the msg sequence nr.
	 * 
	 * @return the msg sequence nr
	 */
	public SCMPMessageSequenceNr getMsgSequenceNr() {
		return msgSequenceNr;
	}

	/**
	 * Sets the SCMP large response.
	 * 
	 * @param largeRequest
	 *            the new SCMP large response
	 */
	public void setSCMPLargeRequest(SCMPCompositeReceiver largeRequest) {
		this.largeRequest = largeRequest;
	}

	/**
	 * Sets the large request.
	 * 
	 * @param largeResponse
	 *            the new large request
	 */
	public void setSCMPLargeResponse(SCMPCompositeSender largeResponse) {
		this.largeResponse = largeResponse;
	}

	/**
	 * Gets the large message timeout.
	 * 
	 * @return the large message timeout
	 */
	public int getLargeMessageTimeoutMillis() {
		return largeMessageTimeout;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * Gets the session timeout.
	 * 
	 * @return the session timeout
	 */
	public ScheduledFuture<TimeoutWrapper> getTimeout() {
		return timeout;
	}

	/**
	 * Sets the session timeout.
	 * 
	 * @param timeout
	 *            the new session timeout
	 */
	public void setTimeout(ScheduledFuture<TimeoutWrapper> timeout) {
		this.timeout = timeout;
	}
}