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
import com.stabilit.sc.common.io.SCMPHeaderKey;

/**
 * @author JTraber
 * 
 */
public class DefaultFrameDecoder implements IFrameDecoder {

	public DefaultFrameDecoder() {
	}
	
	@Override
	public IFactoryable newInstance() {	
		return this;  //singleton
	}
	
	@Override
	public int parseFrameSize(byte[] buffer) throws FrameDecoderException {

		SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		int scmpHeadlineLength = 0;
		int scmpLength = 0;

		int readableBytes = buffer.length;
		if (readableBytes == 0) {
			return 0;
		}
		for (int i = 0; i < readableBytes; i++) {
			byte b = buffer[i];
			if (b == '\n') {
				if (i <= 2) {
					throw new FrameDecoderException("invalid scmp header line");
				}
				headerKey = SCMPHeaderKey.getMsgHeaderKey(buffer);
				if (headerKey == SCMPHeaderKey.UNDEF) {
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
