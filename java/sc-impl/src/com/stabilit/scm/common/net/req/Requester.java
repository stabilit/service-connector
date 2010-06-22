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

import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.listener.RuntimePoint;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageID;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class Requester. Implements a general behavior of a requester. Defines how to connect/disconnect, send/receive
 * has to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class Requester implements IRequester {

	/** The requester configuration. */
	private ICommunicatorConfig reqConfig;
	/** The req connection. */
	protected IConnection connection;
	/** The msg id for the next request. */
	private SCMPMessageID msgID;

	/**
	 * Instantiates a new requester.
	 */
	public Requester() {
		msgID = new SCMPMessageID();
	}

	/** {@inheritDoc} */
	@Override
	public void connect() throws Exception {
		this.connection = ConnectionPool.useConnection(this.reqConfig);  // return an already connected live instance		
	}

	@Override
	public SCMPMessage sendAndReceive(SCMPMessage message) throws Exception {

		try {
			PerformancePoint.getInstance().fireBegin(this, "sendAndReceive");
			this.connect(); // from pool
			SCMPMessage ret = null;
			// differ if message is large or not, sending procedure is different
			if (message.isLargeMessage()) {
				ret = sendLargeSCMPAndReceive(message);
			} else {
				ret = sendSmallSCMPAndReceive(message);
			}
			return ret;
		} finally {
			this.disconnect(); // give back to pool
			PerformancePoint.getInstance().fireEnd(this, "sendAndReceive");
		}
	}

	/**
	 * request is small but response could be small or large.
	 * 
	 * @param message
	 *            the scmp
	 * @return the SCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMPMessage sendSmallSCMPAndReceive(SCMPMessage message) throws Exception {
		if (message.isGroup()) {
			msgID.incrementPartSequenceNr();
		}
		message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		// process send and receive
		SCMPMessage ret = connection.sendAndReceive(message);

		if (message.isPart()) {
			// incoming message is a part groupCall is made by client - part response can be ignored
			return ret;
		}

		if (ret.isPart()) {
			// response is a part - response is large, continue pulling
			return receiveLargeResponse(message, (SCMPMessage) ret);
		}
		msgID.incrementMsgSequenceNr();
		return ret;
	}

	/**
	 * request is large, response could be small or large.
	 * 
	 * @param scmp
	 *            the scmp message
	 * @return the SCMPMessage
	 * @throws Exception
	 *             the exception
	 */
	private SCMPMessage sendLargeSCMPAndReceive(SCMPMessage scmp) throws Exception {
		SCMPMessage ret = this.sendLargeSCMP(scmp); // send large request scmp

		if (ret.isPart() && scmp.isGroup() == false) {
			// response is a part - response is large, continue pulling
			ret = receiveLargeResponse(scmp, (SCMPMessage) ret);
		}
		if (scmp.isGroup() == false) {
			msgID.incrementMsgSequenceNr();
		}
		return ret;
	}

	/**
	 * Sends large scmp.
	 * 
	 * @param scmp
	 *            the scmp message
	 * @return the scmp message
	 * @throws Exception
	 *             the exception
	 */
	private SCMPMessage sendLargeSCMP(SCMPMessage scmp) throws Exception {
		// SCMPLargeRequest handles splitting, works like an iterator
		SCMPCompositeSender scmpLargeRequest = new SCMPCompositeSender(scmp);
		SCMPMessage part = scmpLargeRequest.getFirst();
		msgID.incrementPartSequenceNr();
		while (part != null) {
			part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			SCMPMessage ret = connection.sendAndReceive(part);

			if (part.isRequest()) {
				/*
				 * request has been sent completely. The response can be small or large, this doesn't matter, we
				 * continue reading any large response later
				 */
				return ret;
			}
			if (scmpLargeRequest.hasNext() == false) {
				if (scmp.isGroup()) {
					/*
					 * client processes group call, he needs to get the response - happens in special case: client sends
					 * a single part of a group but content is to large and we need to split
					 */
					return ret;
				}
				RuntimePoint.getInstance().fireRuntime(this,
						"sendLargeRequest.hasNext() == false but part request not done");
				return null;
			}
			part = scmpLargeRequest.getNext();
			msgID.incrementPartSequenceNr();
		}
		return null;
	}

	/**
	 * Receive large response.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMPMessage receiveLargeResponse(SCMPMessage request, SCMPMessage response) throws Exception {
		// SCMPComposite handles parts of large requests, putting all together
		SCMPCompositeReceiver scmpComposite = new SCMPCompositeReceiver(request, response);
		SCMPMessage ret = null;
		msgID.incrementMsgSequenceNr();
		msgID.incrementPartSequenceNr();
		while (true) {
			SCMPMessage message = scmpComposite.getPart();
			message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			ret = connection.sendAndReceive(message); // pull

			if (ret == null) {
				return ret;
			}

			if (ret.isFault()) {
				// response is fault stop receiving
				return ret;
			}
			scmpComposite.add(ret);
			if (ret.isPart() == false) {
				// response received
				break;
			}
			msgID.incrementPartSequenceNr();
		}
		return scmpComposite;
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() throws Exception {
		this.connection.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect() throws Exception {
		ConnectionPool.freeConnection(this.connection);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConnected() {
		return this.connection.isConnected();
	}

	/** {@inheritDoc} */
	@Override
	public void setRequesterConfig(ICommunicatorConfig reqConfig) {
		this.reqConfig = reqConfig;
	}

	/** {@inheritDoc} */
	@Override
	public ICommunicatorConfig getRequesterConfig() {
		return this.reqConfig;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}
}
