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
package org.serviceconnector.net.res;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class ResponseAdapter. Provides basic functionality for responses.
 *
 * @author JTraber
 */
public abstract class ResponseAdapter implements IResponse {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseAdapter.class);

	/** The encoder decoder. */
	protected IEncoderDecoder encoderDecoder;
	/** The scmp. */
	protected SCMPMessage scmp;
	/** The event from Netty framework. */
	protected Channel channel;

	/**
	 * Instantiates a new response adapter.
	 *
	 * @param channel the event
	 */
	public ResponseAdapter(Channel channel) {
		this.scmp = null;
		this.channel = channel;
	}

	/** {@inheritDoc} */
	@Override
	public abstract void write() throws Exception;

	/**
	 * Gets the buffer. Encodes the scmp.
	 *
	 * @return the buffer
	 * @throws Exception the exception
	 */
	public ByteBuf getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(this.scmp);
		encoderDecoder.encode(baos, scmp);
		byte[] buf = baos.toByteArray();
		return Unpooled.copiedBuffer(buf);
	}

	/** {@inheritDoc} */
	@Override
	public void setSCMP(SCMPMessage scmp) {
		if (scmp == null) {
			return;
		}
		scmp.setIsReply(true);
		this.scmp = scmp;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getSCMP() {
		return this.scmp;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLarge() {
		if (this.scmp == null) {
			return false;
		}
		return this.scmp.isLargeMessage();
	}
}
