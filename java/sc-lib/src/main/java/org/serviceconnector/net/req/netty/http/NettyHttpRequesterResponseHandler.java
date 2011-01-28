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

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.Statistics;

/**
 * The Class NettyHttpROequesterResponseHandler.
 * 
 * @author JTraber
 */
public class NettyHttpRequesterResponseHandler extends SimpleChannelUpstreamHandler {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyHttpRequesterResponseHandler.class);
	/** The scmp callback. */
	private ISCMPMessageCallback scmpCallback;
	/** The pending request. */
	private volatile boolean pendingRequest;

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
	 * @param scmpCallback
	 *            the new callback
	 */
	public void setCallback(ISCMPMessageCallback scmpCallback) {
		this.scmpCallback = scmpCallback;
		this.pendingRequest = true;
	}

	/** {@inheritDoc} */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		if (this.pendingRequest) {
			this.pendingRequest = false;
			this.responseReceived((HttpResponse) e.getMessage());
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
				this.scmpCallback.receive(ex);
				return;
			}
			if (ex instanceof IdleTimeoutException) {
				// idle timed out no pending request outstanding - ignore
				// exception
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
	 * @param httpResponse
	 *            the http response
	 * @throws Exception
	 *             the exception
	 */
	private void responseReceived(HttpResponse httpResponse) throws Exception {
		SCMPMessage ret = null;
		try {
			ChannelBuffer content = httpResponse.getContent();
			byte[] buffer = new byte[content.readableBytes()];
			content.readBytes(buffer);
			Statistics.getInstance().incrementTotalMessages(buffer.length);
			if (ConnectionLogger.isEnabledFull()) {
				ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), "", -1, buffer, 0, buffer.length);
			}

			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
			ret = (SCMPMessage) encoderDecoder.decode(bais);
		} catch (Exception ex) {
			logger.warn("receive" + ex.toString());
			this.scmpCallback.receive(ex);
			return;
		}
		this.scmpCallback.receive(ret);
	}
}