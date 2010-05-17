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
package com.stabilit.sc.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.ctx.RequestContext;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.FrameDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.net.IFrameDecoder;
import com.stabilit.sc.scmp.RequestAdapter;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPError;
import com.stabilit.sc.srv.net.SCMPCommunicationException;
import com.stabilit.sc.util.MapBean;

/**
 * The Class NioTcpRequest is responsible for reading a request from a socketChannel. Decodes scmp from a Tcp
 * frame. Based on Nio.
 */
public class NioTcpRequest extends RequestAdapter {

	/** The socket channel. */
	private SocketChannel socketChannel;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new nio tcp request.
	 * 
	 * @param socketChannel
	 *            the socket channel
	 */
	public NioTcpRequest(SocketChannel socketChannel) {
		this.mapBean = new MapBean<Object>();
		this.socketChannel = socketChannel;
		this.socketAddress = socketChannel.socket().getLocalSocketAddress();
		this.message = null;
		this.requestContext = new RequestContext(socketChannel.socket().getRemoteSocketAddress());
	}

	/** {@inheritDoc} */
	@Override
	public void load() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb buffer
		int bytesRead = 0;
		try {
			bytesRead = socketChannel.read(byteBuffer);
		} catch (Throwable ex) {
			throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
		}
		if (bytesRead < 0) {
			throw new NioTcpDisconnectException("line disconnected");
		}
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
		// warning, returns always the same instance, singleton
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionListenerSupport.getInstance().fireRead(this, this.socketChannel.socket().getLocalPort(),
				byteReadBuffer, 0, bytesRead);
		int scmpLengthHeadlineInc = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
		// continues reading until tpc frame is complete
		while (scmpLengthHeadlineInc > bytesRead) {
			byteBuffer.clear();
			int read = 0;
			try {
				read = socketChannel.read(byteBuffer);
			} catch (Throwable ex) {
				throw new SCMPCommunicationException(SCMPError.CONNECTION_LOST);
			}
			if (read < 0) {
				throw new IOException("read failed (<0)");
			}
			bytesRead += read;
			baos.write(byteBuffer.array(), 0, read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage message = (SCMPMessage) encoderDecoder.decode(bais);
		bais.close();
		this.message = message;
	}
}
