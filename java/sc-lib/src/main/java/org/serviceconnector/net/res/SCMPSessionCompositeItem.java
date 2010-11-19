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
import org.serviceconnector.scmp.SCMPLargeRequest;
import org.serviceconnector.scmp.SCMPLargeResponse;
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
	private SCMPLargeResponse largeResponse;
	/** The sender. */
	private SCMPLargeRequest largeRequest;
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
	 * @param largeResponse
	 *            the large response
	 * @param largeRequest
	 *            the large request
	 */
	public SCMPSessionCompositeItem(SCMPLargeResponse largeResponse, SCMPLargeRequest largeRequest) {
		this.largeResponse = largeResponse;
		this.largeRequest = largeRequest;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	/**
	 * Gets the SCMP large response.
	 * 
	 * @return the SCMP large response
	 */
	public SCMPLargeResponse getSCMPLargeResponse() {
		return largeResponse;
	}

	/**
	 * Gets the sCMP large request.
	 * 
	 * @return the sCMP large request
	 */
	public SCMPLargeRequest getSCMPLargeRequest() {
		return largeRequest;
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
	 * @param largeResponse
	 *            the new sCMP large response
	 */
	public void setSCMPLargeResponse(SCMPLargeResponse largeResponse) {
		this.largeResponse = largeResponse;
	}

	/**
	 * Sets the large request.
	 * 
	 * @param largeRequest
	 *            the new large request
	 */
	public void setSCMPLargeRequest(SCMPLargeRequest largeRequest) {
		this.largeRequest = largeRequest;
	}
}