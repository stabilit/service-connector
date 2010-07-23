/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.net.req.netty;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

/**
 * @author JTraber
 */
public class NettyOperationTimeoutHandler extends IdleStateHandler {

	/**
	 * @param timer
	 * @param readerIdleTime
	 * @param writerIdleTime
	 * @param allIdleTime
	 * @param unit
	 */
	public NettyOperationTimeoutHandler(Timer timer, long readerIdleTime, long writerIdleTime, long allIdleTime,
			TimeUnit unit) {
		super(timer, readerIdleTime, writerIdleTime, allIdleTime, unit);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
			throws Exception {
		super.channelIdle(ctx, state, lastActivityTimeMillis);
		switch (state) {
		case WRITER_IDLE:
			return;
		case READER_IDLE:
		case ALL_IDLE:
			Channels.fireExceptionCaught(ctx, new OperationTimeoutException());
		default:
			Channels.fireExceptionCaught(ctx, new OperationTimeoutException());
			break;
		}
	}
}
