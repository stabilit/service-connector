/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.cln.net.client.nio.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.scm.cln.client.IConnection;
import com.stabilit.scm.config.IConstants;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.net.FrameDecoderFactory;
import com.stabilit.scm.net.IFrameDecoder;
import com.stabilit.scm.net.SCMPStreamHttpUtil;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.srv.net.SCMPCommunicationException;

/**
 * The Class NioHttpConnection. Concrete connection implementation on Nio base for Http.
 */
public class NioHttpConnection implements IConnection {

	/** The socket channel. */
	private SocketChannel socketChannel;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The numberOfThreads. */
	private int numberOfThreads;
	/** The stream Http util. */
	private SCMPStreamHttpUtil streamHttpUtil;

	/**
	 * Instantiates a new nio NioHttpConnection.
	 */
	public NioHttpConnection() {
		this.socketChannel = null;
		this.port = 0;
		this.host = null;
		this.numberOfThreads = 10;
		this.streamHttpUtil = new SCMPStreamHttpUtil();
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(true);
		socketChannel.connect(new InetSocketAddress(this.host, this.port));
		ConnectionPoint.getInstance().fireConnect(this, this.socketChannel.socket().getLocalPort());
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		socketChannel.close();
		ConnectionPoint.getInstance().fireDisconnect(this, this.socketChannel.socket().getLocalPort());
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
		streamHttpUtil.writeRequestSCMP(baos, inetSocketAddress.getHostName(), scmp);
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
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionPoint.getInstance().fireRead(this, this.socketChannel.socket().getLocalPort(), byteReadBuffer,
				0, bytesRead);
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getFrameDecoder(IConstants.HTTP);
		int httpFrameSize = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
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
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMPMessage ret = (SCMPMessage) streamHttpUtil.readSCMP(bais);
		bais.close();
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NioHttpConnection();
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
