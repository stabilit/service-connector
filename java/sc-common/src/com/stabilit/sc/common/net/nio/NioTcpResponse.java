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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.scmp.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.IEncoderDecoder;
import com.stabilit.sc.common.scmp.ResponseAdapter;
import com.stabilit.sc.common.scmp.SCMP;

public class NioTcpResponse extends ResponseAdapter {

	private SocketChannel socketChannel;
	private IEncoderDecoder encoderDecoder;

	public NioTcpResponse(SocketChannel socketChannel) {
		this.scmp = null;
		this.socketChannel = socketChannel;
	}

	public byte[] getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (this.encoderDecoder == null) {
		    this.encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(this.scmp);
		}
		encoderDecoder.encode(baos, scmp);
		baos.close();
		byte[] buf = baos.toByteArray();
		return buf;
	}

	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.encoderDecoder = encoderDecoder;
	}

	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		this.scmp = scmp;
	}

	@Override
	public void write() throws Exception {
		byte[] byteWriteBuffer = this.getBuffer();
		ByteBuffer buffer = ByteBuffer.wrap(byteWriteBuffer);
		ConnectionListenerSupport.fireWrite(this, byteWriteBuffer);  // logs inside if registered
		this.socketChannel.write(buffer);
	}
}
