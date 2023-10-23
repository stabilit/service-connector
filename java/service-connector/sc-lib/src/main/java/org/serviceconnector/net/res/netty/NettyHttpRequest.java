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

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.RequestAdapter;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.Statistics;

/**
 * The Class NettyHttpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Http frame. Based on JBoss Netty.
 */
public class NettyHttpRequest extends RequestAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpRequest.class);

	/** The request. */
	private FullHttpRequest request;

	/**
	 * Instantiates a new netty http request.
	 *
	 * @param httpRequest the request
	 * @param localAddress the socket address
	 * @param remoteAddress the remote address
	 */
	public NettyHttpRequest(FullHttpRequest httpRequest, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.request = httpRequest;
	}

	/** {@inheritDoc} */
	@Override
	public void load() throws Exception {
		ByteBuf byteBuf = request.content();
		try {			
			byte[] buffer = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(buffer);
			Statistics.getInstance().incrementTotalMessages(buffer.length);
			if (ConnectionLogger.isEnabledFull()) {
				ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), this.getRemoteSocketAddress().getHostName(), this.getRemoteSocketAddress().getPort(), buffer, 0,
						buffer.length);
			}
			IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
			this.setMessage(message);
		} finally {
			ReferenceCountUtil.release(byteBuf);
		}
					
	}
}
