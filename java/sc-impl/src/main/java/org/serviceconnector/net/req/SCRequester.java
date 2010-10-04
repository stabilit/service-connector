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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.connection.IConnectionContext;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.util.ITimerRun;
import org.serviceconnector.util.TimerTaskWrapper;

/**
 * The Class Requester. Implements a general behavior of a requester in the context of the Client or the Server. Defines
 * how to connect/disconnect, send/receive has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCRequester.class);
	/** The Constant timer, triggers all operation timeout for sending. */
	protected final static Timer timer = new Timer("OperationTimerSCRequester");
	/** The context. */
	protected IRequesterContext reqContext;

	/**
	 * Instantiates a new requester.
	 * 
	 * @param context
	 *            the context
	 */
	public SCRequester(IRequesterContext outerContext) {
		this.reqContext = outerContext;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, int timeoutInMillis, ISCMPCallback scmpCallback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		SCMPMessageId msgId = this.reqContext.getSCMPMessageId();

		ISCMPCallback requesterCallback = null;
		// differ if message is large or not, sending procedure is different
		if (message.isLargeMessage()) {
			// SCMPCompositeSender handles splitting, works like an iterator
			SCMPCompositeSender compositeSender = new SCMPCompositeSender(message);
			requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, compositeSender,
					msgId);
			// setting up operation timeout after successful send
			TimerTask task = new TimerTaskWrapper((ITimerRun) requesterCallback);
			SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
			reqCallback.setOperationTimeoutTask(task);
			reqCallback.setTimeoutMillis(timeoutInMillis);
			timer.schedule(task, (long) timeoutInMillis);
			// extract first part message & send
			SCMPMessage part = compositeSender.getFirst();
			// handling messageId
			if (SCMPMessageId.necessaryToWrite(part.getMessageType())) {
				msgId.incrementPartSequenceNr();
				part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			}
			// send
			connection.send(part, requesterCallback);
		} else {
			requesterCallback = new SCRequesterSCMPCallback(message, scmpCallback, connectionContext, msgId);
			// setting up operation timeout after successful send
			TimerTask task = new TimerTaskWrapper((ITimerRun) requesterCallback);
			SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
			reqCallback.setOperationTimeoutTask(task);
			reqCallback.setTimeoutMillis(timeoutInMillis);
			timer.schedule(task, (long) timeoutInMillis);
			if (message.isGroup()) {
				// increment messageId in case of group call
				msgId.incrementPartSequenceNr();
			}
			// handling messageId
			if (SCMPMessageId.necessaryToWrite(message.getMessageType())) {
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			}
			// process send
			connection.send(message, requesterCallback);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IRequesterContext getContext() {
		return reqContext;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	/**
	 * The Class RequesterSCMPCallback. Component used for asynchronous communication. It gets informed at the time a
	 * reply is received. Handles freeing up earlier requested connections. Provides functionality to deal with large
	 * messages.
	 */
	private class SCRequesterSCMPCallback implements ISCMPCallback, ITimerRun {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPCallback scmpCallback;
		/** The connection context. */
		private IConnectionContext connectionCtx;
		/** The request message, initial message sent by requester. */
		private SCMPMessage requestMsg;
		/** The composite receiver. */
		private SCMPCompositeReceiver compositeReceiver;
		/** The composite sender. */
		private SCMPCompositeSender compositeSender;
		/** The message id. */
		private SCMPMessageId msgId;
		/** The operation timeout task. */
		private TimerTask operationTimeoutTask;
		/** The timeout in milliseconds. */
		private int timeoutInMillis;

		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPMessageId msgId) {
			this(reqMsg, scmpCallback, conCtx, null, msgId);
		}

		public SCRequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPCompositeSender compositeSender, SCMPMessageId msgId) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = conCtx;
			this.requestMsg = reqMsg;
			this.compositeSender = compositeSender;
			this.msgId = msgId;
			this.timeoutInMillis = 0;
			this.operationTimeoutTask = null;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// ------------------- handling large request --------------------
			if (compositeSender != null) {
				// handle large messages
				boolean largeRequestDone = this.handlingLargeRequest(scmpReply);

				if (largeRequestDone == false) {
					// large request is not done yet - wait for other PRS messages
					return;
				}

				this.compositeSender = null;
				if (scmpReply.isPart() && this.requestMsg.isGroup() == false) {
					// response is a part - response is large, continue pulling
					// delete compositeSender - large request done!
					this.msgId.incrementMsgSequenceNr();
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
			if (this.compositeReceiver != null) {
				// large response message is processing - continue procedure
				this.compositeReceiver.add(scmpReply);
				if (scmpReply.isPart() == false) {
					// response received
					// cancel operation timeout
					operationTimeoutTask.cancel();
					// first handle connection - that user has a connection to work, if he has only 1
					this.freeConnection();
					this.scmpCallback.callback(this.compositeReceiver);
					// delete compositeReceiver - large response done!
					this.compositeReceiver = null;
					return;
				}
				SCMPMessage message = compositeReceiver.getPart();
				// pull & exit
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
				// handling large response & messageId
				this.msgId.incrementMsgSequenceNr();
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
			// response is a part - response is large, continue pulling
			// SCMPComposite handles parts of large requests, putting all together
			this.compositeReceiver = new SCMPCompositeReceiver(requestMsg, scmpReply);
			SCMPMessage message = compositeReceiver.getPart();
			// handling messageId
			if (SCMPMessageId.necessaryToWrite(message.getMessageType())) {
				// increment part number in case of large response
				this.msgId.incrementPartSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			}
			// pull & exit
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

			part = compositeSender.getCurrentPart();
			if (part.isRequest()) {
				/*
				 * request has been sent completely. The response can be small or large, this doesn't matter, we
				 * continue reading any large response later
				 */
				return true;
			}
			if (compositeSender.hasNext() == false) {
				if (this.requestMsg.isGroup()) {
					/*
					 * client processes group call, he needs to get the response - happens in special case: client sends
					 * a single part of a group but content is to large and we need to split
					 */
					return true;
				}
				logger.warn("compositeSender.hasNext() == false but part request not done");
				return true;
			}
			part = compositeSender.getNext();
			// handling messageId
			if (SCMPMessageId.necessaryToWrite(part.getMessageType())) {
				if (compositeSender.hasNext() == false) {
					// last part to send - will be a REQ message, increment message number
					this.msgId.incrementMsgSequenceNr();
				} else {
					// there are more parts to complete request - just increment part number
					this.msgId.incrementPartSequenceNr();
				}
				part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
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
			this.compositeReceiver = null;
			this.compositeSender = null;
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
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection
		 * has a curious state.
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
			SCMPFault fault = new SCMPFault(SCMPError.REQUEST_TIMEOUT, "getting message took too long");
			try {
				this.scmpCallback.callback(fault);
			} catch (Exception e) {
				this.scmpCallback.callback(e);
			}
		}
	}
}