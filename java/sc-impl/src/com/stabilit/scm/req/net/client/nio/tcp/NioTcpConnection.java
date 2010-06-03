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
package com.stabilit.scm.cln.net.client.nio.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.scm.cln.client.IConnection;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.net.EncoderDecoderFactory;
import com.stabilit.scm.net.FrameDecoderFactory;
import com.stabilit.scm.net.IEncoderDecoder;
import com.stabilit.scm.net.IFrameDecoder;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.srv.net.SCMPCommunicationException;

/**
 * The Class NioTcpConnection. Concrete connection implementation on Nio base for Tcp.
 */
public class NioTcpConnection implements IConnection {

	/** The socket channel. */
	private SocketChannel socketChannel;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The numberOfThreads. */
	private int numberOfThreads;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new NioTcpConnection.
	 */
	public NioTcpConnection() {
		this.socketChannel = null;
		this.port = 0;
		this.host = null;
		this.numberOfThreads = 10;
		this.encoderDecoder = null;
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(true);
		ConnectionPoint.getInstance().fireConnect(this, this.socketChannel.socket().getLocalPort());
		socketChannel.connect(new InetSocketAddress(this.host, this.port));
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ConnectionPoint.getInstance().fireDisconnect(this, this.socketChannel.socket().getLocalPort());
		socketChannel.close();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		encoderDecoder.encode(baos, scmp);
		byte[] byteWriteBuffer = baos.toByteArray();
		ByteBuffer writeBuffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionPoint.getInstance().fireWrite(this, this.socketChannel.socket().getLocalPort(), byteWriteBuffer);
		socketChannel.write(writeBuffer);
		// read response
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
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionPoint.getInstance().fireRead(this, this.socketChannel.socket().getLocalPort(), byteReadBuffer,
				0, bytesRead);

		int scmpLengthHeadlineInc = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
		// continues reading until http frame is complete
		while (scmpLengthHeadlineInc > bytesRead) {
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
		encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage ret = (SCMPMessage) encoderDecoder.decode(bais);
		bais.close();
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NioTcpConnection();
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}
}
