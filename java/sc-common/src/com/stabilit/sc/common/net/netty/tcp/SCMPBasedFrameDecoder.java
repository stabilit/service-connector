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

import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.net.FrameDecoderFactory;

/**
 * @author JTraber
 * 
 */
public class SCMPBasedFrameDecoder extends FrameDecoder implements ChannelHandler {

	private DecodeState decodeState;
	private IFrameDecoder scmpFrameDecoder;
	private int scmpFrameSize;

	public SCMPBasedFrameDecoder() {
		this.scmpFrameSize = 0;
		this.decodeState = DecodeState.READY;
		// warning, returns always the same instance, singleton
		this.scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
			throws Exception {
		switch (this.decodeState) {
		case READY:
			// read scmp headline
			try {
				// parse headline
				scmpFrameSize = scmpFrameDecoder.parseFrameSize(buffer.toByteBuffer().array());
				if (scmpFrameSize == 0) {
					return null;
				}
				this.decodeState = DecodeState.SIZE;
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

	private enum DecodeState {
		UNDEFINED, READY, SIZE, EXC;
	}
}
