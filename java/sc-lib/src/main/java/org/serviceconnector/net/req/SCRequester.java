/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPLargeRequest;
import org.serviceconnector.scmp.SCMPLargeResponse;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.util.ITimerRun;
import org.serviceconnector.util.TimerTaskWrapper;

/**
 * The Class Requester. Implements a general behavior of a requester in the context of the Client or the Server. Defines how to
 * connect/disconnect, send/receive has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCRequester.class);
	/** The Constant timer, triggers all operation timeout for sending. */
	protected final static Timer timer = new Timer("OperationTimerSCRequester");
	/** The context. */
	protected RequesterContext reqContext;

	/**
	 * Instantiates a new requester.
	 * 
	 * @param context
	 *            the context
	 */
	public SCRequester(RequesterContext outerContext) {
		this.reqContext = outerContext;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, int timeoutInMillis, ISCMPCallback scmpCallback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		ConnectionContext connectionContext = connection.getContext();

		SCMPMessageSequenceNr msgSequenceNr = this.reqContext.getSCMPMsgSequenceNr();
		try {
			ISCMPCallback requesterCallback = null;
			// differ if message is large or not, sending procedure is different
			if (message.isLargeMessage()) {
				// SCMPLargeRequest handles splitting, works like an iterator
				SCMPLargeRequest largeResponse = new SCMPLargeRequest(message);
				requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, largeResponse,
						msgSequenceNr);
				// setting up operation timeout after successful send
				TimerTask task = new TimerTaskWrapper((ITimerRun) requesterCallback);
				SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
				reqCallback.setOperationTimeoutTask(task);
				reqCallback.setTimeoutMillis(timeoutInMillis);
				timer.schedule(task, (long) timeoutInMillis);
				// extract first part message & send
				SCMPMessage part = largeResponse.getFirst();
				// handling msgSequenceNr
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					msgSequenceNr.incrementMsgSequenceNr();
					part.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				// send
				connection.send(part, requesterCallback);
			} else {
				requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, msgSequenceNr);
				// setting up operation timeout after successful send
				TimerTask task = new TimerTaskWrapper((ITimerRun) requesterCallback);
				SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
				reqCallback.setOperationTimeoutTask(task);
				reqCallback.setTimeoutMillis(timeoutInMillis);
				timer.schedule(task, (long) timeoutInMillis);
				// handling msgSequenceNr
				if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
					msgSequenceNr.incrementMsgSequenceNr();
					message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
				}
				// process send
				connection.send(message, requesterCallback);
			}
		} catch (Exception ex) {
			this.reqContext.getConnectionPool().freeConnection(connection);
			throw ex;
		}
	}

	/** {@inheritDoc} */
	@Override
	public RequesterContext getContext() {
		return reqContext;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	/**
	 * The Class RequesterSCMPCallback. Component used for asynchronous communication. It gets informed at the time a reply is
	 * received. Handles freeing up earlier requested connections. Provides functionality to deal with large messages.
	 */
	private class SCRequesterSCMPCallback implements ISCMPCallback, ITimerRun {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPCallback scmpCallback;
		/** The connection context. */
		private ConnectionContext connectionCtx;
		/** The request message, initial message sent by requester. */
		private SCMPMessage requestMsg;
		/** The large response. */
		private SCMPLargeResponse largeResponse;
		/** The large request. */
		private SCMPLargeRequest largeRequest;
		/** The msgSequenceNr. */
		private SCMPMessageSequenceNr msgSequenceNr;
		/** The operation timeout task. */
		private TimerTask operationTimeoutTask;
		/** The timeout in milliseconds. */
		private int timeoutInMillis;

		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, ConnectionContext conCtx,
				SCMPMessageSequenceNr msgSequenceNr) {
			this(reqMsg, scmpCallback, conCtx, null, msgSequenceNr);
		}

		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, ConnectionContext conCtx,
				SCMPLargeRequest largeRequest, SCMPMessageSequenceNr msgSequenceNr) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = conCtx;
			this.requestMsg = reqMsg;
			this.largeRequest = largeRequest;
			this.msgSequenceNr = msgSequenceNr;
			this.timeoutInMillis = 0;
			this.operationTimeoutTask = null;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// ------------------- handling large request --------------------
			if (largeRequest != null) {
				// handle large messages
				boolean largeRequestDone = this.handlingLargeRequest(scmpReply);

				if (largeRequestDone == false) {
					// large request is not done yet - wait for other PRS messages
					return;
				}

				this.largeRequest = null;
				if (scmpReply.isPart() && this.requestMsg.isGroup() == false) {
					// response is a part - response is large, continue polling
					this.handlingLargeResponse(scmpReply);
					return;
				}
				// cancel operation timeout
				operationTimeoutTask.cancel();
				// first handle connection - that user has a connection to work, if he has only 1
				this.freeConnection();
				this.scmpCallback.callback(scmpReply);
				return;
			}

			// ------------------- handling large response -------------------
			if (this.largeResponse != null) {
				// large response message is processing - continue procedure
				this.largeResponse.add(scmpReply);
				if (scmpReply.isPart() == false) {
					// response received
					// cancel operation timeout
					operationTimeoutTask.cancel();
					// first handle connection - that user has a connection to work, if he has only 1
					this.freeConnection();
					this.scmpCallback.callback(this.largeResponse);
					// delete compositeReceiver - large response done!
					this.largeResponse = null;
					return;
				}
				SCMPMessage message = largeResponse.getPart();
				// poll & exit
				this.connectionCtx.getConnection().send(message, this);
				return;
			}

			if (requestMsg.isPart()) {
				// incoming message is a part groupCall is made by client - part response can be ignored
				// cancel operation timeout
				operationTimeoutTask.cancel();
				// first handle connection - that user has a connection to work, if he has only 1
				this.freeConnection();
				this.scmpCallback.callback(scmpReply);
				return;
			}

			if (scmpReply.isPart()) {
				// handling large response
				this.handlingLargeResponse(scmpReply);
				return;
			}
			// cancel operation timeout
			operationTimeoutTask.cancel();
			// first handle connection - that user has a connection to work, if he has only 1
			this.freeConnection();
			this.scmpCallback.callback(scmpReply);
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
			this.largeResponse = new SCMPLargeResponse(requestMsg, scmpReply);
			SCMPMessage message = largeResponse.getPart();
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
				// increment msgSequenceNr
				this.msgSequenceNr.incrementMsgSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			// poll & exit
			this.connectionCtx.getConnection().send(message, this);
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
				// TODO @JAN .. what warning should be here
				logger.warn("compositeSender.hasNext() == false but part request not done");
				return true;
			}
			part = largeRequest.getNext();
			// handling msgSequenceNr
			if (SCMPMessageSequenceNr.necessaryToWrite(part.getMessageType())) {
				this.msgSequenceNr.incrementMsgSequenceNr();
				part.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			this.connectionCtx.getConnection().send(part, this);
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Exception ex) {
			// cancel operation timeout
			this.operationTimeoutTask.cancel();
			// delete composites
			this.largeResponse = null;
			this.largeRequest = null;
			this.scmpCallback.callback(ex);
			if (ex instanceof IdleTimeoutException || ex instanceof ClosedChannelException) {
				// operation timed out - delete this specific connection, prevents race conditions
				this.disconnectConnection();
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
		}

		/**
		 * Free connection. Orders connectionPool to give the connection free. Its not used by the requester anymore.
		 */
		private void freeConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().freeConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				logger.error("freeConnection", e);
			}
		}

		/**
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection has a curious
		 * state.
		 */
		private void disconnectConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				logger.error("disconnectConnection", e);
			}
		}

		/**
		 * Sets the operation timeout task.
		 * 
		 * @param operationTimeoutTask
		 *            the new operation timeout task
		 */
		public void setOperationTimeoutTask(TimerTask operationTimeoutTask) {
			this.operationTimeoutTask = operationTimeoutTask;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.timeoutInMillis;
		}

		/**
		 * Sets the timeout milliseconds.
		 * 
		 * @param timeoutInMillis
		 *            the new timeout milliseconds
		 */
		public void setTimeoutMillis(int timeoutInMillis) {
			this.timeoutInMillis = timeoutInMillis;
		}

		/**
		 * Operation timeout run out. Clean up. Close connection and inform upper level by callback.
		 */
		@Override
		public void timeout() {
			this.disconnectConnection();
			try {
				SCMPFault fault = new SCMPFault(SCMPError.REQUEST_TIMEOUT, "OTI run out on client");
				fault.setMessageType(requestMsg.getMessageType());
				this.scmpCallback.callback(fault);
			} catch (Exception e) {
				this.scmpCallback.callback(e);
			}
		}
	}
}