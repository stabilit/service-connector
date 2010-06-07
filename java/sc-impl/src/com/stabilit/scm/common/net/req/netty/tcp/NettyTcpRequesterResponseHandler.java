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
package com.stabilit.scm.common.net.req.netty.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.scm.cln.net.CommunicationException;
import com.stabilit.scm.common.net.netty.NettyEvent;
import com.stabilit.scm.common.net.netty.NettyExceptionEvent;
import com.stabilit.scm.listener.ExceptionPoint;

/**
 * The Class NettyTcpRequesterResponseHandler. Used to wait until operation us successfully done by netty framework.
 * BlockingQueue is used for synchronization and waiting mechanism. Communication Exception is thrown when
 * operation fails.
 */
@ChannelPipelineCoverage("one")
public class NettyTcpRequesterResponseHandler extends SimpleChannelUpstreamHandler {

	/** Queue to store the answer. */
	private final BlockingQueue<NettyEvent> answer = new LinkedBlockingQueue<NettyEvent>();

	/**
	 * The Constructor.
	 */
	public NettyTcpRequesterResponseHandler() {
	}

	/**
	 * Gets the message synchronously.
	 * 
	 * @return the message
	 * @throws CommunicationException
	 *             the communication exception
	 */
	ChannelBuffer getMessageSync() throws CommunicationException {
		NettyEvent response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() waits until message arrives in queue, locking inside queue
				response = answer.take();
				if (response.isFault()) {
					throw new CommunicationException(((NettyExceptionEvent) response).getResponse().getCause());
				}
				break;
			} catch (InterruptedException e) {
				ExceptionPoint.getInstance().fireException(this, e);
				interrupted = true;
			}
		}

		if (interrupted) {
			// interruption happens when waiting for response - interrupt now
			Thread.currentThread().interrupt();
		}
		return (ChannelBuffer) response.getResponse();
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();
		NettyTcpEvent response = new NettyTcpEvent(chBuffer);
		answer.offer(response);
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = (Throwable) e.getCause();
		NettyEvent response = new NettyExceptionEvent(th);
		answer.offer(response);
	}
}
