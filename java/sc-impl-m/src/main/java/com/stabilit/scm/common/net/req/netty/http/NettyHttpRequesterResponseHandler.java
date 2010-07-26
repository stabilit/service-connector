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
package com.stabilit.scm.common.net.req.netty.http;

import java.io.ByteArrayInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.CommunicationException;
import com.stabilit.scm.common.net.EncoderDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class NettyHttpROequesterResponseHandler.
 * 
 * @author JTraber
 */
@ChannelPipelineCoverage("one")
public class NettyHttpRequesterResponseHandler extends SimpleChannelUpstreamHandler {

	private ISCMPCallback scmpCallback;
	private volatile boolean pendingRequest;

	public NettyHttpRequesterResponseHandler() {
		this.scmpCallback = null;
		this.pendingRequest = false;
	}

	public void setCallback(ISCMPCallback scmpCallback) {
		this.scmpCallback = scmpCallback;
		this.pendingRequest = true;
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (this.pendingRequest) {
			this.pendingRequest = false;
			this.callback((HttpResponse) e.getMessage());
			return;
		}
		// message not expected - race condition
		LoggerPoint.getInstance().fireWarn(this, "message received but no reply was outstanding - race condition.");
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = (Throwable) e.getCause();
		if (this.pendingRequest) {
			this.pendingRequest = false;
			this.scmpCallback.callback(th);
			return;
		}
		if (th instanceof OperationTimeoutException) {
			// idle timed out no pending request outstanding - ignore exception
			return;
		}
		ExceptionPoint.getInstance().fireException(this, th);
	}

	private void callback(HttpResponse httpResponse) throws Exception {
		SCMPMessage ret = null;
		try {
			ChannelBuffer content = httpResponse.getContent();
			byte[] buffer = new byte[content.readableBytes()];
			content.readBytes(buffer);
			ConnectionPoint.getInstance().fireRead(this, -1, buffer, 0, buffer.length);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory()
					.newInstance(buffer);
			ret = (SCMPMessage) encoderDecoder.decode(bais);
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
			this.scmpCallback.callback(e);
			return;
		}
		this.scmpCallback.callback(ret);
	}
}