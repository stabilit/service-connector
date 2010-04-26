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
package com.stabilit.sc.common.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.scmp.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.IEncoderDecoder;
import com.stabilit.sc.common.scmp.ResponseAdapter;
import com.stabilit.sc.common.scmp.SCMP;

public class NettyTcpResponse extends ResponseAdapter {

	private MessageEvent event;
	private IEncoderDecoder encoderDecoder;

	public NettyTcpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
	}

	public MessageEvent getEvent() {
		return event;
	}

	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		}
		encoderDecoder.encode(baos, scmp);
		byte[] buf = baos.toByteArray();
		return ChannelBuffers.copiedBuffer(buf);
	}

	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.encoderDecoder = encoderDecoder;
	}

	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		this.scmp = scmp;
	}

	@Override
	public void write() throws Exception {
		ChannelBuffer buffer = this.getBuffer();
		// Write the response.
		ChannelFuture future = event.getChannel().write(buffer);
		ConnectionListenerSupport.fireWrite(this, buffer.toByteBuffer().array());  // logs inside if registered
	}
}
