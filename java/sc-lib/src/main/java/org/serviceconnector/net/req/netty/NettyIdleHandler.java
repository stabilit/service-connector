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
package org.serviceconnector.net.req.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IIdleConnectionCallback;

/**
 * @author JTraber
 */
public class NettyIdleHandler extends IdleStateHandler {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(NettyIdleHandler.class);

	private ConnectionContext connectionContext;

	/**
	 * @param timer
	 * @param readerIdleTimeSeconds
	 * @param writerIdleTimeSeconds
	 * @param allIdleTimeSeconds
	 */
	public NettyIdleHandler(ConnectionContext connectionContext, Timer timer, int readerIdleTimeSeconds,
			int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		this.connectionContext = connectionContext;
	}

	/** {@inheritDoc} */
	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
			throws Exception {
		super.channelIdle(ctx, state, lastActivityTimeMillis);
		IConnection connection = this.connectionContext.getConnection();
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logKeepAlive(this.getClass().getSimpleName(), "", 0, this.connectionContext
					.getConnection().getNrOfIdlesInSequence());
		}
		IIdleConnectionCallback callback = this.connectionContext.getIdleCallback();
		callback.connectionIdle(connection);
	}
}