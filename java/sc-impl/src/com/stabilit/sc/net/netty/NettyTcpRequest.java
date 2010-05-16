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
package com.stabilit.sc.net.netty;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.ctx.RequestContext;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.RequestAdapter;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.util.MapBean;

/**
 * The Class NettyTcpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Tcp
 * frame. Based on JBoss Netty.
 */
public class NettyTcpRequest extends RequestAdapter {

	/** The request. */
	private ChannelBuffer request;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty tcp request.
	 * 
	 * @param event
	 *            the event from Netty framework
	 * @param socketAddress
	 *            the socket address
	 */
	public NettyTcpRequest(MessageEvent event, SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
		this.mapBean = new MapBean<Object>();
		this.request = (ChannelBuffer) event.getMessage();
		this.message = null;
		this.requestContext = new RequestContext(event.getRemoteAddress());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#load()
	 */
	public void load() throws Exception {
		byte[] buffer = new byte[request.readableBytes()];
		request.readBytes(buffer);
		ConnectionListenerSupport.getInstance().fireRead(this, buffer); // logs inside if registered
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.message = message;
	}
}
