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

import com.stabilit.sc.cln.net.TransportException;
import com.stabilit.sc.common.listener.ExceptionListenerSupport;

/**
 * @author JTraber
 * 
 */
public class NettyOperationListener implements ChannelFutureListener {

	private final BlockingQueue<ChannelFuture> answer = new LinkedBlockingQueue<ChannelFuture>();

	public ChannelFuture awaitUninterruptibly() throws TransportException {
		ChannelFuture response;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() wartet bis Message in Queue kommt!
				response = answer.take();
				if (response.isSuccess() == false) {
					throw new TransportException("Operation could not be completed");
				}
				break;
			} catch (InterruptedException e) {
				ExceptionListenerSupport.fireException(this, e);
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return response;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		answer.offer(future);
	}
}
