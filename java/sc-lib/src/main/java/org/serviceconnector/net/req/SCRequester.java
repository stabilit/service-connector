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
package org.serviceconnector.net.req;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.connection.DisconnectException;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class Requester. Implements a general behavior of a requester in the context of the Client or the Server. Defines how to
 * connect/disconnect, send/receive has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCRequester.class);
	/** The context. */
	private RemoteNodeConfiguration remoteNodeConfiguration;
	/** The msg sequence nr. */
	private SCMPMessageSequenceNr msgSequenceNr;
	/** The connection pool. */
	private ConnectionPool connectionPool = null;

	/**
	 * Instantiates a new requester.
	 * 
	 * @param remoteNodeConfiguration
	 *            the remote node configuration
	 */
	public SCRequester(RemoteNodeConfiguration remoteNodeConfiguration, int keepAliveTimeoutMillis) {
		this.remoteNodeConfiguration = remoteNodeConfiguration;
		this.connectionPool = new ConnectionPool(remoteNodeConfiguration.getHost(), remoteNodeConfiguration.getPort(),
				remoteNodeConfiguration.getConnectionType(), remoteNodeConfiguration.getKeepAliveIntervalSeconds(),
				keepAliveTimeoutMillis);
		this.connectionPool.setMaxConnections(remoteNodeConfiguration.getMaxPoolSize());
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void send(SCMPMessage message, int timeoutMillis, ISCMPMessageCallback scmpCallback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.connectionPool.getConnection();
		ConnectionContext connectionContext = connection.getContext();

		try {
			ISCMPMessageCallback requesterCallback = null;
			// differ if message is large or not, sending procedure is different
			if (message.isLargeMessage()) {
				// SCMPLargeRequest handles splitting, works like an iterator
				SCMPCompositeSender largeResponse = new SCMPCompositeSender(message);
				requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, largeResponse,
						msgSequenceNr);
				// setting up operation timeout after successful send
				TimeoutWrapper timeoutWrapper = new TimeoutWrapper((ITimeout) requesterCallback);
				SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
				ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) AppContext.otiScheduler.schedule(
						timeoutWrapper, (long) timeoutMillis, TimeUnit.MILLISECONDS);
				reqCallback.setOperationTimeout(timeout);
				reqCallback.setTimeoutMillis(timeoutMillis);
				// extract first part message & send
				SCMPMessage part = largeResponse.getFirst();
				// handling msgSequenceNr
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					part.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				// send
				connection.send(part, requesterCallback);
			} else {
				requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, msgSequenceNr);
				// setting up operation timeout after successful send
				TimeoutWrapper timeoutWrapper = new TimeoutWrapper((ITimeout) requesterCallback);
				SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
				ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) AppContext.otiScheduler.schedule(
						timeoutWrapper, (long) timeoutMillis, TimeUnit.MILLISECONDS);
				reqCallback.setOperationTimeout(timeout);
				reqCallback.setTimeoutMillis(timeoutMillis);
				// handling msgSequenceNr
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				// process send
				connection.send(message, requesterCallback);
			}
		} catch (Exception ex) {
			this.connectionPool.freeConnection(connection);
			throw ex;
		}
	}

	/** {@inheritDoc} */
	@Override
	public RemoteNodeConfiguration getRemoteNodeConfiguration() {
		return this.remoteNodeConfiguration;
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.connectionPool.destroy();
	}

	/**
	 * The Class RequesterSCMPCallback. Component used for asynchronous communication. It gets informed at the time a reply is
	 * received. Handles freeing up earlier requested connections. Provides functionality to deal with large messages.
	 */
	private class SCRequesterSCMPCallback implements ISCMPMessageCallback, ITimeout {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPMessageCallback scmpCallback;
		/** The connection context. */
		private ConnectionContext connectionCtx;
		/** The request message, initial message sent by requester. */
		private SCMPMessage requestMsg;
		/** The large response. */
		private SCMPCompositeReceiver largeResponse;
		/** The large request. */
		private SCMPCompositeSender largeRequest;
		/** The msgSequenceNr. */
		private SCMPMessageSequenceNr msgSequenceNr;
		/** The operation timeout. */
		private ScheduledFuture<TimeoutWrapper> operationTimeout;
		/** The timeout in milliseconds. */
		private int timeoutMillis;

		/**
		 * Instantiates a new sC requester scmp callback.
		 * 
		 * @param reqMsg
		 *            the req msg
		 * @param scmpCallback
		 *            the scmp callback
		 * @param conCtx
		 *            the con ctx
		 * @param msgSequenceNr
		 *            the msg sequence nr
		 */
		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPMessageCallback scmpCallback, ConnectionContext conCtx,
				SCMPMessageSequenceNr msgSequenceNr) {
			this(reqMsg, scmpCallback, conCtx, null, msgSequenceNr);
		}

		/**
		 * Instantiates a new sC requester scmp callback.
		 * 
		 * @param reqMsg
		 *            the req msg
		 * @param scmpCallback
		 *            the scmp callback
		 * @param conCtx
		 *            the con ctx
		 * @param largeRequest
		 *            the large request
		 * @param msgSequenceNr
		 *            the msg sequence nr
		 */
		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPMessageCallback scmpCallback, ConnectionContext conCtx,
				SCMPCompositeSender largeRequest, SCMPMessageSequenceNr msgSequenceNr) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = conCtx;
			this.requestMsg = reqMsg;
			this.largeRequest = largeRequest;
			this.msgSequenceNr = msgSequenceNr;
			this.timeoutMillis = 0;
			this.operationTimeout = null;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMPMessage scmpReply) throws Exception {
			if (scmpReply.isFault()) {
				// reset large response/request if any in process
				this.largeResponse = null;
				this.largeRequest = null;
			}
			// ------------------- handling large request --------------------
			if (largeRequest != null) {
				// handle large messages
				boolean largeRequestDone = this.handlingLargeRequest(scmpReply);

				if (largeRequestDone == false) {
					// large request is not done yet - wait for other PAC messages
					return;
				}

				this.largeRequest = null;
				if (scmpReply.isPart() && this.requestMsg.isGroup() == false) {
					// response is a part - response is large, continue polling
					LOGGER.debug("sc requester callback scmpReply cache id = " + scmpReply.getCacheId());
					this.handlingLargeResponse(scmpReply);
					return;
				}
				// cancel operation timeout
				operationTimeout.cancel(false);
				// first handle connection - that user has a connection to work, if he has only 1
				this.freeConnection();
				this.scmpCallback.receive(scmpReply);
				return;
			}

			// ------------------- handling large response -------------------
			if (this.largeResponse != null) {
				LOGGER.debug("sc requester callback large response cache id = " + scmpReply.getCacheId());
				// large response message is processing - continue procedure
				this.largeResponse.add(scmpReply);
				if (scmpReply.isPart() == false) {
					// response received
					// cancel operation timeout
					operationTimeout.cancel(false);
					// first handle connection - that user has a connection to work, if he has only 1
					this.freeConnection();
					this.scmpCallback.receive(this.largeResponse);
					// delete compositeReceiver - large response done!
					this.largeResponse = null;
					return;
				}
				SCMPMessage message = largeResponse.getPart();
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					// increment msgSequenceNr
					this.msgSequenceNr.incrementAndGetMsgSequenceNr();
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				// updating cache part number for poll request
				Integer partNr = scmpReply.getHeaderInt(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);
				if (partNr == null) {
					partNr = 1;
				}
				message.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, partNr);
				LOGGER.debug("handling large response using cache id = " + message.getCacheId());
				// poll & exit
				this.connectionCtx.getConnection().send(message, this);
				return;
			}

			if (requestMsg.isPart()) {
				// incoming message is a part groupCall is made by client - part response can be ignored
				// cancel operation timeout
				operationTimeout.cancel(false);
				// first handle connection - that user has a connection to work, if he has only 1
				this.freeConnection();
				this.scmpCallback.receive(scmpReply);
				return;
			}

			if (scmpReply.isPart()) {
				// handling large response
				this.handlingLargeResponse(scmpReply);
				return;
			}
			// cancel operation timeout
			operationTimeout.cancel(false);
			// first handle connection - that user has a connection to work, if he has only 1
			this.freeConnection();
			// removes canceled oti timeouts
			AppContext.otiScheduler.purge();
			this.scmpCallback.receive(scmpReply);
		}

		/**
		 * Handling large response.
		 * 
		 * @param scmpReply
		 *            the scmp reply
		 * @throws Exception
		 *             the exception
		 */
		private void handlingLargeResponse(SCMPMessage scmpReply) throws Exception {
			// response is a part - response is large, continue polling
			// SCMPLargeResponse handles parts of large requests, putting all together
			this.largeResponse = new SCMPCompositeReceiver(requestMsg, scmpReply);
			SCMPMessage message = largeResponse.getPart();
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
				// increment msgSequenceNr
				this.msgSequenceNr.incrementAndGetMsgSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			// updating cache part number for poll request
			Integer partNr = scmpReply.getHeaderInt(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER);
			if (partNr == null) {
				partNr = 1;
			}
			message.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, partNr);
			LOGGER.debug("handling large response using cache id = " + message.getCacheId());
			// poll & exit
			this.connectionCtx.getConnection().send(message, this);
			LOGGER.debug("handling large response after send using cache id = " + message.getCacheId());
		}

		/**
		 * Handling large request.
		 * 
		 * @param scmpReply
		 *            the scmp reply
		 * @return true, if successful
		 * @throws Exception
		 *             the exception
		 */
		private boolean handlingLargeRequest(SCMPMessage scmpReply) throws Exception {
			SCMPMessage part = null;

			part = largeRequest.getCurrentPart();
			if (part.isRequest()) {
				/*
				 * request has been sent completely. The response can be small or large, this doesn't matter, we continue reading any
				 * large response later
				 */
				return true;
			}
			if (largeRequest.hasNext() == false) {
				if (this.requestMsg.isGroup()) {
					/*
					 * client processes group call, he needs to get the response - happens in special case: client sends a single
					 * part of a group but content is to large and we need to split
					 */
					return true;
				}
				LOGGER.warn("largeRequest.hasNext() == false but part request not done");
				return true;
			}
			part = largeRequest.getNext();
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(part.getMessageType())) {
				this.msgSequenceNr.incrementAndGetMsgSequenceNr();
				part.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			this.connectionCtx.getConnection().send(part, this);
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception ex) {
			// cancel operation timeout
			this.operationTimeout.cancel(false);
			// delete composites
			this.largeResponse = null;
			this.largeRequest = null;
			this.scmpCallback.receive(ex);
			if (ex instanceof IdleTimeoutException || ex instanceof ClosedChannelException || ex instanceof DisconnectException) {
				// operation stopped - delete this specific connection, prevents race conditions
				this.disconnectConnection(false);
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
			// removes canceled oti timeouts
			AppContext.otiScheduler.purge();
		}

		/**
		 * Free connection. Orders connectionPool to give the connection free. Its not used by the requester anymore.
		 */
		private void freeConnection() {
			try {
				SCRequester.this.connectionPool.freeConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				LOGGER.error("freeConnection", e);
			}
		}

		/**
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection has a curious
		 * state.
		 * 
		 * @param quietDisconnect
		 *            the quiet disconnect
		 */
		private void disconnectConnection(boolean quietDisconnect) {
			try {
				SCRequester.this.connectionPool.forceClosingConnection(connectionCtx.getConnection(), quietDisconnect);
			} catch (Exception e) {
				LOGGER.error("disconnectConnection", e);
			}
		}

		/**
		 * Sets the operation timeout.
		 * 
		 * @param operationTimeout
		 *            the new operation timeout
		 */
		public void setOperationTimeout(ScheduledFuture<TimeoutWrapper> operationTimeout) {
			this.operationTimeout = operationTimeout;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.timeoutMillis;
		}

		/**
		 * Sets the timeout milliseconds.
		 * 
		 * @param timeoutMillis
		 *            the new timeout milliseconds
		 */
		public void setTimeoutMillis(int timeoutMillis) {
			this.timeoutMillis = timeoutMillis;
		}

		/**
		 * Operation timeout run out. Clean up. Close connection and inform upper level by callback.
		 */
		@Override
		public void timeout() {
			LOGGER.warn("oti timeout expiration in sc client API oti=" + this.timeoutMillis);
			this.disconnectConnection(true);
			try {
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.REQUEST_TIMEOUT, "Operation timeout expired on client");
				fault.setMessageType(requestMsg.getMessageType());
				this.scmpCallback.receive(fault);
			} catch (Exception e) {
				this.scmpCallback.receive(e);
			}
		}
	}

	/**
	 * Gets the sCMP msg sequence nr.
	 * 
	 * @return the sCMP msg sequence nr
	 */
	public SCMPMessageSequenceNr getSCMPMsgSequenceNr() {
		return this.msgSequenceNr;
	}
}