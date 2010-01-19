/*
 * Copyright 2Red Hat, Inc.
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
package com.stabilit.netty.http.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.queue.Request;
import com.stabilit.sc.queue.RequestQueuer;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1$, $Date: 2010-01-17:31:+0(Fri, Jan 2010) $
 */
@ChannelPipelineCoverage("all")
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

	private boolean continueBit = false;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		continueBit = false;
		HttpRequest httpRequest = (HttpRequest) e.getMessage();
		byte[] buffer = httpRequest.getContent().array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
		if (obj instanceof Request) {
			Request request = (Request) obj;
			request.setHandler(this);
			RequestQueuer.getInstance().add(request);
			if (!continueBit)
				waitForResponse();
		}

		sendMessage(ctx.getChannel());
	}

	private void sendMessage(Channel channel) {
		Request request = new Request("server", "msg", 0, new Date(), null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectStreamHttpUtil.writeObjectOnly(baos, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpResponse httpResponse = new DefaultHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		byte[] buffer = baos.toByteArray();
		httpResponse.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		httpResponse.setContent(channelBuffer);
		channel.write(httpResponse);
	}

	public synchronized void continueWork() {
		continueBit = true;
		notify();
	}

	public synchronized void waitForResponse() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
