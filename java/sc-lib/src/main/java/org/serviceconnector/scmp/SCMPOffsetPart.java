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

import java.io.InputStream;

/**
 * The Class SCMPOffsetPart. Represents an outgoing part SCMP of a large message. Extends SCMPPart and adds information of current
 * body like offset, totalBodyLength.
 * 
 * @author JTraber
 */
public class SCMPOffsetPart extends SCMPPart {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1542392908180130094L;

	/** The offset where body starts. */
	private int offset;
	/** The size of this specific part SCMP. */
	private int size;
	/** The call length, total length of the large message. */
	private int callLength;

	/**
	 * Instantiates a new SCMPOffsetPart.
	 * 
	 * @param message
	 *            the scmp message
	 * @param offset
	 *            the offset
	 * @param largeMessageLength
	 *            the large message length
	 */
	public SCMPOffsetPart(SCMPMessage message, int offset, int largeMessageLength) {
		this.offset = offset;
		this.callLength = largeMessageLength;
		// evaluates the size of this part
		if (this.callLength - this.offset < message.getPartSize()) {
			this.size = this.callLength - this.offset;
		} else {
			this.size = message.getPartSize();
		}
		this.setPartSize(message.getPartSize());
		this.setHeader(message);
		this.setInternalStatus(message.getInternalStatus());
		this.setBody(message.getBody());
		this.setIsReply(message.isReply());
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPart() {
		if (this.isGroup()) {
			return true;
		}
		if (this.getBodyType().equals(SCMPBodyType.INPUT_STREAM)) {
			// we never know if the stream has data available do not know its size
			@SuppressWarnings("unused")
			InputStream is = (InputStream) this.getBody();
			return true;
//			// needs to be different in case of STREAM - total length is misleading
//			try {
//				if (((InputStream) this.getBody()).available() - this.size <= 0) {
//					return false;
//				}
//			} catch (IOException e) {
//				return false;
//			}
		}
		return offset + size < callLength;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBodyOffset() {
		return true;
	}

	/** {@inheritDoc} */
	public int getBodyOffset() {
		return offset;
	}

	/** {@inheritDoc} */
	@Override
	public int getBodyLength() {
		return this.size;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReply() {
		return super.isReply();
	}
}
