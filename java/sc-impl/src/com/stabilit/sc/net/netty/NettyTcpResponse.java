/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.sc.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.ResponseAdapter;
import com.stabilit.sc.scmp.SCMP;

/**
 * The Class NettyTcpResponse is responsible for writing a response to a ChannelBuffer. Encodes scmp to a Tcp
 * frame. Based on JBoss Netty.
 */
public class NettyTcpResponse extends ResponseAdapter {

	/** The event from Netty framework. */
	private MessageEvent event;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty tcp response.
	 * 
	 * @param event
	 *            the event
	 */
	public NettyTcpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
	}

	/**
	 * Gets the event.
	 * 
	 * @return the event
	 */
	public MessageEvent getEvent() {
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
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		encoderDecoder.encode(baos, scmp);
		byte[] buf = baos.toByteArray();
		return ChannelBuffers.copiedBuffer(buf);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.ResponseAdapter#setSCMP(com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		scmp.setIsReply(true);
		this.scmp = scmp;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IResponse#write()
	 */
	@Override
	public void write() throws Exception {
		ChannelBuffer buffer = this.getBuffer();
		// Write the response.
		event.getChannel().write(buffer);
		ConnectionListenerSupport.getInstance().fireWrite(this, buffer.toByteBuffer().array());
	}
}
