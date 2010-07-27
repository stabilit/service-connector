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
package com.stabilit.scm.common.scmp;

/**
 * The Class SCMPMessageId. Responsible to provide correct message id for a specific request/response. Message id is
 * unique for every message. Format: messageSequenceNr / partSequenceNr.
 * 
 * @author JTraber
 */
public class SCMPMessageId {

	/** The message sequence number. */
	private int msgSequenceNr;
	/** The part sequence number. */
	private int partSequenceNr;
	/** The string builder. */
	private StringBuilder sb;

	/**
	 * Instantiates a new scmp message id.
	 */
	public SCMPMessageId() {
		this.msgSequenceNr = 1;
		this.partSequenceNr = 0;
		this.sb = null;
	}

	/**
	 * Gets the current message id.
	 * 
	 * @return the current message id
	 */
	public String getCurrentMessageID() {
		if (partSequenceNr == 0) {
			// no part SCMP has been sent, partSequenceNr irrelevant
			return String.valueOf(msgSequenceNr);
		}
		sb = new StringBuilder();
		sb.append(msgSequenceNr);
		sb.append("/");
		sb.append(partSequenceNr);
		return sb.toString();
	}

	/**
	 * Increment part sequence number.
	 */
	public void incrementPartSequenceNr() {
		partSequenceNr++;
	}

	/**
	 * Increment message sequence number.
	 */
	public void incrementMsgSequenceNr() {
		// partSequenceNr reset when msgSequenceNr gets incremented
		partSequenceNr = 0;
		msgSequenceNr++;
	}

	/**
	 * Gets the message sequence number.
	 * 
	 * @return the message sequence number
	 */
	public Integer getMessageSequenceNr() {
		return msgSequenceNr;
	}

	/**
	 * Gets the part sequence number.
	 * 
	 * @return the part sequence number
	 */
	public Integer getPartSequenceNr() {
		return partSequenceNr;
	}
}
