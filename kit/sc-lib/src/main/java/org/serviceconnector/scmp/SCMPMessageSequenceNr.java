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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;

/**
 * The Class SCMPMessageSequenceNr. Provides correct message sequence number (msn) for a specific request/response. Message sequence
 * number and session id are unique. Will be steadily increased.
 * 
 * @author JTraber
 */
public class SCMPMessageSequenceNr {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPMessageSequenceNr.class);

	/** The message sequence number. */
	private long msgSequenceNr;

	public SCMPMessageSequenceNr() {
		this(1);
	}

	public SCMPMessageSequenceNr(long msgSequenceNr) {
		this.msgSequenceNr = msgSequenceNr;
	}

	/**
	 * Gets the current number.
	 * 
	 * @return the current number
	 */
	public String getCurrentNr() {
		return String.valueOf(this.msgSequenceNr);
	}

	/**
	 * Increment message sequence number.
	 */
	public void incrementMsgSequenceNr() {
		try {
			this.msgSequenceNr++;
		} catch (Exception e) {
			// in case an exception number gets reseted
			this.reset();
		}
	}

	public void validateAndUpdate(SCMPMessage message) throws SCMPValidatorException {
		String messagSequenceNrValue = message.getMessageSequenceNr();
		long inMsgSequenceNr = 0;
		try {
			inMsgSequenceNr = Long.parseLong(messagSequenceNrValue);
		} catch (NumberFormatException ex) {
			logger.warn("parsing message sequence number failed, not a Long value");
		}
		if (inMsgSequenceNr <= this.msgSequenceNr) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "message sequence number is not ascending");
		}
		this.msgSequenceNr = inMsgSequenceNr;
	}

	/**
	 * Reset msgSequenceNr.
	 */
	public void reset() {
		this.msgSequenceNr = 1;
	}

	/**
	 * Necessary to write. Evaluates if msgSequenceNr needs to be written for specific messageType.
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