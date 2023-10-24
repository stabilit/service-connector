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
package org.serviceconnector.net.req.netty.http;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;

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

/**
 * The Class NettyHttpROequesterResponseHandler.
 *
 * @author JTraber
 */
public class NettyHttpRequesterResponseHandler extends ChannelInboundHandlerAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpRequesterResponseHandler.class);
	/** The scmp callback. */
	private ISCMPMessageCallback scmpCallback;
	/** The pending request. */
	private volatile Boolean pendingRequest;

	/**
	 * Instantiates a new netty http requester response handler.
	 */
	public NettyHttpRequesterResponseHandler() {
		this.scmpCallback = null;
		this.pendingRequest = false;
	}

	/**
	 * Sets the callback.
	 *
	 * @param scmpCallback the new callback
	 */
	public void setCallback(ISCMPMessageCallback scmpCallback) {
		this.scmpCallback = scmpCallback;
		this.pendingRequest = true;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (this.pendingRequest) {
			this.pendingRequest = false;
			FullHttpResponse httpResponse = (FullHttpResponse) msg;
			ByteBuf content = httpResponse.content();
			try {				
				byte[] buffer = new byte[content.readableBytes()];
				content.readBytes(buffer);
				Statistics.getInstance().incrementTotalMessages(buffer.length);
				if (ConnectionLogger.isEnabledFull()) {
					InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
					ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), remoteAddress.getHostName(), remoteAddress.getPort(), buffer, 0, buffer.length);
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
				SCMPMessage ret = (SCMPMessage) encoderDecoder.decode(bais);
				NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ret);
			} catch (Throwable th) {
				LOGGER.error("receive message", th);
				if ((th instanceof Exception) == true) {
					try {
						SCCallbackException ex = new SCCallbackException("exception raised in callback", th);
						NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ex);
					} catch (Throwable th1) {
						LOGGER.error("receive exception", th1);
					}
				}
			} finally {
				ReferenceCountUtil.release(content);				
			}
			return;
		}
		// unsolicited input, message not expected - race condition
		LOGGER.error("unsolicited input, message not expected, no reply was outstanding!");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		if (this.pendingRequest) {
			this.pendingRequest = false;
			LOGGER.warn("connection disconnect in pending request state, stop operation."); // regular
																							// disconnect
			if (ConnectionLogger.isEnabled()) {
				InetSocketAddress remoteSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
				ConnectionLogger.logDisconnectByRemoteHost(this.getClass().getSimpleName(), remoteSocketAddress.getHostName(), remoteSocketAddress.getPort());
			}
			DisconnectException ex = new DisconnectException("Connection disconnect, reply is outstanding. Operation stopped.");
			try {
				NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ex);
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
					NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ex);
				} catch (Throwable throwable) {
					LOGGER.error("receive exception", throwable);
				}
				return;
			}
			if (ex instanceof IdleTimeoutException) {
				// idle timed out no pending request outstanding - ignore
				// exception
				return;
			}
		}
		if (th instanceof java.io.IOException) {
			LOGGER.warn("regular disconnect", th); // regular disconnect causes this expected
			// exception
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
