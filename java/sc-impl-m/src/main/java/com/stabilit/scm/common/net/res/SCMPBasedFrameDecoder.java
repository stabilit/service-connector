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
package com.stabilit.scm.common.net.res;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.stabilit.scm.common.net.FrameDecoderException;
import com.stabilit.scm.common.net.FrameDecoderFactory;
import com.stabilit.scm.common.net.IFrameDecoder;
import com.stabilit.scm.common.scmp.SCMPError;

/**
 * The Class SCMPBasedFrameDecoder. Decodes a SCMP frame.
 * 
 * @author JTraber
 */
public class SCMPBasedFrameDecoder extends FrameDecoder implements ChannelHandler {

	/** The decode state. */
	private DecodeState decodeState;
	/** The scmp frame decoder. */
	private IFrameDecoder scmpFrameDecoder;
	/** The scmp frame size. */
	private int scmpFrameSize;

	/**
	 * Instantiates a new SCMPBasedFrameDecoder.
	 */
	public SCMPBasedFrameDecoder() {
		this.scmpFrameSize = 0;
		this.decodeState = DecodeState.READY;
		// warning, returns always the same instance, singleton
		this.scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
	}

	/** {@inheritDoc} */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		switch (this.decodeState) {
		case READY:
			try {
				// parse headline
				scmpFrameSize = scmpFrameDecoder.parseFrameSize(buffer.toByteBuffer().array());
				if (scmpFrameSize == 0) {
					return null;
				}
				this.decodeState = DecodeState.SIZE;
			} catch (FrameDecoderException e) {
				decodeState = DecodeState.EXC;
				throw new SCMPDecoderException(SCMPError.FRAME_DECODER, e);
			}
			break;
		case SIZE:
			break;
		default:
			throw new SCMPDecoderException(SCMPError.FRAME_DECODER);
		}
		if (this.decodeState == DecodeState.SIZE) {
			int readableBytes = buffer.readableBytes();
			byte[] readableBuffer = new byte[readableBytes];
			buffer.getBytes(0, readableBuffer);
			// continue reading if frame is not complete
			if (readableBytes >= scmpFrameSize) {
				this.decodeState = DecodeState.READY;
				return buffer.readBytes(scmpFrameSize);
			}
		}
		return null;
	}

	/**
	 * The Enum DecodeState. Possible states of decoding process.
	 */
	private enum DecodeState {

		/** The UNDEFINED. */
		UNDEFINED,
		/** The READY, ready to parse frame size. */
		READY,
		/** The SIZE, frame size parsed - read data. */
		SIZE,
		/** The EXC, exception in decoding process. */
		EXC;
	}
}
