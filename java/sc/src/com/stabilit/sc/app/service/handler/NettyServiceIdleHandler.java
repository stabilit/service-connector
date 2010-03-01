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
package com.stabilit.sc.app.service.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

import com.stabilit.sc.app.server.handler.IKeepAliveHandler;



/**
 * @author JTraber
 * 
 */
public class NettyServiceIdleHandler extends IdleStateHandler {

	private IKeepAliveHandler callback;

	/**
	 * @param timer
	 * @param timeoutSeconds
	 */
	public NettyServiceIdleHandler(Timer timer, int timeoutSeconds, IKeepAliveHandler callback) {
		super(timer, 0, 0, timeoutSeconds);
		this.callback = callback;
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
			throws Exception {
		// TODO callback?? oder sowas
		super.channelIdle(ctx, state, lastActivityTimeMillis);
	}	
}
