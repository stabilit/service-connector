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
package com.stabilit.sc.common.net.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.ctx.RequestContext;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.net.FrameDecoderFactory;
import com.stabilit.sc.common.net.IFrameDecoder;
import com.stabilit.sc.common.scmp.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.IEncoderDecoder;
import com.stabilit.sc.common.scmp.RequestAdapter;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.util.MapBean;

public class NioTcpRequest extends RequestAdapter {

	private SocketChannel socketChannel;
	private IEncoderDecoder encoderDecoder;

	public NioTcpRequest(SocketChannel socketChannel) {
		this.mapBean = new MapBean<Object>();
		this.socketChannel = socketChannel;
		this.socketAddress = socketChannel.socket().getLocalSocketAddress();
		this.scmp = null;
		this.requestContext = new RequestContext(socketChannel.socket().getRemoteSocketAddress());
	}

	@Override
	public void load() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 << 12); // 8kb
		int bytesRead = socketChannel.read(byteBuffer);
		if (bytesRead < 0) {
			throw new NioTcpDisconnectException("line disconnected");
		}
		// parse headline
		IFrameDecoder scmpFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();
		// warning, returns always the same instance, singleton
		byte[] byteReadBuffer = byteBuffer.array();
		ConnectionListenerSupport.fireRead(this, byteReadBuffer, 0, bytesRead); // logs inside if registered
		int scmpLengthHeadlineInc = scmpFrameDecoder.parseFrameSize(byteReadBuffer);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(byteBuffer.array(), 0, bytesRead);
		while (scmpLengthHeadlineInc > bytesRead) {
			byteBuffer.clear();
			int read = socketChannel.read(byteBuffer);
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
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
		bais.close();
		this.scmp = scmp;
	}
}
