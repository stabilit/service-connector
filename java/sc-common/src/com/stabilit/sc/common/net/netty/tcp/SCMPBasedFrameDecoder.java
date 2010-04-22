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
