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
package com.stabilit.sc.cln.net.client.nio.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.net.FrameDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.net.IFrameDecoder;
import com.stabilit.sc.net.SCMPStreamHttpUtil;
import com.stabilit.sc.net.nio.NioException;
import com.stabilit.sc.scmp.SCMP;

/**
 * The Class NioHttpClientConnection. Concrete client connection implementation on Nio base for Http.
 */
public class NioHttpClientConnection extends ClientConnectionAdapter {

	/** The socket channel. */
	private SocketChannel socketChannel = null;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The stream http util. */
	private SCMPStreamHttpUtil streamHttpUtil;

	/**
	 * Instantiates a new nio http client connection.
	 */
	public NioHttpClientConnection() {
		this.streamHttpUtil = new SCMPStreamHttpUtil();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.stabilit.sc.cln.client.ClientConnectionAdapter#setEncoderDecoder(com.stabilit.sc.net.IEncoderDecoder)
	 */
	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		super.setEncoderDecoder(encoderDecoder);
		this.streamHttpUtil.setEncoderDecoder(encoderDecoder);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClientConnection#connect()
	 */
	@Override
	public void connect() throws Exception {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(true);
		socketChannel.connect(new InetSocketAddress(this.host, this.port));
		ConnectionListenerSupport.getInstance().fireConnect(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClientConnection#disconnect()
	 */
	@Override
	public void disconnect() throws Exception {
		socketChannel.close();
		ConnectionListenerSupport.getInstance().fireDisconnect(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClientConnection#destroy()
	 */
	@Override
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClientConnection#sendAndReceive(com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
		streamHttpUtil.writeRequestSCMP(baos, inetSocketAddress.getHostName(), scmp);
		byte[] byteWriteBuffer = baos.toByteArray();
		ByteBuffer writeBuffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionListenerSupport.getInstance().fireWrite(this, byteWriteBuffer); // logs inside if registered
		socketChannel.write(writeBuffer);
		// read response
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb buffer
		int bytesRead = socketChannel.read(byteBuffer);
		if (bytesRead < 0) {
			throw new NioException("no bytes read");
		}
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionListenerSupport.getInstance().fireRead(this, byteReadBuffer, 0, bytesRead);
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getFrameDecoder(IConstants.HTTP);
		int httpFrameSize = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
		// continues reading until http frame is complete
		while (httpFrameSize > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			if (read < 0) {
				throw new NioException("read failed (<0)");
			}
			bytesRead += read;
			baos.write(byteBuffer.array(), 0, read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP ret = (SCMP) streamHttpUtil.readSCMP(bais);
		bais.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return new NioHttpClientConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setPort(int)
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
	}
}
