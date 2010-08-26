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
package com.stabilit.scm.common.net.req;

import java.util.Timer;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.req.netty.IdleTimeoutException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;
import com.stabilit.scm.common.util.ITimerRun;
import com.stabilit.scm.common.util.TimerTaskWrapper;

/**
 * The Class Requester. Implements a general behavior of a requester. Defines how to connect/disconnect, send/receive
 * has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class Requester implements IRequester {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(Requester.class);
	/** The Constant timer, triggers all operation timeout for sending. */
	protected final static Timer timer = new Timer("OperationTimer");
	/** The context. */
	protected IRequesterContext reqContext;

	/**
	 * Instantiates a new requester.
	 * 
	 * @param context
	 *            the context
	 */
	public Requester(IRequesterContext outerContext) {
		this.reqContext = outerContext;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, int timeoutInSeconds, ISCMPCallback scmpCallback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		SCMPMessageId msgId = this.reqContext.getSCMPMessageId();

		ISCMPCallback requesterCallback = null;
		// differ if message is large or not, sending procedure is different
		if (message.isLargeMessage()) {
			// SCMPCompositeSender handles splitting, works like an iterator
			SCMPCompositeSender compositeSender = new SCMPCompositeSender(message);
			requesterCallback = new RequesterSCMPCallback(message, scmpCallback, connectionContext, compositeSender,
					msgId);
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
			requesterCallback = new RequesterSCMPCallback(message, scmpCallback, connectionContext, msgId);
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
		// setting up operation timeout after successful send
		TimerTaskWrapper task = new TimerTaskWrapper((ITimerRun) requesterCallback);
		RequesterSCMPCallback reqCallback = (RequesterSCMPCallback) requesterCallback;
		reqCallback.setOperationTimeoutTask(task);
		reqCallback.setTimeoutSeconds(timeoutInSeconds);
		timer.schedule(task, timeoutInSeconds * Constants.SEC_TO_MILISEC_FACTOR);
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
	private class RequesterSCMPCallback implements ISCMPCallback, ITimerRun {

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
		private TimerTaskWrapper operationTimeoutTask;
		/** The timeout in seconds. */
		private int timeoutInSeconds;

		public RequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPMessageId msgId) {
			this(reqMsg, scmpCallback, conCtx, null, msgId);
		}

		public RequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPCompositeSender compositeSender, SCMPMessageId msgId) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = conCtx;
			this.requestMsg = reqMsg;
			this.compositeSender = compositeSender;
			this.msgId = msgId;
			this.timeoutInSeconds = 0;
			this.operationTimeoutTask = null;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// cancel operation timeout
			operationTimeoutTask.cancel();
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
				// incoming message is a part groupCall is made by client - part
				// response can be ignored
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
				LoggerPoint.getInstance()
						.fireWarn(this, "compositeSender.hasNext() == false but part request not done");
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
			if (ex instanceof IdleTimeoutException) {
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
				Requester.this.reqContext.getConnectionPool().freeConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				logger.error("freeConnection "+e.getMessage(), e);
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		/**
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection
		 * has a curious state.
		 */
		private void disconnectConnection() {
			try {
				Requester.this.reqContext.getConnectionPool().forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				logger.error("disconnectConnection "+e.getMessage(), e);
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		/**
		 * Sets the operation timeout task.
		 * 
		 * @param operationTimeoutTask
		 *            the new operation timeout task
		 */
		public void setOperationTimeoutTask(TimerTaskWrapper operationTimeoutTask) {
			this.operationTimeoutTask = operationTimeoutTask;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutSeconds() {
			return this.timeoutInSeconds;
		}

		/**
		 * Sets the timeout seconds.
		 * 
		 * @param timeoutInSeconds
		 *            the new timeout seconds
		 */
		public void setTimeoutSeconds(int timeoutInSeconds) {
			this.timeoutInSeconds = timeoutInSeconds;
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