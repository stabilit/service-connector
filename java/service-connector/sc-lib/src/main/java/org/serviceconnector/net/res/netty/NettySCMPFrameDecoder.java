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
package org.serviceconnector.net.res.netty;

import java.util.List;

import org.serviceconnector.Constants;
import org.serviceconnector.net.SCMPFrameDecoder;
import org.serviceconnector.scmp.SCMPError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * The Class NettySCMPFrameDecoder. Decodes a SCMP frame.
 *
 * @author JTraber
 */
public class NettySCMPFrameDecoder extends ByteToMessageDecoder {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettySCMPFrameDecoder.class);
	/** The scmp frame size. */
	private int scmpFrameSize;
	/** The headline. */
	private byte[] headline = new byte[Constants.SCMP_HEADLINE_SIZE];

	/**
	 * Instantiates a new NettySCMPFrameDecoder.
	 */
	public NettySCMPFrameDecoder() {
		this.scmpFrameSize = 0;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (this.scmpFrameSize != 0) {
			// headline and frame size has already been decoded
			byte[] ret = this.aggregateFrame(in);
			if (ret != null) {
				out.add(ret);
			}
		} else {
			// try reading headline & extracting frame size
			this.decodeFrameSizeFromHeadline(in);
			if (scmpFrameSize != 0) {
				// headline decoded try aggregate whole frame
				byte[] ret = this.aggregateFrame(in);
				if (ret != null) {
					out.add(ret);
				}
			}
		}
	}

	/**
	 * Decode frame size from headline.
	 *
	 * @param buffer the buffer
	 * @throws SCMPFrameDecoderException the sCMP frame decoder exception
	 */
	private void decodeFrameSizeFromHeadline(ByteBuf buffer) throws SCMPFrameDecoderException {
		if (buffer.readableBytes() < Constants.SCMP_HEADLINE_SIZE) {
			// not enough bytes in buffer to decode the SCMP headline
			return;
		}
		try {
			buffer.getBytes(0, this.headline);
			// parse headline
			this.scmpFrameSize = SCMPFrameDecoder.parseFrameSize(this.headline);
		} catch (Exception ex) {
			LOGGER.warn("decode " + ex.getMessage());
			throw new SCMPFrameDecoderException(SCMPError.FRAME_DECODER);
		}
	}

	/**
	 * Aggregate frame.
	 *
	 * @param buffer the buffer
	 * @return the byte[]
	 */
	private byte[] aggregateFrame(ByteBuf buffer) {
		if (buffer.readableBytes() < scmpFrameSize) {
			return null;
		}

		byte[] frame = new byte[scmpFrameSize];
		buffer.readBytes(frame);

		// reset the frame size
		this.scmpFrameSize = 0;
		return frame;
	}
}
