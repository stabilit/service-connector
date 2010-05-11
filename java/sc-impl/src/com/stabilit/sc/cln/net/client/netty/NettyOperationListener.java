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
package com.stabilit.sc.cln.net.client.netty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.listener.ExceptionListenerSupport;

/**
 * The Class NettyOperationListener. Used to wait until operation us successfully done by netty framework.
 * BlockingQueue is used for synchronization and waiting mechanism. Communication Exception is thrown when
 * operation fails.
 * 
 * @author JTraber
 */
public class NettyOperationListener implements ChannelFutureListener {

	/** Queue to store the answer. */
	private final BlockingQueue<ChannelFuture> answer = new LinkedBlockingQueue<ChannelFuture>();

	/**
	 * Await uninterruptibly until operation is completed.
	 * 
	 * @return the channel future
	 * @throws CommunicationException
	 *             the communication exception
	 */
	public ChannelFuture awaitUninterruptibly() throws CommunicationException {
		ChannelFuture response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() waits until message arrives in queue, locking inside queue
				response = answer.take();
				if (response.isSuccess() == false) {
					throw new CommunicationException("Operation could not be completed", response.getCause());
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
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
	 */
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		answer.offer(future);
	}
}
