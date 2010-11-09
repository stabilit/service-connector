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

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.scmp.ResponseAdapter;
import org.serviceconnector.scmp.SCMPMessage;


/**
 * The Class NettyTcpResponse is responsible for writing a response to a ChannelBuffer. Encodes SCMP to a TCP
 * frame. Based on JBoss Netty.
 */
public class NettyTcpResponse extends ResponseAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpResponse.class);
	
	/** The Constant connectionLogger. */
	private final static ConnectionLogger connectionLogger = ConnectionLogger.getInstance();
	
	/** The event from Netty framework. */
	private ChannelEvent event;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty tcp response.
	 * 
	 * @param event
	 *            the event
	 */
	public NettyTcpResponse(ChannelEvent event) {
		this.scmp = null;
		this.event = event;
	}

	/**
	 * Gets the event.
	 * 
	 * @return the event
	 */
	public ChannelEvent getEvent() {
		return event;
	}

	/**
	 * Gets the buffer. Encodes the scmp.
	 * 
	 * @return the buffer
	 * @throws Exception
	 *             the exception
	 */
	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = AppContext.getCurrentContext().getEncoderDecoderFactory().createEncoderDecoder(this.scmp);
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

	/** {@inheritDoc} */
	@Override
	public void write() throws Exception {
		ChannelBuffer buffer = this.getBuffer();
		// Write the response.
		event.getChannel().write(buffer);
		if (connectionLogger.isEnabledFull()) {
			connectionLogger.logWriteBuffer(this.getClass().getSimpleName(), ((InetSocketAddress) this.event.getChannel()
					.getLocalAddress()).getHostName(), ((InetSocketAddress) this.event.getChannel().getLocalAddress()).getPort(),
					buffer.toByteBuffer().array(), 0, buffer.toByteBuffer().array().length);
		}
	}
}
