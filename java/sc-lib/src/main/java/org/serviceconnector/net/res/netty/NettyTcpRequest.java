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
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.RequestAdapter;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.Statistics;

/**
 * The Class NettyTcpRequest is responsible for reading a request from a ChannelBuffer. Decodes SCMP from a TCP frame. Based on
 * JBoss
 * Netty.
 */
public class NettyTcpRequest extends RequestAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NettyTcpRequest.class);
	/** The request. */
	private byte[] buffer;

	/**
	 * Instantiates a new netty tcp request.
	 * 
	 * @param event
	 *            the event from Netty framework
	 * @param localAddress
	 *            the local address
	 * @param remoteAddress
	 *            the remote address
	 */
	public NettyTcpRequest(byte[] buffer, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.buffer = buffer;
	}

	/** {@inheritDoc} */
	@Override
	public void load() throws Exception {
		Statistics.getInstance().incrementTotalMessages(buffer.length);
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), this.getRemoteSocketAddress().getHostName(), this
					.getRemoteSocketAddress().getPort(), buffer, 0, buffer.length);
		}
		IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		this.setMessage(message);
	}
}
