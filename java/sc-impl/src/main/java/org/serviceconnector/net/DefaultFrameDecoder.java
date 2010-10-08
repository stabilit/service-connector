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
package org.serviceconnector.net;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPHeadlineKey;


/**
 * The Class DefaultFrameDecoder.
 * 
 * @author JTraber
 */
public class DefaultFrameDecoder implements IFrameDecoder {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DefaultFrameDecoder.class);
	
	/**
	 * Instantiates a new default frame decoder.
	 */
	protected DefaultFrameDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public int parseFrameSize(byte[] buffer) throws FrameDecoderException {

		if (buffer == null || buffer.length < Constants.FIX_HEADLINE_SIZE) {
			return 0; // don't throw exception it is the case if client disconnects
		}
		// check headerKey
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.getKeyByHeadline(buffer);
		if (headerKey == SCMPHeadlineKey.UNDEF) {
			throw new FrameDecoderException("invalid scmp header line");
		}
		// parse frame size
		int scmpLength = this.parseMessageSize(buffer);
		return Constants.FIX_HEADLINE_SIZE + scmpLength;
	}
	
	@Override
	public int parseMessageSize(byte[] buffer) throws FrameDecoderException {
		return this.readInt(buffer, Constants.FIX_MSG_SIZE_START, Constants.FIX_MSG_SIZE_END);
	}
	
	@Override
	public int parseHeaderSize(byte[] buffer) throws Exception {
		return this.readInt(buffer, Constants.FIX_HEADER_SIZE_START, Constants.FIX_HEADER_SIZE_END);
	}

	/**
	 * Read int from byte buffer.
	 * 
	 * @param b
	 *            the b
	 * @param startOffset
	 *            the start offset
	 * @param endOffset
	 *            the end offset
	 * @return the int
	 * @throws FrameDecoderException
	 *             the frame decoder exception
	 */
	public int readInt(byte[] b, int startOffset, int endOffset) throws FrameDecoderException {

		if (b == null) {
			throw new FrameDecoderException("invalid scmp message length");
		}

		if (endOffset <= 0 || endOffset <= startOffset) {
			throw new FrameDecoderException("invalid scmp message length");
		}

		int scmpLength = 0;
		int factor = 1;
		for (int i = endOffset; i >= startOffset; i--) {
			if (b[i] >= '0' && b[i] <= '9') {
				scmpLength += ((int) b[i] - 0x30) * factor;
				factor *= 10;
			} else {
				throw new FrameDecoderException("invalid scmp message length");
			}
		}
		return scmpLength;
	}
}
