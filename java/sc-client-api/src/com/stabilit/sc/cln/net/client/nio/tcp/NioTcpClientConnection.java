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
package com.stabilit.sc.cln.net.client.nio.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.cln.client.ClientConnectionAdapter;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.listener.ConnectionEvent;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.net.FrameDecoderFactory;
import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.net.nio.NioTcpException;

public class NioTcpClientConnection extends ClientConnectionAdapter {

	private SocketChannel socketChannel = null;
	private int port;
	private String host;

	public NioTcpClientConnection() {
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
		encoderDecoder.encode(baos, scmp);
		byte[] byteWriteBuffer = baos.toByteArray();
		ByteBuffer writeBuffer = ByteBuffer.wrap(byteWriteBuffer);
		if (ConnectionListenerSupport.getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(this,
					byteWriteBuffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
		socketChannel.write(writeBuffer);
		// read response
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb
		int bytesRead = socketChannel.read(byteBuffer);
		if (bytesRead < 0) {
			throw new NioTcpException("no bytes read");
		}
		byte[] byteReadBuffer = byteBuffer.array();
		if (ConnectionListenerSupport.getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(this,
					byteReadBuffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}		
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory
				.getDefaultFrameDecoder();
		// warning, returns always the same instance, singleton
		int scmpLengthHeadlineInc = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array());
		while (scmpLengthHeadlineInc > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
			bytesRead += read;
			baos.write(byteBuffer.array());
		}
		baos.flush();
		byte[] buffer = baos.toByteArray();
		encoderDecoder = EncoderDecoderFactory.newInstance(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP ret = (SCMP) encoderDecoder.decode(bais);
		return ret;
	}

	@Override
	public IFactoryable newInstance() {
		return new NioTcpClientConnection();
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
