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

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.req.IConnection;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.IRequesterContext;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class Requester. Implements a general behavior of a requester. Defines how to connect/disconnect, send/receive
 * has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class Requester implements IRequester {

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

	@Override
	public void send(SCMPMessage message, ISCMPCallback scmpCallback) throws Exception {
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
			msgId.incrementPartSequenceNr();
			part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			connection.send(part, requesterCallback);
		} else {
			requesterCallback = new RequesterSCMPCallback(message, scmpCallback, connectionContext, msgId);
			if (message.isGroup()) {
				// increment messageId in case of group call
				msgId.incrementPartSequenceNr();
			}
			message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			// process send and receive
			connection.send(message, requesterCallback);
		}
	}

	public IContext getContext() {
		return reqContext;
	}

	/**
	 * To hash code string.
	 * 
	 * @return the string {@inheritDoc}
	 */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	private class RequesterSCMPCallback implements ISCMPCallback {
		private ISCMPCallback scmpCallback;
		private IConnectionContext connectionCtx;
		private SCMPMessage requestMsg;
		private SCMPCompositeReceiver compositeReceiver;
		private SCMPCompositeSender compositSender;
		private SCMPMessageId msgId;

		public RequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPMessageId msgId) {
			this(reqMsg, scmpCallback, conCtx, null, msgId);
		}

		public RequesterSCMPCallback(SCMPMessage reqMsg, ISCMPCallback scmpCallback, IConnectionContext conCtx,
				SCMPCompositeSender compositeSender, SCMPMessageId msgId) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = conCtx;
			this.requestMsg = reqMsg;
			this.compositSender = compositeSender;
			this.msgId = msgId;
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {

			// ------------------- handling large request --------------------
			if (compositSender != null) {
				// handle large messages
				boolean largeRequestDone = this.handlingLargeRequest(scmpReply);

				if (largeRequestDone == false) {
					// large request is not done yet - wait for other PRS messages
					return;
				}

				this.compositSender = null;
				if (scmpReply.isPart() && this.requestMsg.isGroup() == false) {
					// response is a part - response is large, continue pulling
					// delete compositeSender - large request done!
					this.msgId.incrementMsgSequenceNr();
					this.handlingLargeResponse(scmpReply);
					return;
				}
				this.scmpCallback.callback(scmpReply);
				return;
			}

			// ------------------- handling large response -------------------
			if (this.compositeReceiver != null) {
				// large response message is processing - continue procedure
				this.compositeReceiver.add(scmpReply);
				if (scmpReply.isPart() == false) {
					// response received
					this.scmpCallback.callback(this.compositeReceiver);
					// delete compositeReceiver - large response done!
					this.compositeReceiver = null;
					return;
				}
				SCMPMessage message = compositeReceiver.getPart();
				this.connectionCtx.getConnection().send(message, this); // pull & exit
				return;
			}

			if (requestMsg.isPart()) {
				// incoming message is a part groupCall is made by client - part
				// response can be ignored
				this.scmpCallback.callback(scmpReply);
				return;
			}

			if (scmpReply.isPart()) {
				// handling large response & messageId
				this.msgId.incrementMsgSequenceNr();
				this.handlingLargeResponse(scmpReply);
				return;
			}
			this.scmpCallback.callback(scmpReply);
			// free connection after callback returns - prevents using the same connection right away, race conditions
			this.freeConnection();
		}

		private void handlingLargeResponse(SCMPMessage scmpReply) throws Exception {
			// response is a part - response is large, continue pulling
			// SCMPComposite handles parts of large requests, putting all together
			this.compositeReceiver = new SCMPCompositeReceiver(requestMsg, scmpReply);
			SCMPMessage message = compositeReceiver.getPart();
			// increment part number in case of large response
			this.msgId.incrementPartSequenceNr();
			message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			this.connectionCtx.getConnection().send(message, this); // pull & exit
		}

		private boolean handlingLargeRequest(SCMPMessage scmpReply) throws Exception {
			SCMPMessage part = null;

			part = compositSender.getCurrentPart();
			if (part.isRequest()) {
				/*
				 * request has been sent completely. The response can be small or large, this doesn't matter, we
				 * continue reading any large response later
				 */
				return true;
			}
			if (compositSender.hasNext() == false) {
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
			part = compositSender.getNext();
			if (compositSender.hasNext() == false) {
				// last part to send - will be a REQ message, increment message number
				this.msgId.incrementMsgSequenceNr();
			} else {
				// there are more parts to complete request - just increment part number
				this.msgId.incrementPartSequenceNr();
			}
			part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgId.getCurrentMessageID());
			this.connectionCtx.getConnection().send(part, this);
			return false;
		}

		@Override
		public void callback(Throwable th) {
			// delete composites
			this.compositeReceiver = null;
			this.compositSender = null;
			this.scmpCallback.callback(th);
			if (th instanceof OperationTimeoutException) {
				// operation timed out - delete this specific connection, prevents race conditions
				this.disconnectConnection();
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
		}

		private void freeConnection() {
			try {
				Requester.this.reqContext.getConnectionPool().freeConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		private void disconnectConnection() {
			try {
				Requester.this.reqContext.getConnectionPool().forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}
}
