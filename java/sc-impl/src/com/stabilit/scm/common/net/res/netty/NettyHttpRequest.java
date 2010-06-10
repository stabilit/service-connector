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
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.scm.common.ctx.RequestContext;
import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.net.EncoderDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.scmp.RequestAdapter;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class NettyHttpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Http frame.
 * Based on JBoss Netty.
 */
public class NettyHttpRequest extends RequestAdapter {

	/** The request. */
	private HttpRequest request;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty http request.
	 * 
	 * @param httpRequest
	 *            the request
	 * @param localAddress
	 *            the socket address
	 */
	public NettyHttpRequest(HttpRequest httpRequest, SocketAddress localAddress, SocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.mapBean = new MapBean<Object>();
		this.request = httpRequest;
		this.message = null;
		this.requestContext = new RequestContext(this.remoteSocketAddress);
	}

	/** {@inheritDoc} */
	@Override
	public void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = new byte[channelBuffer.readableBytes()];
		channelBuffer.readBytes(buffer);
		ConnectionPoint.getInstance().fireRead(this, ((InetSocketAddress) this.localSocketAddress).getPort(), buffer);
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.message = message;
	}
}
