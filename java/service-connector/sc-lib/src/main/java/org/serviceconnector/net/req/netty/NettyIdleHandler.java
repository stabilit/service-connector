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

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IIdleConnectionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author JTraber
 */
public class NettyIdleHandler extends IdleStateHandler {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyIdleHandler.class);

	/** The connection context. */
	private ConnectionContext connectionContext;

	/**
	 * Instantiates a new netty idle handler.
	 *
	 * @param connectionContext the connection context
	 * @param timer the timer
	 * @param readerIdleTimeSeconds the reader idle time seconds
	 * @param writerIdleTimeSeconds the writer idle time seconds
	 * @param allIdleTimeSeconds the all idle time seconds
	 */
	public NettyIdleHandler(ConnectionContext connectionContext, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		this.connectionContext = connectionContext;
	}
	
	
	/** {@inheritDoc} */
	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		super.channelIdle(ctx, evt);
		IConnection connection = this.connectionContext.getConnection();
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logKeepAlive(this.getClass().getSimpleName(), connection.getHost(), 0, this.connectionContext.getConnection().getNrOfIdlesInSequence());
		}
		IIdleConnectionCallback callback = this.connectionContext.getIdleCallback();
		AppContext.getSCWorkerThreadPool().execute((callback));
	}
}
