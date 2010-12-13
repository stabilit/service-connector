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
package org.serviceconnector.scmp;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;

/**
 * The Class ResponseAdapter. Provides basic functionality for responses.
 * 
 * @author JTraber
 */
public abstract class ResponseAdapter implements IResponse {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ResponseAdapter.class);
	
	/** The encoder decoder. */
	protected IEncoderDecoder encoderDecoder;
	/** The scmp. */
	protected SCMPMessage scmp;
	/** The event from Netty framework. */
	protected ChannelEvent event;

	/**
	 * Instantiates a new response adapter.
	 */
	public ResponseAdapter(ChannelEvent event) {
		this.scmp = null;
		this.event = event;
	}
	
	/** {@inheritDoc} */
	@Override
	public abstract void write() throws Exception;

	/**
	 * Gets the buffer. Encodes the scmp.
	 * 
	 * @return the buffer
	 * @throws Exception
	 *             the exception
	 */
	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(this.scmp);
		encoderDecoder.encode(baos, scmp);
		byte[] buf = baos.toByteArray();
		return ChannelBuffers.copiedBuffer(buf);
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
	
	/**
	 * Gets the event.
	 * 
	 * @return the event
	 */
	public ChannelEvent getEvent() {
		return event;
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
