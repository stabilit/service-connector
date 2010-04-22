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
package com.stabilit.sc.common.net;

import com.stabilit.sc.common.factory.IFactoryable;

/**
 * @author JTraber
 * 
 */
public class HttpFrameDecoder extends DefaultFrameDecoder {

	static final byte CR = 13;
	static final byte LF = 10;
	static final byte[] CRLF = new byte[] { CR, LF };

	public HttpFrameDecoder() {
	}

	@Override
	public IFactoryable newInstance() {
		return new HttpFrameDecoder();
	}

	@Override
	public int parseFrameSize(byte[] buffer) throws FrameDecoderException {

		int sizeStart = 0;
		int sizeEnd = 0;
		int headerEnd = 0;
		int bytesRead = buffer.length;

		label: for (int i = 0; i < bytesRead; i++) {
			if (buffer[i] == CR && buffer[i + 1] == LF) {
				i += 2;
 				if (buffer[i] == CR && buffer[i + 1] == LF) {
					headerEnd = i + 2;
					break label;
				}
				if (buffer[i] == 'C' && buffer[i + 7] == '-' && buffer[i + 8] == 'L' && buffer[i + 14] == ':') {
					sizeStart = i + 16;
					sizeEnd = sizeStart + 1;
					while (sizeEnd < bytesRead) {
						if (buffer[sizeEnd + 1] == CR && buffer[sizeEnd + 2] == LF) {
							break;
						}
						sizeEnd++;
					}
				}
			}
		}
		int contentLength = readInt(buffer, sizeStart, sizeEnd);
		return contentLength + headerEnd;
	}
}
