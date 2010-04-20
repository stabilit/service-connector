/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
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
