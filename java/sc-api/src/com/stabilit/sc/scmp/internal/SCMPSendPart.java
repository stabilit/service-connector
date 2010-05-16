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
package com.stabilit.sc.scmp.internal;

import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Class SCMPSendPart. Represents an outgoing part SCMP of a large message.
 * 
 * @author JTraber
 */
public class SCMPSendPart extends SCMPPart {

	/** The offset where body starts. */
	private int offset;
	/** The size of this specific part SCMP. */
	private int size;
	/** The call length, total length of the large message. */
	private int callLength;

	/**
	 * Instantiates a new SCMPSendPart.
	 * 
	 * @param message
	 *            the scmp message
	 * @param offset
	 *            the offset
	 */
	public SCMPSendPart(SCMPMessage message, int offset) {
		this.offset = offset;
		this.callLength = message.getBodyLength();
		// evaluates the size of this part
		this.size = this.callLength - this.offset < SCMPMessage.LARGE_MESSAGE_LIMIT ? this.callLength - this.offset
				: SCMPMessage.LARGE_MESSAGE_LIMIT;
		this.setHeader(message);
		this.setInternalStatus(message.getInternalStatus());
		this.setBody(message.getBody());
		this.setIsReply(message.isReply());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMPPart#isPart()
	 */
	@Override
	public boolean isPart() {
		if (this.isGroup()) {
			return true;
		}
		return offset + size < callLength;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isBodyOffset()
	 */
	@Override
	public boolean isBodyOffset() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getBodyOffset()
	 */
	public int getBodyOffset() {
		return offset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getBodyLength()
	 */
	@Override
	public int getBodyLength() {
		return this.size;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isReply()
	 */
	@Override
	public boolean isReply() {
		return super.isReply();
	}
}
