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
package org.serviceconnector.net.req.netty;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.serviceconnector.net.CommunicationException;


/**
 * The Class NettyOperationListener. Used to wait until operation us successfully done by netty framework. BlockingQueue
 * is used for synchronization and waiting mechanism. Communication Exception is thrown when operation fails.
 * BlockingQueue only holds newest answer.
 * 
 * @author JTraber
 */
public class NettyOperationListener implements ChannelFutureListener {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyOperationListener.class);
	
	/** Queue to store the answer. */
	private final BlockingQueue<ChannelFuture> answer = new ArrayBlockingQueue<ChannelFuture>(1);

	/**
	 * Await unInterruptibly until operation is completed or time runs out.
	 * 
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 * @return the channel future
	 * @throws Exception
	 *             the exception
	 */
	public ChannelFuture awaitUninterruptibly(long timeoutMillis) throws Exception {
		ChannelFuture response;
		// poll() waits until message arrives in queue or time runs out
		response = this.answer.poll(timeoutMillis, TimeUnit.MILLISECONDS);
		if (response == null || response.isSuccess() == false) {
			throw new CommunicationException("Operation could not be completed");
		}
		return response;
	}

	/** {@inheritDoc} */
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (this.answer.offer(future)) {
			// queue empty object can be added
			return;
		}
		// object could not be added - clear queue and offer again
		this.answer.clear();
		this.answer.offer(future);
	}
}
