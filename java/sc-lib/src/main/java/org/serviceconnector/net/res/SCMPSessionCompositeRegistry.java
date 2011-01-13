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
import org.serviceconnector.registry.Registry;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;

/**
 * The Class SCMPSessionCompositeRegistry. Stores composite components (large response/requests) of a communication to resume at the
 * time it gets active again.
 * 
 * @author JTraber
 */
public final class SCMPSessionCompositeRegistry extends Registry<String, SCMPSessionCompositeItem> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPSessionCompositeRegistry.class);

	/**
	 * Adds the session.
	 * 
	 * @param key
	 *            the key
	 */
	public void addSession(String key) {
		this.put(key, new SCMPSessionCompositeItem());
	}

	/**
	 * Removes the session.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSession(String key) {
		super.remove(key);
	}

	/**
	 * Adds the scmp large request.
	 * 
	 * @param key
	 *            the key
	 * @param largeRequest
	 *            the large request
	 */
	public void addSCMPLargeRequest(String key, SCMPCompositeReceiver largeRequest) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeRequest(largeRequest);
	}

	/**
	 * Gets the SCMP large request.
	 * 
	 * @param key
	 *            the key
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
	 * @param key
	 *            the key
	 */
	public void removeSCMPLargeRequest(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeRequest(null);
	}

	/**
	 * Adds the SCMP large response.
	 * 
	 * @param key
	 *            the key
	 * @param largeResponse
	 *            the large response
	 */
	public void addSCMPLargeResponse(String key, SCMPCompositeSender largeResponse) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeResponse(largeResponse);
	}

	/**
	 * Gets the SCMP large response.
	 * 
	 * @param key
	 *            the key
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
	 * @param key
	 *            the key
	 */
	public void removeSCMPLargeResponse(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		item.setSCMPLargeResponse(null);
	}

	/**
	 * Gets the SCMP message id.
	 * 
	 * @param key
	 *            the key
	 * @return the SCMP message id
	 */
	public SCMPMessageSequenceNr getSCMPMsgSequenceNr(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getMsgSequenceNr();
	}
}
