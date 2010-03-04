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
package com.stabilit.sc.app.server.http.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.Timer;

import com.stabilit.sc.msg.IClientListener;


/**
 * @author JTraber
 * 
 */
public class NettyWriteTimeoutHandler extends WriteTimeoutHandler {

	private IClientListener callback;

	/**
	 * @param timer
	 * @param timeoutSeconds
	 */
	public NettyWriteTimeoutHandler(Timer timer, int timeoutSeconds, IClientListener callback) {
		super(timer, timeoutSeconds);
		this.callback = callback;
	}

	@Override
	protected void writeTimedOut(ChannelHandlerContext ctx) throws Exception {
		super.writeTimedOut(ctx);
	}
}
