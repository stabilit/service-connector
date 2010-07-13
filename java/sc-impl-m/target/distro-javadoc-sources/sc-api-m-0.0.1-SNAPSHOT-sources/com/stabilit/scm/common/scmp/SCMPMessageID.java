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
 * The Class SCMPMessageID. Responsible to provide correct message id for a specific request/response. Message id
 * is unique for every message. Format: messageSequenceNr / partSequenceNr.
 * 
 * @author JTraber
 */
public class SCMPMessageID {

	/** The msg sequence nr. */
	private int msgSequenceNr;
	/** The part sequence nr. */
	private int partSequenceNr;
	
	/** The string builder. */
	private StringBuilder stringBuilder;

	/**
	 * Instantiates a new scmp message id.
	 */
	public SCMPMessageID() {
		this.msgSequenceNr = 1;
		this.partSequenceNr = 0;
		this.stringBuilder = null;
	}

	/**
	 * Gets the next message id.
	 * 
	 * @return the next message id
	 */
	public String getNextMessageID() {
		if (partSequenceNr == 0) {
			//no part SCMP has been sent, partSequenceNr irrelevant
			return String.valueOf(msgSequenceNr);
		}
		stringBuilder = new StringBuilder();
		stringBuilder.append(msgSequenceNr);
		stringBuilder.append("/");
		stringBuilder.append(partSequenceNr);
		return stringBuilder.toString();
	}

	/**
	 * Increment part sequence nr.
	 */
	public void incrementPartSequenceNr() {
		partSequenceNr++;
	}

	/**
	 * Increment msg sequence nr.
	 */
	public void incrementMsgSequenceNr() {
		//partSequenceNr reset when msgSequenceNr gets incremented
		partSequenceNr = 0;
		msgSequenceNr++;
	}

	/**
	 * Gets the message sequence nr.
	 * 
	 * @return the message sequence nr
	 */
	public Integer getMessageSequenceNr() {
		return msgSequenceNr;
	}

	/**
	 * Gets the part sequence nr.
	 * 
	 * @return the part sequence nr
	 */
	public Integer getPartSequenceNr() {
		return partSequenceNr;
	}
}
