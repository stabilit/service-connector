/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.stabilit.sc.cln.net.client.nio.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.net.FrameDecoderFactory;
import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.net.nio.NioTcpException;
import com.stabilit.sc.common.util.SCMPStreamHttpUtil;

public class NioHttpClientConnection extends ClientConnectionAdapter {

	private SocketChannel socketChannel = null;
	private int port;
	private String host;
	private SCMPStreamHttpUtil streamHttpUtil;

	public NioHttpClientConnection() {
		this.streamHttpUtil = new SCMPStreamHttpUtil();
	}

	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		super.setEncoderDecoder(encoderDecoder);
		this.streamHttpUtil.setEncoderDecoder(encoderDecoder);
	}

	@Override
	public void connect() throws Exception {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(true);
		socketChannel.connect(new InetSocketAddress(this.host, this.port));
	}

	@Override
	public void disconnect() throws Exception {
		socketChannel.close();
	}

	@Override
	public void destroy() {
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.socket()
				.getRemoteSocketAddress();
		streamHttpUtil.writeRequestSCMP(baos, inetSocketAddress.getHostName(), scmp);
		byte[]byteWriteBuffer = baos.toByteArray();
		ByteBuffer writeBuffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionListenerSupport.fireWrite(this, byteWriteBuffer);  // logs inside if registered
		socketChannel.write(writeBuffer);
		// read response
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb
		int bytesRead = socketChannel.read(byteBuffer);
		if (bytesRead < 0) {
			throw new NioTcpException("no bytes read");
		}
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionListenerSupport.fireRead(this, byteReadBuffer, 0, bytesRead);  // logs inside if registered
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getFrameDecoder("http");
		int httpFrameSize = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(),0,bytesRead);
		while (httpFrameSize > bytesRead) {
			byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb
			int read = socketChannel.read(byteBuffer);
			if (read < 0) {
				throw new IOException("read failed (<0)");
			}
			bytesRead += read;
			baos.write(byteBuffer.array(),0,read);
		}
		baos.close();
		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP ret = (SCMP) streamHttpUtil.readSCMP(bais);
		bais.close();
		return ret;
	}

	@Override
	public IFactoryable newInstance() {
		return new NioHttpClientConnection();
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}
}
