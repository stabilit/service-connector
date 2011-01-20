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

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;

/**
 * The Class SCMPSessionCompositeItem. Item represents a value in SCMPSessionCompositeRegistry. Gives access to composite
 * receiver/sender and in the context used SCMPMessageId.
 * 
 * @author JTraber
 */
public class SCMPSessionCompositeItem {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPSessionCompositeItem.class);

	/** The large response. */
	private SCMPCompositeReceiver largeRequest;
	/** The sender. */
	private SCMPCompositeSender largeResponse;
	/** The msgSequenceNr. */
	private SCMPMessageSequenceNr msgSequenceNr;

	/**
	 * Instantiates a new SCMP session composite item.
	 */
	public SCMPSessionCompositeItem() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMP session composite item.
	 * 
	 * @param largeRequest
	 *            the large response
	 * @param largeResponse
	 *            the large request
	 */
	public SCMPSessionCompositeItem(SCMPCompositeReceiver largeRequest, SCMPCompositeSender largeResponse) {
		this.largeRequest = largeRequest;
		this.largeResponse = largeResponse;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
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
}