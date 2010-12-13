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

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.Statistics;

/**
 * The Class NettyTcpRequesterResponseHandler.
 * 
 * @author JTraber
 */
public class NettyTcpRequesterResponseHandler extends SimpleChannelUpstreamHandler {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyTcpRequesterResponseHandler.class);
	/** The scmp callback. */
	private ISCMPCallback scmpCallback;
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
	 * @param callback
	 *            the new callback
	 */
	public void setCallback(ISCMPCallback callback) {
		this.scmpCallback = callback;
		this.pendingRequest = true;
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (this.pendingRequest) {
			this.pendingRequest = false;
			this.responseReceived((ChannelBuffer) e.getMessage());
			return;
		}
		// unsolicited input, message not expected - race condition
		logger.error("unsolicited input, message not expected, no reply was outstanding!");
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		if (th instanceof Exception) {
			Exception ex = (Exception) th;
			if (this.pendingRequest) {
				this.pendingRequest = false;
				this.scmpCallback.callback(ex);
				return;
			}
			if (ex instanceof IdleTimeoutException) {
				// idle timed out no pending request outstanding - ignore exception
				return;
			}
		}
		if (th instanceof java.io.IOException) {
			logger.warn(th.toString());	// regular disconnect causes this expected exception
		}
		else {
			logger.error("Response error",th);
		}
	}

	/**
	 * Response received.
	 * 
	 * @param channelBuffer
	 *            the channel buffer
	 * @throws Exception
	 *             the exception
	 */
	private void responseReceived(ChannelBuffer channelBuffer) throws Exception {
		SCMPMessage ret = null;
		try {
			byte[] buffer = new byte[channelBuffer.readableBytes()];
			channelBuffer.readBytes(buffer);
			Statistics.getInstance().incrementTotalMessages(buffer.length);
			if (ConnectionLogger.isEnabledFull()) {
				ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), "", -1, buffer, 0, buffer.length);
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
			ret = (SCMPMessage) encoderDecoder.decode(bais);
		} catch (Exception ex) {
			logger.warn("receive" + ex.toString());
			this.scmpCallback.callback(ex);
			return;
		}
		this.scmpCallback.callback(ret);
	}
}