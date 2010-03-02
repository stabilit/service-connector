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
package com.stabilit.sc.app.client.netty.tcp;

import java.io.ByteArrayInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ISCClientListener;
import com.stabilit.sc.pool.IPoolConnection;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

@ChannelPipelineCoverage("one")
public class NettyClientTCPResponseHandler extends SimpleChannelUpstreamHandler {

	private final BlockingQueue<ChannelBuffer> answer = new LinkedBlockingQueue<ChannelBuffer>();

	private ISCClientListener callback = null;
	private IPoolConnection conn;

	/**
	 * @param scListener
	 * @param conn
	 */
	public NettyClientTCPResponseHandler(ISCClientListener scListener, IPoolConnection conn) {
		this.callback = scListener;
		this.conn = conn;
	}

	public void setCallback(ISCClientListener callback) {
		this.callback = callback;
	}

	public ISCClientListener getCallback() {
		return callback;
	}

	public ChannelBuffer getMessageSync() {
		ChannelBuffer response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() wartet bis Message in Queue kommt!
				response = answer.take();
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
		
		// run callback if any
		if (this.callback != null) {
			ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();
			byte[] buffer = chBuffer.array();
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
			if (obj instanceof SCMP) {
				SCMP ret = (SCMP) obj;
				this.callback.messageReceived(conn, ret);
				return;
			}
		}
		answer.offer((ChannelBuffer) e.getMessage());
	}
}
