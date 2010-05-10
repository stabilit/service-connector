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
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.stabilit.sc.ctx.RequestContext;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.RequestAdapter;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.util.MapBean;

/**
 * The Class NettyHttpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Http
 * frame. Based on JBoss Netty.
 */
public class NettyHttpRequest extends RequestAdapter {

	/** The request. */
	private HttpRequest request;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty http request.
	 * 
	 * @param request
	 *            the request
	 * @param socketAddress
	 *            the socket address
	 */
	public NettyHttpRequest(HttpRequest request, SocketAddress socketAddress) {
		this.mapBean = new MapBean<Object>();
		this.request = request;
		this.scmp = null;
		this.socketAddress = socketAddress;
		this.requestContext = new RequestContext(this.socketAddress);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#load()
	 */
	@Override
	public void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = new byte[channelBuffer.readableBytes()];
		channelBuffer.readBytes(buffer);
		ConnectionListenerSupport.getInstance().fireRead(this, buffer); // logs inside if registered
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
		this.scmp = scmp;
	}
}
