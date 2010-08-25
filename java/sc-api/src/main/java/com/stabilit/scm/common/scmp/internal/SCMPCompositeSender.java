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
package com.stabilit.scm.common.scmp.internal;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SCMPCompositeSender. Used to handle outgoing large request/response. Works like an iterator and provides
 * functionality of splitting large SCMP into parts.
 * 
 * @author JTraber
 */
public class SCMPCompositeSender extends SCMPMessage {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPCompositeSender.class);
	
	/** The large scmp message. */
	private SCMPMessage message;
	/** The offset. */
	private int offset;
	/** The large message length. */
	private int largeMessageLength;
	/** The current part. */
	private SCMPMessage currentPart;

	/**
	 * Instantiates a new SCMPCompositeSender.
	 * 
	 * @param message
	 *            the scmp message
	 */
	public SCMPCompositeSender(SCMPMessage message) {
		this.message = message;
		this.largeMessageLength = this.message.getBodyLength();
		this.offset = 0;
		this.currentPart = null;
	}

	/**
	 * Gets the first part.
	 * 
	 * @return the first
	 */
	public SCMPMessage getFirst() {
		this.offset = 0;
		this.currentPart = new SCMPSendPart(this.message, this.offset);
		this.offset += currentPart.getBodyLength();
		return this.currentPart;
	}

	/**
	 * Checks for next part.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return this.offset < this.largeMessageLength;
	}

	/**
	 * Gets the next part.
	 * 
	 * @return the next
	 */
	public SCMPMessage getNext() {
		if (this.hasNext()) {
			this.currentPart = new SCMPSendPart(message, this.offset);
			this.offset += currentPart.getBodyLength();
			return this.currentPart;
		}
		this.currentPart = null;
		return this.currentPart;
	}

	/**
	 * @return the currentPart
	 */
	public SCMPMessage getCurrentPart() {
		return currentPart;
	}
}
