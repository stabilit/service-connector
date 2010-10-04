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

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.EncoderDecoderFactory;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.scmp.RequestAdapter;
import org.serviceconnector.scmp.SCMPMessage;


/**
 * The Class NettyTcpRequest is responsible for reading a request from a ChannelBuffer. Decodes SCMP from a TCP frame.
 * Based on JBoss Netty.
 */
public class NettyTcpRequest extends RequestAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpRequest.class);
	
	/** The Constant connectionLogger. */
	private final static ConnectionLogger connectionLogger = ConnectionLogger.getInstance();
	
	/** The request. */
	private ChannelBuffer request;

	/**
	 * Instantiates a new netty tcp request.
	 * 
	 * @param event
	 *            the event from Netty framework
	 * @param socketAddress
	 *            the socket address
	 */
	public NettyTcpRequest(MessageEvent event, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.request = (ChannelBuffer) event.getMessage();
	}

	/** {@inheritDoc} */
	public void load() throws Exception {
		byte[] buffer = new byte[request.readableBytes()];
		request.readBytes(buffer);
		if (connectionLogger.isTraceEnabled()) {
			connectionLogger.logReadBuffer(this.getClass().getSimpleName(), this.getLocalSocketAddress().getHostName(), this.getLocalSocketAddress().getPort(), buffer, 0, buffer.length);
		}
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.setMessage(message);
	}
}
