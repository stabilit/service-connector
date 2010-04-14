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
package com.stabilit.sc.common.net.netty.tcp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.stabilit.sc.common.io.SCMPHeaderKey;
import com.stabilit.sc.common.net.nio.SCMPNioDecoderException;

/**
 * @author JTraber
 * 
 */
public class SCMPBasedFrameDecoder extends FrameDecoder implements ChannelHandler {

	private DecodeState decodeState;

	public SCMPBasedFrameDecoder() {
		this.decodeState = DecodeState.READY;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
			throws Exception {
		int scmpFrameSize = 0;
		switch (this.decodeState) {
		case READY:
			// read scmp headline
			try {
				scmpFrameSize = SCMPBasedFrameDecoder.parseScmpFrameSize(buffer.toByteBuffer()
						.array());
			} catch (SCMPDecoderException e) {
				decodeState = DecodeState.EXC;
				throw e;
			}
			break;
		case SIZE:
			break;
		default:
			throw new SCMPDecoderException("invalid scmp header line");
		}
		if (this.decodeState == DecodeState.SIZE) {
			int readableBytes = buffer.readableBytes();
			byte[] readableBuffer = new byte[readableBytes];
			buffer.getBytes(0, readableBuffer);
			if (readableBytes >= scmpFrameSize) {
				this.decodeState = DecodeState.READY;
				return buffer.readBytes(scmpFrameSize);
			}
		}
		return null;
	}

	public static int parseScmpFrameSize(byte[] buffer) throws Exception {

		SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		int scmpHeadlineLength = 0;
		int scmpLength = 0;

		int readableBytes = buffer.length;
		for (int i = 0; i < readableBytes; i++) {
			byte b = buffer[i];
			if (b == '\n') {
				if (i <= 2) {
					throw new SCMPDecoderException("invalid scmp header line");
				}
				headerKey = SCMPHeaderKey.getMsgHeaderKey(buffer);
				if (headerKey == SCMPHeaderKey.UNDEF) {
					throw new SCMPDecoderException("invalid scmp header line");
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
		throw new SCMPNioDecoderException("invalid scmp header line");
	}

	public static int readInt(byte[] b, int startOffset, int endOffset) throws SCMPDecoderException {

		if (b == null) {
			throw new SCMPDecoderException("invalid scmp message length");
		}

		if (endOffset <= 0 || endOffset <= startOffset) {
			throw new SCMPDecoderException("invalid scmp message length");
		}

		if ((endOffset - startOffset) > 5) {
			throw new SCMPDecoderException("invalid scmp message length");
		}

		int scmpLength = 0;
		int factor = 1;
		for (int i = endOffset; i >= startOffset; i--) {
			if (b[i] >= '0' && b[i] <= '9') {
				scmpLength += ((int) b[i] - 0x30) * factor;
				factor *= 10;
			} else {
				throw new SCMPDecoderException("invalid scmp message length");
			}
		}
		return scmpLength;
	}

	private enum DecodeState {
		UNDEFINED, READY, SIZE, EXC;
	}
}
