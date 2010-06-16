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
package com.stabilit.scm.common.net.res.nio.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.log.listener.ConnectionPoint;
import com.stabilit.scm.common.net.FrameDecoderFactory;
import com.stabilit.scm.common.net.IFrameDecoder;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.net.SCMPStreamHttpUtil;
import com.stabilit.scm.common.scmp.RequestAdapter;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class NioHttpRequest is responsible for reading a request from a socketChannel. Decodes scmp from a Http frame.
 * Based on Nio.
 */
public class NioHttpRequest extends RequestAdapter {

	/** The socket channel. */
	private SocketChannel socketChannel;
	/** The stream http util. */
	private SCMPStreamHttpUtil streamHttpUtil;

	/**
	 * Instantiates a new nio http request.
	 * 
	 * @param socketChannel
	 *            the socket channel
	 */
	public NioHttpRequest(SocketChannel socketChannel, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.socketChannel = socketChannel;
		this.streamHttpUtil = new SCMPStreamHttpUtil();
	}

	/** {@inheritDoc} */
	public void load() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb buffer
		int bytesRead = 0;
		try {
			bytesRead = socketChannel.read(byteBuffer);
		} catch (Throwable ex) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		if (bytesRead < 0) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getFrameDecoder(IConstants.HTTP);
		// warning, returns always the same instance, singleton
		byte[] byteReadBuffer = byteBuffer.array();
		int httpFrameSize = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteReadBuffer, 0, bytesRead);
		// continues reading until http frame is complete
		while (httpFrameSize > bytesRead) {
			byteBuffer.clear();
			int read = 0;
			try {
				read = socketChannel.read(byteBuffer);
			} catch (Throwable ex) {
				throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
			}
			if (read < 0) {
				throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
			}
			bytesRead += read;
			baos.write(byteBuffer.array(), 0, read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		ConnectionPoint.getInstance().fireRead(this, this.getLocalSocketAddress().getPort(), buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) streamHttpUtil.readSCMP(bais);
		bais.close();
		this.setMessage(message);
	}
}
