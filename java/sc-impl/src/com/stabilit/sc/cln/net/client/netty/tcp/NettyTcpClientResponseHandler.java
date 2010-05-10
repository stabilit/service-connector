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
package com.stabilit.sc.cln.net.client.netty.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.cln.net.client.netty.NettyExceptionResponse;
import com.stabilit.sc.cln.net.client.netty.NettyResponse;
import com.stabilit.sc.listener.ExceptionListenerSupport;

/**
 * The Class NettyTcpClientResponseHandler. Used to wait until operation us successfully done by netty framework.
 * BlockingQueue is used for synchronization and waiting mechanism. Communication Exception is thrown when
 * operation fails.
 */
@ChannelPipelineCoverage("one")
public class NettyTcpClientResponseHandler extends SimpleChannelUpstreamHandler {

	/** Queue to store the answer. */
	private final BlockingQueue<NettyResponse> answer = new LinkedBlockingQueue<NettyResponse>();

	/**
	 * The Constructor.
	 */
	public NettyTcpClientResponseHandler() {
	}

	/**
	 * Gets the message synchronously.
	 * 
	 * @return the message
	 * @throws CommunicationException
	 *             the communication exception
	 */
	ChannelBuffer getMessageSync() throws CommunicationException {
		NettyResponse response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() waits until message arrives in queue, locking inside queue
				response = answer.take();
				if (response.isFault()) {
					throw new CommunicationException(((NettyExceptionResponse) response).getFault().getCause());
				}
				break;
			} catch (InterruptedException e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				interrupted = true;
			}
		}

		if (interrupted) {
			// interruption happens when waiting for response - interrupt now
			Thread.currentThread().interrupt();
		}
		return response.getBuffer();
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.
	 * ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();
		NettyResponse response = new NettyResponse(chBuffer);
		answer.offer(response);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.
	 * ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = (Throwable) e.getCause();
		NettyResponse response = new NettyExceptionResponse(th);
		answer.offer(response);
	}
}
