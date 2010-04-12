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

/**
 * @author JTraber
 * 
 */
public class SCMPBasedFrameDecoder extends FrameDecoder implements ChannelHandler {

	private DecodeState decodeState;
	private SCMPHeaderKey headerKey;
	private int scmpHeadlineLength;
	private int scmpLength;

	public SCMPBasedFrameDecoder() {
		this.decodeState = DecodeState.READY;
		this.headerKey = SCMPHeaderKey.UNDEF;
		this.scmpHeadlineLength = 0;
		this.scmpLength = 0;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
			throws Exception {
		
		switch (this.decodeState) {
		case READY:
			// read scmp headline
			scmpHeadlineRead(ctx, channel, buffer);
			break;
		case SIZE:
			break;
		default:
			throw new SCMPNettyDecoderException("invalid scmp header line");
		}
		if (this.decodeState == DecodeState.SIZE) {
			int readableBytes = buffer.readableBytes();
			byte[] readableBuffer = new byte[readableBytes];
			buffer.getBytes(0, readableBuffer);
			System.out.println(new String(readableBuffer));
			if (readableBytes >= this.scmpLength + this.scmpHeadlineLength) {
				this.decodeState = DecodeState.READY;
				return buffer.readBytes(this.scmpLength + this.scmpHeadlineLength);
			}
		}
		return null;
	}

	private void scmpHeadlineRead(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
			throws Exception {
		int readableBytes = buffer.readableBytes();
		for (int i = 0; i < readableBytes; i++) {
			byte b = buffer.getByte(i);
			if (b == '\n') {
				if (i <= 2) {
					decodeState = DecodeState.EXC;
					throw new SCMPNettyDecoderException("invalid scmp header line");
				}
				byte[] headLine = new byte[i + 1];
				// we reached the line
				buffer.getBytes(0, headLine);
				this.headerKey = SCMPHeaderKey.getMsgHeaderKey(headLine);
				if (this.headerKey == SCMPHeaderKey.UNDEF) {
					decodeState = DecodeState.EXC;
					throw new SCMPNettyDecoderException("invalid scmp header line");
				}

				int startIndex = 0;
				int endIndex = 0;
				System.out.println(new String(headLine));
				label:
				for (startIndex = 0; startIndex < headLine.length; startIndex++) {

					if (headLine[startIndex] == '/' || headLine[startIndex] == '&') {

						if (headLine[startIndex + 1] == 's' && headLine[startIndex + 2] == '=') {

							startIndex += 3;
							for (endIndex = startIndex; endIndex < headLine.length; endIndex++) {
								if (headLine[endIndex] == '&' || headLine[endIndex] == ' ')
									break label;
							}

						}
					}
				}
				// parse scmpLength
				this.scmpLength = readInt(headLine, startIndex, endIndex-1);
				this.scmpHeadlineLength = i+1;
				decodeState = DecodeState.SIZE;
				return;
			}
		}
	}

	private int readInt(byte[] b, int startOffset, int endOffset) throws SCMPNettyDecoderException {

		if (b == null) {
			throw new SCMPNettyDecoderException("invalid scmp message length");
		}

		if (endOffset <= 0 || endOffset <= startOffset) {
			throw new SCMPNettyDecoderException("invalid scmp message length");
		}

		if ((endOffset - startOffset) > 5) {
			throw new SCMPNettyDecoderException("invalid scmp message length");
		}

		int scmpLength = 0;
		int factor = 1;
		for (int i = endOffset; i >= startOffset; i--) {
			if (b[i] >= '0' && b[i] <= '9') {
				scmpLength += ((int)b[i]-0x30) * factor;
				factor *= 10;
			} else {
				throw new SCMPNettyDecoderException("invalid scmp message length");
			}
		}
		return scmpLength;
	}

	private enum DecodeState {
		UNDEFINED, READY, SIZE, EXC;
	}
}
