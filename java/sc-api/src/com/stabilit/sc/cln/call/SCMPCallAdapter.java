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
package com.stabilit.sc.cln.call;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.client.IClientSession;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPInternalStatus;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.internal.SCMPPart;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls.
 * 
 * @author JTraber
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	/** The client to used to invoke the call. */
	protected IClient client;
	/** The scmp session to use for the call. */
	protected IClientSession scmpSession;
	/** The request message. */
	protected SCMPMessage requestMessage;
	/** The response message. */
	protected SCMPMessage responseMessage;

	/**
	 * Instantiates a new scmp call adapter.
	 */
	public SCMPCallAdapter() {
		this(null, null);
	}

	public SCMPCallAdapter(IClient client) {
		this(client, client.getClientSession());
	}

	/**
	 * Instantiates a new scmp call adapter.
	 * 
	 * @param client
	 *            the client
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPCallAdapter(IClient client, IClientSession scmpSession) {
		this.client = client;
		this.scmpSession = scmpSession;

		if (this.scmpSession != null) {
			this.requestMessage = new SCMPMessage();
			this.requestMessage.setSessionId(scmpSession.getSessionId());
			this.requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmpSession.getServiceName());
		}

		if (this.requestMessage == null) {
			this.requestMessage = new SCMPMessage();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IClient client) {
		throw new UnsupportedOperationException("not allowed");
	}

	@Override
	public ISCMPCall newInstance(IClient client, IClientSession clientSession) {
		throw new UnsupportedOperationException("not allowed");
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMPMessage scmpMessage) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall openGroup() {
		ISCMPCall groupCall = new SCMPGroupCall(this);
		return groupCall;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage closeGroup() {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		this.requestMessage.setMessageType(getMessageType().getRequestName());
		this.responseMessage = client.sendAndReceive(this.requestMessage);

		if (this.responseMessage.isFault()) {
			throw new SCMPCallException((SCMPFault) responseMessage);
		}
		return this.responseMessage;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getRequest() {
		return requestMessage;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getResponse() {
		return responseMessage;
	}

	/** {@inheritDoc} */
	public void setRequestBody(Object obj) {
		requestMessage.setBody(obj);
	}

	/**
	 * Sets the compression.
	 * 
	 * @param compression
	 *            the new compression
	 */
	public void setCompression(boolean compression) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}

	/**
	 * The Class SCMPGroupCall. Inner class - allows to open a group for messages. Sends parts as long as group is
	 * not closed. Very useful if you don't know how big your complete message is going to be.
	 */
	public class SCMPGroupCall implements ISCMPCall {	//TODO (TRN) is this not better a SCMPCallPart?

		/** The parent call. */
		private ISCMPCall parentCall;
		/** The group state. */
		private SCMPGroupState groupState;

		/**
		 * Instantiates a new sCMP group call.
		 * 
		 * @param parentCall
		 *            the parent call
		 */
		private SCMPGroupCall(ISCMPCall parentCall) {
			this.parentCall = parentCall;
			this.groupState = SCMPGroupState.OPEN;
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage invoke() throws Exception {
			if (this.groupState == SCMPGroupState.CLOSE) {
				throw new SCMPCallException("group is closed");
			}
			SCMPMessage callSCMP = this.parentCall.getRequest();
			SCMPCallAdapter.this.requestMessage.setInternalStatus(SCMPInternalStatus.GROUP);

			if (callSCMP.isLargeMessage()) {
				// parent call is large no need to change anything
				SCMPMessage message = this.parentCall.invoke();
				return message;
			}
			if (callSCMP.isPart() == false) {
				// callSCMP is small and not part but inside a group only parts are allowed
				SCMPPart scmpPart = new SCMPPart();
				scmpPart.setHeader(callSCMP);
				scmpPart.setBody(callSCMP.getBody());
				SCMPCallAdapter.this.requestMessage = scmpPart; // SCMPCallAdapter.this points to this.parentCall
				callSCMP = null;
			}
			SCMPMessage message = this.parentCall.invoke();
			return message;
		}

		/** {@inheritDoc} */
		@Override
		public void setRequestBody(Object body) {
			this.parentCall.setRequestBody(body);
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage closeGroup() throws Exception {
			this.groupState = SCMPGroupState.CLOSE;
			// send empty closing REQ
			SCMPMessage message = new SCMPMessage();
			message.setHeader(SCMPCallAdapter.this.requestMessage);
			message.setBody(null);
			message.setInternalStatus(SCMPInternalStatus.GROUP);
			SCMPCallAdapter.this.requestMessage = message;
			SCMPMessage result = this.parentCall.invoke();
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall openGroup() {
			throw new UnsupportedOperationException("not allowed");
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMsgType getMessageType() {
			return parentCall.getMessageType();
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage getRequest() {
			return this.parentCall.getRequest();
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage getResponse() {
			return this.parentCall.getResponse();
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall newInstance(IClient client) {
			throw new UnsupportedOperationException("not allowed");
		}

		@Override
		public ISCMPCall newInstance(IClient client, IClientSession clientSession) {
			throw new UnsupportedOperationException("not allowed");
		}

		@Override
		public ISCMPCall newInstance(IClient client, SCMPMessage scmpMessage) {
			throw new UnsupportedOperationException("not allowed");
		}
	}

	/**
	 * The Enum SCMPGroupState.
	 */
	private static enum SCMPGroupState {
		/** The OPEN of a group. */
		OPEN,
		/** The CLOSE of a group. */
		CLOSE;
	}
}