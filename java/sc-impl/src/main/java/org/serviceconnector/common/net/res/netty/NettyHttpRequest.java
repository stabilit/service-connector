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
package org.serviceconnector.common.net.res.netty;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.serviceconnector.common.log.IConnectionLogger;
import org.serviceconnector.common.log.impl.ConnectionLogger;
import org.serviceconnector.common.net.EncoderDecoderFactory;
import org.serviceconnector.common.net.IEncoderDecoder;
import org.serviceconnector.common.scmp.RequestAdapter;
import org.serviceconnector.common.scmp.SCMPMessage;


/**
 * The Class NettyHttpRequest is responsible for reading a request from a ChannelBuffer. Decodes scmp from a Http frame.
 * Based on JBoss Netty.
 */
public class NettyHttpRequest extends RequestAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpRequest.class);
	
	/** The Constant connectionLogger. */
	private final static IConnectionLogger connectionLogger = ConnectionLogger.getInstance();
	
	/** The request. */
	private HttpRequest request;

	/**
	 * Instantiates a new netty http request.
	 * 
	 * @param httpRequest
	 *            the request
	 * @param localAddress
	 *            the socket address
	 */
	public NettyHttpRequest(HttpRequest httpRequest, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.request = httpRequest;
	}

	/** {@inheritDoc} */
	@Override
	public void load() throws Exception {
		ChannelBuffer channelBuffer = request.getContent();
		byte[] buffer = new byte[channelBuffer.readableBytes()];
		channelBuffer.readBytes(buffer);
		if (connectionLogger.isDebugEnabled()) {
			connectionLogger.logReadBuffer(this.getClass().getSimpleName(), this.getLocalSocketAddress().getHostName(), this.getLocalSocketAddress().getPort(), buffer, 0, buffer.length);
		}
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.setMessage(message);
	}
}
