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

import org.apache.log4j.Logger;

/**
 * The Class SCMPMessageId. Responsible to provide correct message id for a specific request/response. Message id is
 * unique for every message. Format: messageSequenceNr / partSequenceNr.
 * 
 * @author JTraber
 */
public class SCMPMessageId {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPMessageId.class);
	
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

	/**
	 * Reset messageId.
	 */
	public void reset() {
		this.msgSequenceNr = 1;
		this.partSequenceNr = 0;
		this.sb = null;
	}

	/**
	 * Necessary to write. Evaluates if messageId needs to be written for specific messageType.
	 * 
	 * @param messageTypeValue
	 *            the message type value
	 * @return true, if successful
	 */
	public static boolean necessaryToWrite(String messageTypeValue) {
		SCMPMsgType messageType = SCMPMsgType.getMsgType(messageTypeValue);

		switch (messageType) {
		case CLN_CREATE_SESSION:
		case SRV_CREATE_SESSION:
		case CLN_DELETE_SESSION:
		case SRV_DELETE_SESSION:
		case CLN_EXECUTE:
		case SRV_EXECUTE:
		case ECHO:
		case CLN_SUBSCRIBE:
		case SRV_SUBSCRIBE:
		case CLN_CHANGE_SUBSCRIPTION:
		case SRV_CHANGE_SUBSCRIPTION:
		case CLN_UNSUBSCRIBE:
		case SRV_UNSUBSCRIBE:
		case RECEIVE_PUBLICATION:
		case PUBLISH:
			return true;
		default:
			return false;
		}
	}
}
