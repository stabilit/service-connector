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
import org.serviceconnector.service.SCCallbackException;
import org.serviceconnector.util.Statistics;

/**
 * The Class NettyHttpROequesterResponseHandler.
 * 
 * @author JTraber
 */
public class NettyHttpRequesterResponseHandler extends SimpleChannelUpstreamHandler {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(NettyHttpRequesterResponseHandler.class);
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
			// set up responderRequestHandlerTask to take care of the request
			NettyHttpRequesterResponseHandlerTask responseHandlerTask = new NettyHttpRequesterResponseHandlerTask((HttpResponse) e
					.getMessage());
			AppContext.getExecutor().submit(responseHandlerTask);
			return;
		}
		// unsolicited input, message not expected - race condition
		LOGGER.error("unsolicited input, message not expected, no reply was outstanding!");
	}

	/** {@inheritDoc} */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable th = e.getCause();
		if (th instanceof Exception) {
			Exception ex = (Exception) th;
			if (this.pendingRequest) {
				this.pendingRequest = false;
				NettyHttpRequesterErrorHandlerTask errorHandler = new NettyHttpRequesterErrorHandlerTask(ex);
				AppContext.getExecutor().submit(errorHandler);
				return;
			}
			if (ex instanceof IdleTimeoutException) {
				// idle timed out no pending request outstanding - ignore
				// exception
				return;
			}
		}
		if (th instanceof java.io.IOException) {
			LOGGER.warn(th); // regular disconnect causes this expected exception
		} else {
			LOGGER.error("Response error", th);
		}
	}

	/**
	 * The Class NettyHttpRequesterResponseHandlerTask. Is responsible for processing a response. It has to be a new thread because
	 * of NETTY threading concept.
	 * A worker thread owns a channel pipeline. If block the thread nothing will be sent on that channel.
	 * More information about this issue: http://www.jboss.org/netty/community.html#nabble-td5441049
	 */
	private class NettyHttpRequesterResponseHandlerTask implements Runnable {

		private HttpResponse httpResponse;

		public NettyHttpRequesterResponseHandlerTask(HttpResponse httpResponse) {
			this.httpResponse = httpResponse;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
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
				NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ret);
			} catch (Throwable th) {
				LOGGER.error("receive message", th);
				if ((th instanceof Exception) == true) {
					try {
						SCCallbackException ex = new SCCallbackException("exception raised in callback", th);
						NettyHttpRequesterResponseHandler.this.scmpCallback.receive(ex);
					} catch (Throwable th1) {
						LOGGER.error("receive exception", th);
					}
				}
			}
		}
	}

	private class NettyHttpRequesterErrorHandlerTask implements Runnable {

		private Exception exception;

		public NettyHttpRequesterErrorHandlerTask(Exception exception) {
			this.exception = exception;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			LOGGER.error("receive exception", exception);
			try {
				NettyHttpRequesterResponseHandler.this.scmpCallback.receive(exception);
			} catch (Throwable th) {
				LOGGER.error("receive exception", th);
			}
		}
	}
}