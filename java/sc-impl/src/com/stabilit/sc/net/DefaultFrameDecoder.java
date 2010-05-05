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
package com.stabilit.sc.net;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.SCMPHeadlineKey;

/**
 * @author JTraber
 * 
 */
public class DefaultFrameDecoder implements IFrameDecoder {

	protected DefaultFrameDecoder() {
	}

	@Override
	public IFactoryable newInstance() {
		return this; // singleton
	}

	@Override
	public int parseFrameSize(byte[] buffer) throws FrameDecoderException {

		if (buffer == null || buffer.length <= 0) {
			return 0; //don't throw exception it is the case if client disconnects 
		}
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		int scmpHeadlineLength = 0;
		int scmpLength = 0;
		int readableBytes = buffer.length;

		for (int i = 0; i < readableBytes; i++) {
			byte b = buffer[i];
			if (b == '\n') {
				if (i <= 2) {
					throw new FrameDecoderException("invalid scmp header line");
				}
				headerKey = SCMPHeadlineKey.getKeyByHeadline(buffer);
				if (headerKey == SCMPHeadlineKey.UNDEF) {
					throw new FrameDecoderException("invalid scmp header line");
				}

				int startIndex = 0;
				int endIndex = 0;
				label: for (startIndex = 0; startIndex < buffer.length; startIndex++) {

					if (buffer[startIndex] == '/' || buffer[startIndex] == '&') {

						if (buffer[startIndex + 1] == 's' && buffer[startIndex + 2] == '=') {

							startIndex += 3;
							for (endIndex = startIndex; endIndex < buffer.length; endIndex++) {
								if (buffer[endIndex] == '&' || buffer[endIndex] == ' ')
									break label;
							}
						}
					}
				}
				// parse scmpLength
				scmpLength = readInt(buffer, startIndex, endIndex - 1);
				scmpHeadlineLength = i + 1;
				return scmpLength + scmpHeadlineLength;
			}
		}
		throw new FrameDecoderException("invalid scmp header line");
	}

	public int readInt(byte[] b, int startOffset, int endOffset) throws FrameDecoderException {

		if (b == null) {
			throw new FrameDecoderException("invalid scmp message length");
		}

		if (endOffset <= 0 || endOffset <= startOffset) {
			throw new FrameDecoderException("invalid scmp message length");
		}

		if ((endOffset - startOffset) > 5) {
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
