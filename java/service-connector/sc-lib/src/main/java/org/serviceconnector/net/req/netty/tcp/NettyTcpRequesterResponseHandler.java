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
package org.serviceconnector.net.req.netty.tcp;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.connection.DisconnectException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCCallbackException;
import org.serviceconnector.util.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * The Class NettyTcpRequesterResponseHandler.
 *
 * @author JTraber
 */
public class NettyTcpRequesterResponseHandler extends ChannelInboundHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpRequesterResponseHandler.class);
	/** The scmp callback. */
	private ISCMPMessageCallback scmpCallback;
	/** The pending request. */
	private volatile boolean pendingRequest;

	/**
	 * Instantiates a new netty tcp requester response handler.
	 */
	public NettyTcpRequesterResponseHandler() {
		this.scmpCallback = null;
		this.pendingRequest = false;
	}

	/**
	 * Sets the callback.
	 *
	 * @param callback the new callback
	 */
	public void setCallback(ISCMPMessageCallback callback) {
		this.scmpCallback = callback;
		this.pendingRequest = true;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (this.pendingRequest) {
			this.pendingRequest = false;
			SCMPMessage ret = null;
			try {
				byte[] buffer = (byte[]) msg;
				Statistics.getInstance().incrementTotalMessages(buffer.length);
				if (ConnectionLogger.isEnabledFull()) {
					InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
					ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), remoteAddress.getHostName(), remoteAddress.getPort(), buffer, 0, buffer.length);
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
				ret = (SCMPMessage) encoderDecoder.decode(bais);
				NettyTcpRequesterResponseHandler.this.scmpCallback.receive(ret);
			} catch (Throwable th) {
				LOGGER.error("receive message", th);
				if ((th instanceof Exception) == false) {
					try {
						SCCallbackException ex = new SCCallbackException("exception raised in callback", th);
						NettyTcpRequesterResponseHandler.this.scmpCallback.receive(ex);
					} catch (Throwable th1) {
						LOGGER.error("receive exception", th1);
					}
				}
			}
			return;
		}
		// unsolicited input, message not expected - race condition
		LOGGER.error("unsolicited input, message not expected, no reply was outstanding!");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (this.pendingRequest) {
			this.pendingRequest = false;
			LOGGER.warn("connection disconnect in pending request state, stop operation."); // regular disconnect
			if (ConnectionLogger.isEnabled()) {
				InetSocketAddress remoteSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
				ConnectionLogger.logDisconnectByRemoteHost(this.getClass().getSimpleName(), remoteSocketAddress.getHostName(), remoteSocketAddress.getPort());
			}
			DisconnectException ex = new DisconnectException("Connection disconnect, reply is outstanding. Operation stopped.");
			try {
				NettyTcpRequesterResponseHandler.this.scmpCallback.receive(ex);
			} catch (Throwable throwable) {
				LOGGER.error("receive exception", throwable);
			}
			return;
		}
		if (ConnectionLogger.isEnabled()) {
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
			ConnectionLogger.logDisconnectByRemoteHost(this.getClass().getSimpleName(), remoteSocketAddress.getHostName(), remoteSocketAddress.getPort());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable th) throws Exception {
		if (th instanceof Exception) {
			Exception ex = (Exception) th;
			if (this.pendingRequest) {
				this.pendingRequest = false;
				LOGGER.warn("connection exception in pending request state, stop operation. " + ex.toString());
				try {
					NettyTcpRequesterResponseHandler.this.scmpCallback.receive(ex);
				} catch (Throwable throwable) {
					LOGGER.error("receive exception", throwable);
				}
				return;
			}
			if (ex instanceof IdleTimeoutException) {
				// idle timed out no pending request outstanding - ignore exception
				return;
			}
		}
		if (th instanceof java.io.IOException) {
			LOGGER.warn("regular disconnect", th); // regular disconnect causes this expected exception
		} else {
			LOGGER.error("Response error", th);
		}
	}

	/**
	 * Connection disconnect. Method gets called when connection got disconnected for some reason. This avoids receiving messages in disconnect procedure.
	 */
	public void connectionDisconnect() {
		this.pendingRequest = false;
	}
}
