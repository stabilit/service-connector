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
package com.stabilit.scm.common.net.res.netty;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.scm.ctx.RequestContext;
import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.net.EncoderDecoderFactory;
import com.stabilit.scm.net.IEncoderDecoder;
import com.stabilit.scm.scmp.RequestAdapter;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.util.MapBean;

/**
 * The Class NettyTcpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Tcp frame.
 * Based on JBoss Netty.
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
	public NettyTcpRequest(MessageEvent event, SocketAddress localAddress, SocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.mapBean = new MapBean<Object>();
		this.request = (ChannelBuffer) event.getMessage();
		this.message = null;
		this.requestContext = new RequestContext(event.getRemoteAddress());
	}

	/** {@inheritDoc} */
	public void load() throws Exception {
		byte[] buffer = new byte[request.readableBytes()];
		request.readBytes(buffer);
		ConnectionPoint.getInstance().fireRead(this, ((InetSocketAddress) this.localSocketAddress).getPort(), buffer);
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.message = message;
	}
}
