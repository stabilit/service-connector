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
package com.stabilit.sc.app.client.netty.http;

import java.io.ByteArrayInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.msg.impl.UnSubscribeMessage;

@ChannelPipelineCoverage("one")
public class NettyHttpClientResponseHandler_old extends SimpleChannelUpstreamHandler {

	private final BlockingQueue<HttpResponse> answer = new LinkedBlockingQueue<HttpResponse>();

	private IClientListener callback = null;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	public void setCallback(IClientListener callback) {
		this.callback = callback;
	}

	public IClientListener getCallback() {
		return callback;
	}

	public HttpResponse getMessageSync() {
		HttpResponse responseMessage;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() wartet bis Message in Queue kommt!
				responseMessage = answer.take();
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return responseMessage;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// run callback if any
		if (this.callback != null) {
			HttpResponse httpResponse = (HttpResponse) e.getMessage();
			byte[] buffer = httpResponse.getContent().array();
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			SCMP ret = new SCMP();
			encoderDecoder.decode(bais, ret);
			
			// check for subscribe id
			String subscribeId = ret.getSubscribeId();
			if (subscribeId != null) {
				if (subscribeId.equals(this.callback.getSubscribeId())) {
					if (UnSubscribeMessage.ID.equals(ret.getMessageId())) {
						this.callback = null;
						return;
					}
					// this.callback.messageReceived(ret);
					return;
				}
			}

		}
		answer.offer((HttpResponse) e.getMessage());
	}
}
