/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
*/
/**
 * 
 */
package com.stabilit.sc.net.client.netty.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * @author JTraber
 *
 */
public class TCPClientConnectListener implements ChannelFutureListener {
	
	private final BlockingQueue<ChannelFuture> answer = new LinkedBlockingQueue<ChannelFuture>();
	
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		answer.offer(future);
	}
	
	public ChannelFuture getOperationCompleteEventSync() {
		ChannelFuture responseFuture;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() waits until first message gets in queue!
				responseFuture = answer.take();
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return responseFuture;
	}
}
