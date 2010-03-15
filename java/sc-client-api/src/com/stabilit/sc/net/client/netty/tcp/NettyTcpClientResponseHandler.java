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
package com.stabilit.sc.net.client.netty.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.client.IConnectionListener;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;

@ChannelPipelineCoverage("one")
public class NettyTcpClientResponseHandler extends SimpleChannelUpstreamHandler {

	private final BlockingQueue<ChannelBuffer> answer = new LinkedBlockingQueue<ChannelBuffer>();

	private IConnectionListener callback = null;
	private boolean sync = false;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	/**
	 * @param scListener
	 * @param conn
	 */
	public NettyTcpClientResponseHandler() {
	}

	public void setCallback(IConnectionListener callback) {
		this.callback = callback;
	}

	public IConnectionListener getCallback() {
		return callback;
	}

	public ChannelBuffer getMessageSync() {
		sync = true;
		ChannelBuffer response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() wartet bis Message in Queue kommt!
				response = answer.take();
				sync = false;
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return response;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();

		answer.offer(chBuffer);

		// if (sync) {
		// answer.offer(chBuffer);
		// } else {
		// byte[] buffer = chBuffer.array();
		// ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		// SCMP ret = new SCMP();
		// encoderDecoder.decode(bais, ret);
		//
		// if (ret.getMessageId().equals("asyncCall")) {
		// try {
		// // checks if unsubscribe has been made.
		// if (((ISubscribe) conn).continueSubscriptionOnConnection()) {
		// NettyTcpResponse response = new NettyTcpResponse(e);
		//
		// SCMP req = new SCMP();
		// req.setMessageId(AsyncCallMessage.ID);
		// AsyncCallMessage async = new AsyncCallMessage();
		// req.setBody(async);
		// req.setSubsribeId(ret.getSubscribeId());
		// response.setSCMP(req);
		// ctx.getChannel().write(response.getBuffer());
		// this.callback.messageReceived(conn, ret);
		// }
		// } catch (ClassCastException cce) {
		// throw new SCMPException(
		// "Wrong message type (asyncCall) received, connection is not type of ISubscribe.");
		// }
		//
		// } else {
		// conn.setWritable(true);
		// this.callback.messageReceived(conn, ret);
		// }}

	}
}
